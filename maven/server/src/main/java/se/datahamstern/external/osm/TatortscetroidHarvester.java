package se.datahamstern.external.osm;

import com.sleepycat.persist.EntityCursor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Kommun;
import se.datahamstern.domain.Koordinat;
import se.datahamstern.domain.Ort;
import se.datahamstern.event.Event;
import se.datahamstern.event.JsonEventLogWriter;
import se.kodapan.geography.domain.CoordinateImpl;
import se.kodapan.geography.domain.EnvelopeImpl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.*;

/**
 * @author kalle
 * @since 2012-05-24 16:10
 */
public class TatortscetroidHarvester {

  public static void main(String[] args) throws Exception {

    Datahamstern.getInstance().open();

    TatortscetroidHarvester extractor = new TatortscetroidHarvester();


    JsonEventLogWriter logWriter = new JsonEventLogWriter(new File("/tmp/" + System.currentTimeMillis() + ".osm-tatortscentroid.log.json"));

    Source source = new Source();
    source.setAuthor("OpenStreetMap.org");
    source.setLicense("Data Copyright OpenStreetMap Contributors, Some Rights Reserved. CC-BY-SA 2.0.");
    source.setDetails(null);
    source.setTimestamp(new Date());
    source.setTrustworthiness(0.3f);


    try {
      EntityCursor<Ort> cursor = DomainStore.getInstance().getOrtByTätortskod().entities();
      try {
        Ort ort;

        while ((ort = cursor.next()) != null) {
          Koordinat centroid = extractor.extract(ort);
          if (centroid == null) {
            Nop.breakpoint();
          } else {

            StringBuilder jsonData = new StringBuilder(4096);
            jsonData.append("{\"tätortskod\":\"").append(ort.getTätortskod().get()).append("\"");
            jsonData.append(",\"latitud\":").append(String.valueOf(centroid.getLatitude()));
            jsonData.append(",\"longitud\":").append(String.valueOf(centroid.getLongitude())).append("}");


            Event event = new Event();
            event.setSources(new ArrayList<Source>());
            event.getSources().add(source);
            event.setCommandName(TatortscentroidCommand.COMMAND_NAME);
            event.setCommandVersion(TatortscentroidCommand.COMMAND_VERSION);
            event.setIdentity(UUID.randomUUID().toString());
            event.setJsonData(jsonData.toString());

            logWriter.consume(event);

            Nop.breakpoint();
          }
        }
      } finally {
        cursor.close();
      }

      logWriter.close();

    } finally {
      Datahamstern.getInstance().close();
    }

  }

  private DOMParser p = new DOMParser();
  private XPath xpath = XPathFactory.newInstance().newXPath();
  private XPathExpression nodeExpression = xpath.compile("//node");
  private XPathExpression idExpression = xpath.compile("@id");
  private XPathExpression latitudeExpression = xpath.compile("@lat");
  private XPathExpression longitudeExpression = xpath.compile("@lon");

  private HttpClient httpClient = new DefaultHttpClient();

  public TatortscetroidHarvester() throws Exception {
  }

  public Koordinat extract(Ort ort) throws Exception {

    StringBuilder overpassQuery = new StringBuilder();

    overpassQuery.append("<osm-script timeout=\"900\">\n");
    overpassQuery.append("  <query into=\"_\" type=\"node\">\n");
    overpassQuery.append("    <has-kv k=\"ref:se:scb\" modv=\"\" v=\"").append(ort.getTätortskod().get()).append("\"/>\n");
    overpassQuery.append("  </query>\n");
    overpassQuery.append("  <print/>\n");
    overpassQuery.append("</osm-script>\n");


    HttpPost post = new HttpPost("http://www.overpass-api.de/api/interpreter");
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    nameValuePairs.add(new BasicNameValuePair("data", overpassQuery.toString()));
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    HttpResponse response = httpClient.execute(post);

    StringWriter buffer = new StringWriter();
    IOUtils.copy(response.getEntity().getContent(), buffer);

    p.parse(new InputSource(new StringReader(buffer.toString())));

    NodeList nodes = (NodeList) nodeExpression.evaluate(p.getDocument(), XPathConstants.NODESET);

    if (nodes.getLength() == 0) {
      return null;
    } else if (nodes.getLength() > 1) {

      EnvelopeImpl envelope = new EnvelopeImpl();

      List<String> nodeIds = new ArrayList<String>();

      for (int i = 0; i < nodes.getLength(); i++) {
        org.w3c.dom.Node node = nodes.item(i);
        CoordinateImpl coordinate = new CoordinateImpl();
        coordinate.setLatitude(Double.valueOf(latitudeExpression.evaluate(node)));
        coordinate.setLongitude(Double.valueOf(longitudeExpression.evaluate(node)));
        envelope.addBounds(coordinate);
        nodeIds.add(idExpression.evaluate(node));
      }
      double maximumCoordinateDistancesKilometers = envelope.getNortheast().arcDistance(envelope.getSouthwest());

      int maximumDistanceMeters = (int) (maximumCoordinateDistancesKilometers * 1000);

      // todo use centroid, set low trustworthiness based on distance
      return null;
    } else {
      org.w3c.dom.Node node = nodes.item(0);
      Koordinat koordinat = new Koordinat();
      koordinat.setLatitude(Double.valueOf(latitudeExpression.evaluate(node)));
      koordinat.setLongitude(Double.valueOf(longitudeExpression.evaluate(node)));

      return koordinat;
    }
  }

}
