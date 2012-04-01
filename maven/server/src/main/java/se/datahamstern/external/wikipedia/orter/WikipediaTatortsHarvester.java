package se.datahamstern.external.wikipedia.orter;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventConsumer;
import se.datahamstern.event.EventQueue;
import se.datahamstern.event.JsonEventLogWriter;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.xml.DomUtils;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.File;
import java.util.*;

/**
 * @author kalle
 * @since 2012-03-07 03:35
 */
public class WikipediaTatortsHarvester {

  public static void main(String[] args) throws Exception {

    try {

      Datahamstern.getInstance().open();


      JsonEventLogWriter eventLog = new JsonEventLogWriter(new File(EventQueue.getInstance().getInbox(), System.currentTimeMillis() + ".wikipedia-tatorter.events.json")) {
        @Override
        public void consume(Event event) throws Exception {
          event.setIdentity(UUID.randomUUID().toString());
          super.consume(event);
        }
      };
      new WikipediaTatortsHarvester().harvest(eventLog);
      eventLog.close();

    } finally {
      Datahamstern.getInstance().close();
    }

  }

  public WikipediaTatortsHarvester() throws Exception {
    super();
  }

  public void harvest(EventConsumer eventConsumer) throws Exception {

    Source source = new Source();
    source.setTimestamp(new Date());
    source.setLicense("public domain");
    source.setDetails("http://sv.wikipedia.org/wiki/Lista_över_Sveriges_tätorter");
    source.setAuthor("sv.wikipedia");
    source.setTrustworthiness(0.3f);


    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {

      selenium.openAndWaitForPageToLoad("http://sv.wikipedia.org/wiki/Lista_över_Sveriges_tätorter");

      Node table = (Node) selenium.xpath.compile("id('content')/*//TABLE/TBODY/TR/TD[2]/TABLE[@class='sortable wikitable jquery-tablesorter']").evaluate(selenium.getDOM(), XPathConstants.NODE);
      if (table == null) {
        throw new RuntimeException("Kunde inte hitta listan med tätorter!");
      }

      Set<String> requiredHeaders = new HashSet<String>(Arrays.asList(
          "Tätort",
          "Kommun",
          "Folkmängd",
          "Landareal (hektar)",
          "Täthet (inv./km²)",
          "Tätort",
          "Tätortskod"
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
      if (rows == null || rows.getLength() < 1900) {
        throw new RuntimeException("Det finns färre än 1900 tätorter i listan!");
      }
      for (int rowIndex = 0; rowIndex < rows.getLength(); rowIndex++) {

        Event event = new Event();
        event.setCommandName(WikipediaTatortsCommand.COMMAND_NAME);
        event.setCommandVersion(WikipediaTatortsCommand.COMMAND_VERSION);

        event.setSources(new ArrayList<Source>());
        event.getSources().add(source);

        StringBuilder jsonData = new StringBuilder();

        boolean needsComma = false;

        jsonData.append("{");

        NodeList columns = (NodeList) columnsExpression.evaluate(rows.item(rowIndex), XPathConstants.NODESET);
        for (int columnIndex = 0; columnIndex < columns.getLength(); columnIndex++) {

          Object value;

          String header = textHeaders.get(columnIndex);

          if ("Täthet (inv./km²)".equals(header)) {
            continue;
          }

          if (needsComma) {
            jsonData.append(',');
          }

          jsonData.append('"');

          if ("Tätort".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("tätortsnamn"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Tätortsnamn saknas!");
            }
            value = stringValue;

          } else if ("Kommun".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("kommunnamn"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Kommunnamn saknas!");
            }
            value = stringValue;

          } else if ("Folkmängd".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("folkmängd"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Folkmängd saknas!");
            }
            value = Integer.parseInt(stringValue.replaceAll("\\s+", ""));

          } else if ("Landareal (hektar)".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("hektarLandareal"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex).getFirstChild().getNextSibling()));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Areal saknas!");
            }
            value = Double.parseDouble(stringValue.replaceAll("\\s+", "").replaceAll(",", "."));

          } else if ("Tätortskod".equals(header)) {
            jsonData.append(StringEscapeUtils.escapeJavaScript("tätortskod"));
            String stringValue = DomUtils.normalizeText(DomUtils.toText(columns.item(columnIndex)));
            if (stringValue.isEmpty()) {
              throw new RuntimeException("Tätortskod saknas!");
            }
            value = stringValue;


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

        eventConsumer.consume(event);


      }

    } finally {
      selenium.stop();
    }

  }

}
