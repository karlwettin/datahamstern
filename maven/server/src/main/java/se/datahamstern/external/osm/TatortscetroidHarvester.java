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

    FileOutputStream fos = new FileOutputStream(System.currentTimeMillis() + "-tatorter.wiki.txt");
    OutputStreamWriter osw = new OutputStreamWriter(fos);
    PrintWriter pw = new PrintWriter(osw);

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

//        while ((ort = cursor.next()) != null) {
//          if (ort.getTätortskod().get().equals("0232")) {
//            break;
//          }
//        }

        while ((ort = cursor.next()) != null) {
          if (ort.getTätortskod().get() == null) {
            continue;
          }
          Koordinat centroid = extractor.extract(pw, ort);
          if (centroid == null) {
            Nop.breakpoint();
          } else {

            StringBuilder jsonData = new StringBuilder(4096);
            jsonData.append("{\"tätortskod\":\"").append(ort.getTätortskod().get()).append("\"\n");
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
          pw.flush();
        }
      } finally {
        cursor.close();
      }

      logWriter.close();

      pw.close();
      osw.close();
      fos.close();

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


  public TatortscetroidHarvester() throws Exception {
  }

  public Koordinat extract(PrintWriter out, Ort ort) throws Exception {

    Kommun kommun = DomainStore.getInstance().getKommuner().get(ort.getKommunIdentity().get());

    Double south = null;
    Double west = null;
    Double north = null;
    Double east = null;

    for (Koordinat koordinat : kommun.getKoordinater().getPolygon().get()) {

      if (south == null) {

        south = koordinat.getLatitude();
        west = koordinat.getLongitude();
        north = koordinat.getLatitude();
        east = koordinat.getLongitude();

      } else {

        if (koordinat.getLatitude() > north) {
          north = koordinat.getLatitude();
        } else if (koordinat.getLatitude() < south) {
          south = koordinat.getLatitude();
        }

        if (east < koordinat.getLongitude()) {
          east = koordinat.getLongitude();
        } else if (west > koordinat.getLongitude()) {
          west = koordinat.getLongitude();
        }
      }
    }

//    // expand bbox due to discrepancies in osm kommun border data
//
//    double latitudeDiff = north - south;
//    double longitudeDiff = east - west;
//
////    latitudeDiff /= 2d;
////    longitudeDiff /= 2d;
//
//    north += latitudeDiff;
//    south -= latitudeDiff;
//
//    east += longitudeDiff;
//    west -= longitudeDiff;


    StringBuilder overpassQuery = new StringBuilder();

    overpassQuery.append("<osm-script timeout=\"900\">\n");
    overpassQuery.append("  <query into=\"_\" type=\"node\">\n");
    overpassQuery.append("    <bbox-query into=\"_\" s=\"").append(south).append("\" n=\"").append(north).append("\" w=\"").append(west).append("\" e=\"").append(east).append("\"/>\n");
    overpassQuery.append("    <has-kv k=\"name\" modv=\"\" v=\"").append(StringEscapeUtils.escapeXml(ort.getNamn().get())).append("\"/>\n");
    overpassQuery.append("    <has-kv k=\"place\" modv=\"\" regv=\"city|town|village|hamlet|suburb|neighbourhood\"/>\n");
    overpassQuery.append("  </query>\n");
    overpassQuery.append("  <print/>\n");
    overpassQuery.append("</osm-script>\n");

    HttpClient httpClient = new DefaultHttpClient();

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
      System.out.println("| " + ort.getNamn().get() + " || " + kommun.getNamn().get() + " || " + ort.getTätortskod().get() + " || || Saknas i OSM?");
      System.out.println("|-");

      return null;
    } else if (nodes.getLength() > 1) {

      EnvelopeImpl envelope = new EnvelopeImpl();

      List<String> nodeIds = new ArrayList<String>();

      for (int i=0; i<nodes.getLength(); i++) {
        org.w3c.dom.Node node = nodes.item(i);
        CoordinateImpl coordinate = new CoordinateImpl();
        coordinate.setLatitude(Double.valueOf(latitudeExpression.evaluate(node)));
        coordinate.setLongitude(Double.valueOf(longitudeExpression.evaluate(node)));
        envelope.addBounds(coordinate);
        nodeIds.add(idExpression.evaluate(node));
      }
      double maximumCoordinateDistancesKilometers = envelope.getNortheast().arcDistance(envelope.getSouthwest());

      int maximumDistanceMeters = (int)(maximumCoordinateDistancesKilometers * 1000);

      System.out.print("| "+ort.getNamn().get()+" || "+kommun.getNamn().get()+" || "+ort.getTätortskod().get()+" || || Flera möjliga noder inom "+maximumDistanceMeters+" meter:<br/>");
      for (Iterator<String> it = nodeIds.iterator(); it.hasNext();) {
        String nodeId = it.next();
        System.out.print("{{Node|"+nodeId+"}}");
        if (it.hasNext()) {
          System.out.print("<br/>");
        }
      }
      System.out.println();
      System.out.println("|-");


      return null;
    }
    org.w3c.dom.Node node = nodes.item(0);
    Koordinat koordinat = new Koordinat();
    koordinat.setLatitude(Double.valueOf(latitudeExpression.evaluate(node)));
    koordinat.setLongitude(Double.valueOf(longitudeExpression.evaluate(node)));


    String nodeId = idExpression.evaluate(node);

    System.out.println("| "+ort.getNamn().get()+" || "+kommun.getNamn().get()+" || "+ort.getTätortskod().get()+" || {{Node|"+nodeId+"}} ||");
    System.out.println("|-");

    return koordinat;
  }

}
