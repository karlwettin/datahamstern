package se.datahamstern.external.wikipedia.lan;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventQueue;
import se.datahamstern.event.JsonEventLogWriter;
import se.datahamstern.external.wikipedia.orter.WikipediaTatortsCommand;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.xml.DomUtils;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author kalle
 * @since 2012-03-07 03:35
 */
public class WikipediaLanHarvester {

  public static void main(String[] args) throws Exception {

    try {

      Datahamstern.getInstance().open();


      JsonEventLogWriter eventLog = new JsonEventLogWriter(new File(EventQueue.getInstance().getOutbox(), "wikipedia_lan-" + System.currentTimeMillis() + ".events.json")) {
        @Override
        public void write(Event event) throws IOException {
          event.setIdentity(UUID.randomUUID().toString());
          super.write(event);
        }
      };
      new WikipediaLanHarvester().harvest(eventLog);
      eventLog.close();

    } finally {
      Datahamstern.getInstance().close();
    }

  }

  public WikipediaLanHarvester() throws Exception {
    super();
  }

  public void harvest(JsonEventLogWriter eventLog) throws Exception {

    Source source = new Source();
    source.setTimestamp(new Date());
    source.setLicense("public domain");
    source.setDetails("http://sv.wikipedia.org/wiki/Sveriges_län");
    source.setAuthor("sv.wikipedia");
    source.setTrustworthiness(0.3f);


    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {

      selenium.openAndWaitForPageToLoad("http://sv.wikipedia.org/wiki/Sveriges_län");

      Node table = (Node) selenium.xpath.compile("id('content')/*//H2/SPAN[(contains(text(), 'Lista över Sveriges län'))]/ancestor::H2/following-sibling::TABLE/TBODY/TR/TD/TABLE").evaluate(selenium.getDOM(), XPathConstants.NODE);
      if (table == null) {
        throw new RuntimeException("Kunde inte hitta listan med län!");
      }

      Set<String> requiredHeaders = new HashSet<String>(Arrays.asList(
          "Län",
          "Bokstav",
          "Kod",
          "Yta (km²)",
          "Folkmängd",
          "Inv/km²",
          "Residensstad",
          "Inrättat"
      ));

      List<String> textHeaders = new ArrayList<String>(10);
      NodeList headers = (NodeList) selenium.xpath.compile("THEAD/TR/TH").evaluate(table, XPathConstants.NODESET);
      for (int headerIndex = 0; headerIndex < headers.getLength() - 1; headerIndex++) {
        textHeaders.add(DomUtils.normalizeText(headers.item(headerIndex).getTextContent()));
      }

      if (!textHeaders.containsAll(requiredHeaders) || textHeaders.size() != requiredHeaders.size()) {
        throw new RuntimeException("Kolumnbeskrivningarna i listan på wikipediasidan har ändrats!");
      }

      XPathExpression columnsExpression = selenium.xpath.compile("TD");

      // final row is a summary
      NodeList rows = (NodeList) selenium.xpath.compile("TBODY/TR").evaluate(table, XPathConstants.NODESET);
      if (rows == null || rows.getLength() != 22) {
        throw new RuntimeException("Förutsätter 21 län, men det fanns " + (rows.getLength() - 1) + " listan!");
      }
      for (int rowIndex = 0; rowIndex < rows.getLength() -1; rowIndex++) {

        Event event = new Event();
        event.setCommandName(WikipediaLanCommand.COMMAND_NAME);
        event.setCommandVersion(WikipediaLanCommand.COMMAND_VERSION);

        event.setSources(new ArrayList<Source>());
        event.getSources().add(source);

        StringBuilder jsonData = new StringBuilder();

        boolean needsComma = false;

        jsonData.append("{");

        NodeList columns = (NodeList) columnsExpression.evaluate(rows.item(rowIndex), XPathConstants.NODESET);
        for (int columnIndex = 0; columnIndex < columns.getLength(); columnIndex++) {

          Object value;

          if (columnIndex >= textHeaders.size()) {
            // log.info("skipping column index " + columnIndex);
            continue;
          }

          String header = textHeaders.get(columnIndex);

          if ("Inv/km²".equals(header)
              || "Inrättat".equals(header)
              || "N/S".equals(header)) {
            continue;
          }

          if (needsComma) {
            jsonData.append(',');
          }

          jsonData.append('"');


          /*

          "Län",
          "Bokstav",
          "Kod",
          "Yta (km²)",
          "Folkmängd",
          "Inv/km²",
          "Residensstad",
          "Inrättat",
          "N/S" // todo vad är detta?


           */

          if ("Län".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("länsnamn"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Länsnamn saknas!");
            }
            value = stringValue;

          } else if ("Bokstav".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("alfakod"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Länsbokstavskod saknas!");
            }
            value = stringValue;

          } else if ("Kod".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("nummerkod"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Länsnummerkod saknas!");
            }
            value = stringValue;

          } else if ("Yta (km²)".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("kvadratkilometerLandareal"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Landareal saknas!");
            }
            value = Double.parseDouble(stringValue.replaceAll("\\s+", "").replaceAll(",", "."));

          } else if ("Folkmängd".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("folkmängd"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Folkmängd saknas!");
            }
            value = Integer.parseInt(stringValue.replaceAll("\\s+", ""));

          } else if ("Residensstad".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("residensstad"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Residensstad saknas!");
            }
            value = stringValue;

//          } else if ("Inrättat".equals(header)) {
//            jsonData.append(StringEscapeUtils.escapeJavaScript("inrättningsår"));
//            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
//            if (stringValue.isEmpty()) {
//              throw new RuntimeException("Inrättningsår saknas!");
//            }
//            value = Integer.parseInt(stringValue.replaceAll("\\s+", ""));
//

          } else {
//            log.warn("We are missing out on data in unknown column " + header);
            throw new RuntimeException();
          }

          jsonData.append('"');

          jsonData.append(":");
          if (value == null) {
            jsonData.append("null");
          } else if (value instanceof String) {
            jsonData.append('"');
            jsonData.append(StringEscapeUtils.escapeJavaScript(value.toString()));
            jsonData.append('"');
          } else if (value instanceof Number) {
            jsonData.append(value.toString());
          } else {
            throw new RuntimeException();
          }

          needsComma = true;

        }

        jsonData.append("}");

        event.setJsonData(jsonData.toString());

        eventLog.write(event);


      }

    } finally {
      selenium.stop();
    }

  }

}
