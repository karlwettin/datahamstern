package se.datahamstern.external.osm;

import com.sleepycat.persist.EntityCursor;
import org.apache.commons.io.IOUtils;
import org.apache.xerces.parsers.DOMParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Gata;
import se.datahamstern.domain.Koordinat;
import se.datahamstern.domain.Postort;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2012-05-24 12:15
 */
public class GatupolygontagResolver {

  public static void main(String[] args) throws Exception {
    Datahamstern.getInstance().open();

    GatupolygontagResolver resolver = new GatupolygontagResolver();

    try {
      EntityCursor<Gata> gator = DomainStore.getInstance().getGator().entities();
      try {
        Gata gata;
        while ((gata = gator.next()) != null) {
          resolver.resolve(gata);
        }
      } finally {
        gator.close();
      }
    } finally {
      Datahamstern.getInstance().close();
    }
  }


  public GatupolygontagResolver() throws Exception {
  }

  private DOMParser p = new DOMParser();
  private XPath xpath = XPathFactory.newInstance().newXPath();
  private XPathExpression wayNodesExpression = xpath.compile("//nd/@ref");
  private XPathExpression nodeLatitudeExpression = xpath.compile("//node/@lat");
  private XPathExpression nodeLongitudeExpression = xpath.compile("//node/@lon");

  public List<Koordinat> resolve(Gata gata) throws Exception {

    Postort postort = DomainStore.getInstance().getPostorter().get(gata.getPostortIdentity().get());

    StringBuilder addressQueryFactory = new StringBuilder();
    addressQueryFactory.append(gata.getNamn().get());
    addressQueryFactory.append(", ");
    addressQueryFactory.append(postort.getNamn().get());
    addressQueryFactory.append(", Sweden");

    String addressQuery = URLEncoder.encode(addressQueryFactory.toString(), "UTF8");

    StringBuilder urlFactory = new StringBuilder(512);
    urlFactory.append("http://nominatim.openstreetmap.org/search?format=json&addressdetails=1&polygon=1");
    urlFactory.append("&q=").append(addressQuery);

    URLConnection connection = new URL(urlFactory.toString()).openConnection();

    Reader reader = new InputStreamReader(connection.getInputStream(), "UTF8");
    StringWriter jsonFactory = new StringWriter(49152);
    IOUtils.copy(reader, jsonFactory);
    reader.close();
    String json = jsonFactory.toString();
    reader = new StringReader(json);


    JSONParser parser = new JSONParser();

    JSONArray results = (JSONArray) parser.parse(reader);
    reader.close();

    if (results.size() == 0) {
      return null;

    } else {

      List<Way> ways = new ArrayList<Way>();
      for (int wayIndex = 0; wayIndex < results.size(); wayIndex++) {
        JSONObject result = (JSONObject) results.get(wayIndex);
        if ("way".equals(result.get("osm_type"))) {
          Way way = new Way();
          way.setId(Integer.valueOf((String) result.get("osm_id")));

          // load way
          way.setNodes(new ArrayList<Node>());
          p.parse("http://www.openstreetmap.org/api/0.6/way/" + way.getId());
          NodeList wayNodes = (NodeList) wayNodesExpression.evaluate(p.getDocument(), XPathConstants.NODESET);
          if (wayNodes.getLength() == 0) {
            throw new RuntimeException("Way nodes expected!");
          }
          for (int wayNodeIndex = 0; wayNodeIndex < wayNodes.getLength(); wayNodeIndex++) {
            Node node = new Node();
            node.setId(Integer.valueOf(wayNodes.item(wayNodeIndex).getTextContent()));

            // load node
            p.parse("http://www.openstreetmap.org/api/0.6/node/" + node.getId());
            Document nodeDocument = p.getDocument();
            node.setLatitude(Double.valueOf(nodeLatitudeExpression.evaluate(nodeDocument)));
            node.setLongitude(Double.valueOf(nodeLongitudeExpression.evaluate(nodeDocument)));

            way.getNodes().add(node);
          }
          ways.add(way);
        }
      }

      Nop.breakpoint();
      // todo make sure all results share at least one node id
    }


    return null;
  }

}
