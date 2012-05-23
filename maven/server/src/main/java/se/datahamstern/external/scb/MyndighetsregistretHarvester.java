package se.datahamstern.external.scb;

import org.json.simple.JSONObject;
import org.w3c.dom.NodeList;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventExecutor;
import se.datahamstern.event.JsonEventLogWriter;
import se.datahamstern.external.posten.postnummer.PostenIckeExisterandePostnummerCommand;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.io.SourceChangedException;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2012-04-17 13:10
 */
public class MyndighetsregistretHarvester {

  public static void main(String[] args) throws Exception {
    Datahamstern.getInstance().open();
    try {
      JsonEventLogWriter eventLog = new JsonEventLogWriter(new File(EventExecutor.getInstance().getOutbox(), System.currentTimeMillis() + ".scb-myndighetsregistret.events.json")) {
        @Override
        public void consume(Event event) throws Exception {
          event.setIdentity(UUID.randomUUID().toString());
          super.consume(event);
        }
      };
      new MyndighetsregistretHarvester().harvest(eventLog);
      eventLog.close();

    } finally {
      Datahamstern.getInstance().close();
    }
  }

  public void harvest(JsonEventLogWriter eventLog) throws Exception {

    Source source = new Source();
    source.setTimestamp(new Date());
    source.setLicense("public domain");
    source.setDetails("http://www.myndighetsregistret.scb.se/Myndighet.aspx");
    source.setAuthor("myndighetsregistret.scb.se");
    source.setTrustworthiness(1f);


    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {

      Set<String> linksSeen = new HashSet<String>();

      selenium.openAndWaitForPageToLoad("http://www.myndighetsregistret.scb.se/Myndighet.aspx");

      String[] options = selenium.getSelectOptions("DropDown1$ddVal");
      if (!"--- Välj myndighetsgrupp här ---".equals(options[0])) {
        throw new SourceChangedException("Expected a header in top select option!");
      }
      for (int optionIndex = 1; optionIndex < options.length; optionIndex++) {
        String option = options[optionIndex];
        selenium.selectAndWaitForPageToLoad("DropDown1$ddVal", option);
        NodeList items = (NodeList) selenium.xpath.compile("id('grd')/TBODY/TR/TD/A/@href").evaluate(selenium.getDOM(), XPathConstants.NODESET);
        if (items.getLength() == 0) {
          throw new SourceChangedException("Expected at least one item in the table");
        }
        for (int itemIndex = 0; itemIndex < items.getLength(); itemIndex++) {
          linksSeen.add(items.item(itemIndex).getTextContent());
        }
      }

      for (String link : linksSeen) {

        System.out.println(link);

        selenium.openAndWaitForPageToLoad(link);

        String namn = selenium.xpath.compile("id('txtNamn')/@value").evaluate(selenium.getDOM()).trim();
        if (namn.isEmpty()) {
          throw new SourceChangedException("Myndighetsnamn must not be empty! If this is an error at scb, contact them and let them know so they can fix it!");
        }

        String orgno = selenium.xpath.compile("id('txtPeOrgNr')/@value").evaluate(selenium.getDOM()).replaceAll("\\D", "").trim();
        if (orgno.isEmpty()) {
          System.out.println("Skipping " + link + " due to missing organisationnummer. Usually true for embassies.");
          continue;
          //throw new SourceChangedException("Organisationsnummer must not be empty!");
        }
        if (!orgno.matches("^\\d{10}$")) {
          throw new SourceChangedException("Organisationsnummer is supposed to be 10 digits");
        }

        String postadress = selenium.xpath.compile("id('txtPostAdr')/@value").evaluate(selenium.getDOM()).trim();
        if (postadress.isEmpty()) {
          postadress = null;
        }

        String postadressPostnummer = selenium.xpath.compile("id('txtPostNr')/@value").evaluate(selenium.getDOM()).trim();
        if (postadressPostnummer.isEmpty()) {
          throw new SourceChangedException("Postadress postnummer must not be empty!");
        }

        String postadressPostort = selenium.xpath.compile("id('txtPostOrt')/@value").evaluate(selenium.getDOM()).trim();
        if (postadressPostort.isEmpty()) {
          throw new SourceChangedException("Postadress postort must not be empty!");
        }

        String besöksadress = selenium.xpath.compile("id('txtBAdr')/@value").evaluate(selenium.getDOM()).trim();
        if (besöksadress.isEmpty()) {
          besöksadress = null;
        }
        String besöksadressPostnummer = selenium.xpath.compile("id('txtBPostNr')/@value").evaluate(selenium.getDOM()).trim();
        if (besöksadressPostnummer.isEmpty()) {
          besöksadressPostnummer = null;
        }
        String besöksadressPostort = selenium.xpath.compile("id('txtBPostOrt')/@value").evaluate(selenium.getDOM()).trim();
        if (besöksadressPostort.isEmpty()) {
          besöksadressPostort = null;
        }

        String epost = selenium.xpath.compile("id('lnkEpost')/@href").evaluate(selenium.getDOM()).replaceFirst("(mailto:)(.+)", "$2").trim();
        if (epost.isEmpty()) {
          epost = null;
        }
        String hemsida = selenium.xpath.compile("id('lnkWebbAdr')/@href").evaluate(selenium.getDOM()).trim();
        if (hemsida.isEmpty()) {
          hemsida = null;
        }
        String telefon = selenium.xpath.compile("id('txtTel')/@value").evaluate(selenium.getDOM()).trim();
        if (telefon.isEmpty()) {
          telefon = null;
        }
        String fax = selenium.xpath.compile("id('txtFax')/@value").evaluate(selenium.getDOM()).trim();
        if (fax.isEmpty()) {
          fax = null;
        }


        Event event = new Event();
        event.setCommandName(MyndighetsregistretCommand.COMMAND_NAME);
        event.setCommandVersion(MyndighetsregistretCommand.COMMAND_VERSION);

        event.setSources(new ArrayList<Source>());
        event.getSources().add(source);

        StringBuilder jsonData = new StringBuilder();

        jsonData.append("{");

        jsonData.append('"');
        jsonData.append(JSONObject.escape("organisationsnummer"));
        jsonData.append('"');
        jsonData.append(':');
        jsonData.append('"');
        jsonData.append(JSONObject.escape(orgno));
        jsonData.append('"');
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("namn"));
        jsonData.append('"');
        jsonData.append(':');
        jsonData.append('"');
        jsonData.append(JSONObject.escape(namn));
        jsonData.append('"');
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("postadress"));
        jsonData.append('"');
        jsonData.append(':');
        jsonData.append('{');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("adress"));
        jsonData.append('"');
        jsonData.append(':');
        if (postadress == null) {
          jsonData.append("null");
        } else {

          jsonData.append('"');
          jsonData.append(JSONObject.escape(postadress));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("postnummer"));
        jsonData.append('"');
        jsonData.append(':');
        if (postadressPostnummer == null) {
          jsonData.append("null");
        } else {

          jsonData.append('"');
          jsonData.append(JSONObject.escape(postadressPostnummer));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("postort"));
        jsonData.append('"');
        jsonData.append(':');
        if (postadressPostort == null) {
          jsonData.append("null");
        } else {

          jsonData.append('"');
          jsonData.append(JSONObject.escape(postadressPostort));
          jsonData.append('"');
        }

        jsonData.append('}');
        jsonData.append(',');


        jsonData.append('"');
        jsonData.append(JSONObject.escape("besöksadress"));
        jsonData.append('"');
        jsonData.append(':');
        jsonData.append('{');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("adress"));
        jsonData.append('"');
        jsonData.append(':');
        if (besöksadress == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(besöksadress));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("postnummer"));
        jsonData.append('"');
        jsonData.append(':');
        if (besöksadressPostnummer == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(besöksadressPostnummer));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("postort"));
        jsonData.append('"');
        jsonData.append(':');
        if (besöksadressPostort == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(besöksadressPostort));
          jsonData.append('"');
        }
        jsonData.append('}');
        jsonData.append(',');


        jsonData.append('"');
        jsonData.append(JSONObject.escape("epost"));
        jsonData.append('"');
        jsonData.append(':');
        if (epost == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(epost));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("hemsida"));
        jsonData.append('"');
        jsonData.append(':');
        if (hemsida == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(hemsida));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("telefon"));
        jsonData.append('"');
        jsonData.append(':');
        if (telefon == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(telefon));
          jsonData.append('"');
        }
        jsonData.append(',');

        jsonData.append('"');
        jsonData.append(JSONObject.escape("fax"));
        jsonData.append('"');
        jsonData.append(':');
        if (fax == null) {
          jsonData.append("null");
        } else {
          jsonData.append('"');
          jsonData.append(JSONObject.escape(fax));
          jsonData.append('"');
        }

        jsonData.append("}");

        event.setJsonData(jsonData.toString());

        eventLog.consume(event);


        Nop.breakpoint();

      }

    } finally {
      selenium.stop();
    }

  }


}
