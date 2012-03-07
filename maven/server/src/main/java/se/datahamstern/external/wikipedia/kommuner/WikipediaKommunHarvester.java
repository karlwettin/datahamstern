package se.datahamstern.external.wikipedia.kommuner;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventQueue;
import se.datahamstern.event.JsonEventLogWriter;
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
public class WikipediaKommunHarvester {

  public static void main(String[] args) throws Exception {

    try {

      Datahamstern.getInstance().open();

      JsonEventLogWriter eventLog = new JsonEventLogWriter(new File(EventQueue.getInstance().getOutbox(), "wikipedia_kommuner-" + System.currentTimeMillis() + ".events.json")) {
        @Override
        public void write(Event event) throws IOException {
          event.setIdentity(UUID.randomUUID().toString());
          super.write(event);
        }
      };
      new WikipediaKommunHarvester().harvest(eventLog);
      eventLog.close();

    } finally {
      Datahamstern.getInstance().close();
    }

  }

  public WikipediaKommunHarvester() throws Exception {
    super();
  }

  public void harvest(JsonEventLogWriter eventLog) throws Exception {

    Source source = new Source();
    source.setTimestamp(new Date());
    source.setLicense("public domain");
    source.setDetails("http://sv.wikipedia.org/wiki/Lista_över_Sveriges_kommuner");
    source.setAuthor("sv.wikipedia");
    source.setTrustworthiness(0.3f);


    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {

      selenium.openAndWaitForPageToLoad("http://sv.wikipedia.org/wiki/Lista_över_Sveriges_kommuner");

      Node table = (Node) selenium.xpath.compile("id('content')/*//TABLE/TBODY/TR/TD[2]/TABLE[@class='sortable wikitable jquery-tablesorter']").evaluate(selenium.getDOM(), XPathConstants.NODE);
      if (table == null) {
        throw new RuntimeException("Kunde inte hitta listan med kommuner!");
      }

      Set<String> requiredHeaders = new HashSet<String>(Arrays.asList(
          "Kod",
          "Kommun",
          "Län",
          "Folkmängd",
          "Area",
          "Land",
          "Sjö",
          "Hav",
          "Täthet"
      ));

      List<String> textHeaders = new ArrayList<String>(10);
      NodeList headers = (NodeList) selenium.xpath.compile("THEAD/TR/TH").evaluate(table, XPathConstants.NODESET);
      for (int headerIndex = 0; headerIndex < headers.getLength(); headerIndex++) {
        textHeaders.add(DomUtils.normalizeText(headers.item(headerIndex).getTextContent()));
      }

      if (!textHeaders.containsAll(requiredHeaders) || textHeaders.size() != requiredHeaders.size()) {
        throw new RuntimeException("Kolumnbeskrivningarna i listan på wikipediasidan har ändrats!");
      }

      XPathExpression columnsExpression = selenium.xpath.compile("TD");

      NodeList rows = (NodeList) selenium.xpath.compile("TBODY/TR").evaluate(table, XPathConstants.NODESET);
      if (rows == null || rows.getLength() != 290) {
        throw new RuntimeException("Förutsatte 290 kommuner i listan!");
      }
      for (int rowIndex = 0; rowIndex < rows.getLength(); rowIndex++) {

        Event event = new Event();
        event.setCommandName(WikipediaKommunCommand.COMMAND_NAME);
        event.setCommandVersion(WikipediaKommunCommand.COMMAND_VERSION);

        event.setSources(new ArrayList<Source>());
        event.getSources().add(source);

        StringBuilder jsonData = new StringBuilder();

        boolean needsComma = false;

        jsonData.append("{");

        NodeList columns = (NodeList) columnsExpression.evaluate(rows.item(rowIndex), XPathConstants.NODESET);
        for (int columnIndex = 0; columnIndex < columns.getLength(); columnIndex++) {

          Object value;

          String header = textHeaders.get(columnIndex);

          if ("Täthet".equals(header)) {
            continue;
          }

          if (needsComma) {
            jsonData.append(',');
          }

          jsonData.append('"');

          if ("Kod".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("kommunnummerkod"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Kommunkod saknas!");
            }
            value = stringValue;

          } else if ("Kommun".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("kommunnamn"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Kommunnamn saknas!");
            }
            value = stringValue;

          } else if ("Län".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("länsnamn"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Länsnamn saknas!");
            }
            value = stringValue;

          } else if ("Folkmängd".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("folkmängd"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Folkmängd saknas!");
            }
            value = Integer.parseInt(stringValue.replaceAll("\\s+", ""));

          } else if ("Area".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("hektarLandareal"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Areal saknas!");
            }
            value = Double.parseDouble(stringValue.replaceAll("\\s+", "").replaceAll(",", "."));

          } else if ("Land".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("hektarLandsareal"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Landareal saknas!");
            }
            value = Double.parseDouble(stringValue.replaceAll("\\s+", "").replaceAll(",", "."));

          } else if ("Sjö".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("hektarSjöareal"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Sjöareal saknas!");
            }
            value = Double.parseDouble(stringValue.replaceAll("\\s+", "").replaceAll(",", "."));

          } else if ("Hav".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("hektarHavsareal"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Havsareal saknas!");
            }
            value = Double.parseDouble(stringValue.replaceAll("\\s+", "").replaceAll(",", "."));

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
