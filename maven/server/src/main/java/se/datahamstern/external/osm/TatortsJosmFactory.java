package se.datahamstern.external.osm;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import se.datahamstern.Nop;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2012-05-31 11:31
 */
public class TatortsJosmFactory {

  public static void main(String[] args) throws Exception {

    DOMParser p = new DOMParser();
    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression nodeExpression = xpath.compile("//node");
    XPathExpression scbRefExpression = xpath.compile("//tag[@k='ref:se:scb']");

    TransformerFactory transfac = TransformerFactory.newInstance();
    Transformer trans = transfac.newTransformer();
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    trans.setOutputProperty(OutputKeys.INDENT, "yes");


    HttpClient httpClient = new DefaultHttpClient();
    HttpGet get = new HttpGet("http://wiki.openstreetmap.org/wiki/WikiProject_Sweden/Tätorter?action=raw");
    BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.execute(get).getEntity().getContent(), "UTF8"));
    String line;

    // skip to first entry
    while ((line = br.readLine()) != null) {
      if (line.startsWith("{|")) {
        break;
      }
    }

    while ((line = br.readLine()) != null) {
      if (line.startsWith("! ")) {
        break;
      }
    }

    Pattern pattern = Pattern.compile(".* \\|\\| .* \\|\\| ([0-9]+) \\|\\| \\{\\{Node\\|([0-9]+)\\}\\} \\|\\|");

    PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("tatorter.josm.osm")), "UTF8"));

    pw.println("<?xml version='1.0' encoding='UTF-8'?>");
    pw.println("<osm version='0.6' upload='true' generator='JOSM'>");

    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.equals("|-")) {
        continue;
      }
      if ("|}".equals(line)) {
        break;
      }

      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        String scbRef = matcher.group(1);
        String nodeId = matcher.group(2);

        p.parse("http://www.openstreetmap.org/api/0.6/node/" + nodeId);
        Document document = p.getDocument();

        if (scbRefExpression.evaluate(document, XPathConstants.NODE) != null) {
          System.out.println("Node "+nodeId+" already contains scb ref!");
        } else {

          Element node = (Element)nodeExpression.evaluate(document, XPathConstants.NODE);
          node.setAttribute("action", "modify");

          Element scbRefTag = document.createElement("tag");
          scbRefTag.setAttribute("k", "ref:se:scb");
          scbRefTag.setAttribute("v", scbRef);
          node.appendChild(scbRefTag);


          StreamResult result = new StreamResult(pw);
          DOMSource source = new DOMSource(node);
          trans.transform(source, result);

        }

        Nop.breakpoint();
      } else {
        Nop.breakpoint();
      }


    }


    pw.println("</osm>");

    pw.close();


  }



}
