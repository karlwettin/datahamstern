package se.datahamstern.external.posten.postnummer;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventQueue;
import se.datahamstern.event.JsonEventLogWriter;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.xml.DomUtils;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
        public void consume(Event event) throws Exception {
          event.setIdentity(UUID.randomUUID().toString());
          super.consume(event);
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

      // todo this operation should of course be threaded. hence the synchronized set.

      Set<String> processedPostorter = Collections.synchronizedSet(new HashSet<String>());

      // make sure we find out about all postnummer
      for (int postnummer = 10000; postnummer < 100000; postnummer++) {

        PostnummerQuery query = new PostnummerQuery();
        query.setPostnummer(String.valueOf(postnummer));

        Set<String> postorter = new HashSet<String>();
        search(query, eventLog, source, selenium, postorter);

        // pick up all streets in postorter found in postnummer
        for (String postort : postorter) {
          if (processedPostorter.add(postort)) {
            for (char c : streetNameCharset) {
              harvestStreetNames(String.valueOf(c), eventLog, source, selenium, postort);
            }
          }
        }

      }


    } finally {
      selenium.stop();
    }

  }

  final char[] streetNameCharset = new char[]{
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'å', 'ä', 'ö',
      'ü', // todo detect chars in use while harvesting? or analyze afterwords?
  };

  public void harvestStreetNames(String streetNamePrefix, JsonEventLogWriter eventLog, Source source, SeleniumAccessor selenium, String postort) throws Exception {
    PostnummerQuery streetQuery = new PostnummerQuery();
    streetQuery.setGatunamn(streetNamePrefix);
    streetQuery.setPostort(postort);
    if (!search(streetQuery, eventLog, source, selenium, null)) {
      for (char c : streetNameCharset) {
        harvestStreetNames(streetNamePrefix + c, eventLog, source, selenium, postort);
      }
    }
  }

  /**
   * @param eventLog
   * @param source
   * @param selenium
   * @param postorter if not null we will add any postort found to this set
   * @return false if too many hits and required to refine query to get them all, and true if any number (including 0) of matching hits
   * @throws Exception
   */
  private boolean search(PostnummerQuery query, JsonEventLogWriter eventLog, Source source, SeleniumAccessor selenium, Set<String> postorter) throws Exception {

    StringBuilder queryUrlBuilder = new StringBuilder();
    queryUrlBuilder.append("http://www.posten.se/soktjanst/postnummersok/resultat.jspv?");
    if (query.getPostnummer() != null) {
      queryUrlBuilder.append("pnr=").append(query.getPostnummer()).append("&");
    }
    if (query.getPostort() != null) {
      queryUrlBuilder.append("po=").append(query.getPostort()).append("&");
    }
    if (query.getGatunamn() != null) {
      queryUrlBuilder.append("gatunamn=").append(query.getGatunamn()).append("&");
    }
    System.out.println("Sending query " + queryUrlBuilder.toString());
    selenium.openAndWaitForPageToLoad(queryUrlBuilder.toString());

    Node table = (Node) selenium.xpath.compile("//TABLE[@summary='Tabellen visar resultatet av din postnummersökning.']").evaluate(selenium.getDOM(), XPathConstants.NODE);
    if (table == null) {
      // todo save this as an event where the postnummer is non existing! it might have been removed or changed or something!
      // log.info("No data for query " + postnummerIndex);
      return true;
    }

    NodeList rowNodes = (NodeList) selenium.xpath.compile("TBODY/TR").evaluate(table, XPathConstants.NODESET);
    // todo assert row 0 is headers and that the header names are right
    for (int rowIndex = 1; rowIndex < rowNodes.getLength(); rowIndex++) {
      Node row = rowNodes.item(rowIndex);

      String gatunamn = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[1]").evaluate(row, XPathConstants.NODE)));
      String gatunummer = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[2]").evaluate(row, XPathConstants.NODE)));
      String postnummer = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[3]").evaluate(row, XPathConstants.NODE)));
      String postort = DomUtils.normalizeText(DomUtils.toText((Node) selenium.xpath.compile("TD[4]").evaluate(row, XPathConstants.NODE)));

      if (postorter != null) {
        if (!postort.isEmpty()) {
          postorter.add(postort);
        }
      }

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


      eventLog.consume(event);


      Nop.breakpoint();
    }

    return selenium.xpath.compile("//DIV[@class='errorMessage' and contains(text(),'stort antal')]").evaluate(selenium.getDOM(), XPathConstants.NODE) == null;

  }


}
