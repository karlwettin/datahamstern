package se.datahamstern.external.posten;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventQueue;
import se.datahamstern.event.JsonEventLogWriter;
import se.datahamstern.external.wikipedia.kommuner.WikipediaKommunCommand;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.xml.DomUtils;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-12 12:56
 */
public class PostenPostnummerdatabasHarvester {

  public static void main(String[] args) throws Exception {
    Datahamstern.getInstance().open();
    try {
      JsonEventLogWriter eventLog = new JsonEventLogWriter(new File(EventQueue.getInstance().getOutbox(), "posten-postnummer-" + System.currentTimeMillis() + ".events.json")) {
        @Override
        public void write(Event event) throws IOException {
          event.setIdentity(UUID.randomUUID().toString());
          super.write(event);
        }
      };
      new PostenPostnummerdatabasHarvester().harvest(eventLog);
      eventLog.close();

    } finally {
      Datahamstern.getInstance().close();
    }
  }

  public void harvest(JsonEventLogWriter eventLog) throws Exception {

    Source source = new Source();
    source.setTimestamp(new Date());
    source.setLicense("public domain");
    source.setDetails("http://www.posten.se/soktjanst/postnummersok");
    source.setAuthor("posten.se");
    source.setTrustworthiness(1f);

    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {

      for (int postnummerIndex = 10000; postnummerIndex < 100000; postnummerIndex++) {

        selenium.openAndWaitForPageToLoad("http://www.posten.se/soktjanst/postnummersok/resultat.jspv?pnr=" + postnummerIndex);

        Node table = (Node) selenium.xpath.compile("//TABLE[@class='result']").evaluate(selenium.getDOM(), XPathConstants.NODE);
        if (table == null) {
          // todo save this as an event where the postnummer is non existing! it might have been removed or changed or something!
          // log.info("No data for postnummer " + postnummerIndex);
          continue;
        }

        NodeList rowNodes = (NodeList) selenium.xpath.compile("TR").evaluate(table, XPathConstants.NODESET);
        // todo assert row 0 is headers and that the header names are right
        for (int rowIndex = 1; rowIndex > rowNodes.getLength(); rowIndex++) {
          Node row = rowNodes.item(rowIndex);

          String gatunamn = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[1]").evaluate(row, XPathConstants.NODE)));
          String gatunummer = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[2]").evaluate(row, XPathConstants.NODE)));
          String postnummer = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[3]").evaluate(row, XPathConstants.NODE)));
          String postort = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[4]").evaluate(row, XPathConstants.NODE)));

          Event event = new Event();
          event.setCommandName(PostenPostnummerCommand.COMMAND_NAME);
          event.setCommandVersion(PostenPostnummerCommand.COMMAND_VERSION);

          event.setSources(new ArrayList<Source>());
          event.getSources().add(source);

          StringBuilder jsonData = new StringBuilder();

          jsonData.append("{");

          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript("gatunamn"));
          jsonData.append('"');
          jsonData.append(':');
          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript(gatunamn));
          jsonData.append('"');
          jsonData.append(',');

          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript("gatunummer"));
          jsonData.append('"');
          jsonData.append(':');
          if (gatunummer.isEmpty()) {
            jsonData.append("null");
          } else {
            jsonData.append('"');
            jsonData.append(StringEscapeUtils.escapeJavaScript(gatunummer));
            jsonData.append('"');
          }
          jsonData.append(',');

          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript("postnummer"));
          jsonData.append('"');
          jsonData.append(':');
          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript(postnummer));
          jsonData.append('"');
          jsonData.append(',');

          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript("postort"));
          jsonData.append('"');
          jsonData.append(':');
          jsonData.append('"');
          jsonData.append(StringEscapeUtils.escapeJavaScript(postort));
          jsonData.append('"');

          jsonData.append("}");

          event.setJsonData(jsonData.toString());


          eventLog.write(event);


          Nop.breakpoint();
        }

      }

    } finally {
      selenium.stop();
    }

  }

}
