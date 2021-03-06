package se.datahamstern.external.osm;

import com.sleepycat.persist.EntityCursor;
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
import se.datahamstern.domain.Lan;
import se.datahamstern.event.Event;
import se.datahamstern.event.JsonEventLogWriter;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;

/**
 * @author kalle
 * @since 2012-05-24 16:10
 */
public class LansgranspolygonHarvester {

  public static void main(String[] args) throws Exception {
    Datahamstern.getInstance().open();

    LansgranspolygonHarvester extractor = new LansgranspolygonHarvester();


    JsonEventLogWriter logWriter = new JsonEventLogWriter(new File("/tmp/" + System.currentTimeMillis() + ".osm-lansgranser.log.json"));

    Source source =  new Source();
    source.setAuthor("OpenStreetMap.org");
    source.setLicense("Data Copyright OpenStreetMap Contributors, Some Rights Reserved. CC-BY-SA 2.0.");
    source.setDetails(null);
    source.setTimestamp(new Date());
    source.setTrustworthiness(0.3f);


    try {
      EntityCursor<Lan> cursor = DomainStore.getInstance().getLän().entities();
      try {
        Lan län;
        while ((län = cursor.next()) != null) {
          List<Koordinat> polygon = extractor.extract(län);
          if (polygon == null) {
            System.out.println(län.getNamn().get() + " is missing!");
            Nop.breakpoint();
          } else {

            StringBuilder jsonData = new StringBuilder(4096);
            jsonData.append("{\"länsnummerkod\":\"").append(län.getNummerkod()).append("\",\"polygon\":[\n");
            for (Iterator<Koordinat> it = polygon.iterator(); it.hasNext();) {
              Koordinat koordinat = it.next();
              jsonData.append("{\"latitud\":").append(String.valueOf(koordinat.getLatitude())).append(",");
              jsonData.append("\"longitud\":").append(String.valueOf(koordinat.getLongitude())).append("}");
              if (it.hasNext()) {
                jsonData.append(",\n");
              }
            }
            jsonData.append("]}");

            Event event = new Event();
            event.setSources(new ArrayList<Source>());
            event.getSources().add(source);
            event.setCommandName(LansgranspolygonCommand.COMMAND_NAME);
            event.setCommandVersion(LansgranspolygonCommand.COMMAND_VERSION);
            event.setIdentity(UUID.randomUUID().toString());
            event.setJsonData(jsonData.toString());

            logWriter.consume(event);

            System.out.println(län.getNamn().get() + " is all good.");
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
  private XPathExpression wayNodesExpression = xpath.compile("//node");
  private XPathExpression latitudeExpression = xpath.compile("@lat");
  private XPathExpression longitudeExpression = xpath.compile("@lon");


  public LansgranspolygonHarvester() throws Exception {
  }

  public List<Koordinat> extract(Lan län) throws Exception {
    // todo should assert there is only one relation in the response!
    StringBuilder overpassQuery = new StringBuilder();

    overpassQuery.append("<osm-script timeout=\"900\">\n");
    overpassQuery.append("  <query into=\"_\" type=\"relation\">\n");
//    overpassQuery.append("    <bbox-query into=\"_\" s=\"55\" n=\"70\" w=\"10\" e=\"25\"/>\n");
    overpassQuery.append("    <has-kv k=\"lnkod\" modv=\"\" v=\"").append(län.getNummerkod().get()).append("\"/>\n");
//    overpassQuery.append("    <has-kv k=\"boundary\" modv=\"\" v=\"administrative\"/>\n");
    overpassQuery.append("  </query>\n");
    overpassQuery.append("  <recurse type=\"relation-way\"/>\n");
    overpassQuery.append("  <recurse type=\"way-node\"/>\n");
    overpassQuery.append("  <print/>\n");
    overpassQuery.append("</osm-script>\n");

    HttpClient httpClient = new DefaultHttpClient();

    HttpPost post = new HttpPost("http://www.overpass-api.de/api/interpreter");
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    nameValuePairs.add(new BasicNameValuePair("data", overpassQuery.toString()));
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    HttpResponse response = httpClient.execute(post);

    p.parse(new InputSource(response.getEntity().getContent()));

    List<Koordinat> polygon = new ArrayList<Koordinat>();

    NodeList nodes = (NodeList) wayNodesExpression.evaluate(p.getDocument(), XPathConstants.NODESET);

    if (nodes.getLength() == 0) {
      return null;
    }

    for (int i = 0; i < nodes.getLength(); i++) {
      org.w3c.dom.Node node = nodes.item(i);
      Koordinat koordinat = new Koordinat();
      koordinat.setLatitude(Double.valueOf(latitudeExpression.evaluate(node)));
      koordinat.setLongitude(Double.valueOf(longitudeExpression.evaluate(node)));
      polygon.add(koordinat);
    }

    return polygon;
  }

}
