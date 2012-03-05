package se.datahamstern.external.naringslivsregistret;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.event.Event;
import se.datahamstern.command.Source;
import se.datahamstern.domain.Lan;
import se.datahamstern.domain.Organisation;
import se.datahamstern.sourced.SourcedValue;

import java.util.ArrayList;
import java.util.Date;

/**
 * exempelpost:
 * <p/>
 * "command" : {
 * "name" : "uppdatera post från näringslivsregistret",
 * "version" : "1"
 * },
 * "data" : {
 * "nummerprefix" : "16"
 * "nummer" : "5522334455",
 * "nummersuffix" : "01",
 * "namn" : "Företaget AB",
 * "typ" : "AB",
 * "länsnummer" : "12",
 * "status" : "fusion avslutad"
 * },
 * "sources" : [{
 * "author" : "näringslivsregistret@bolagsverket.se",
 * "trustworthiness" : 1,
 * "details" : "från genererat organisationnummer",
 * "license" : "public domain",
 * "timestamp" : 1221112888483
 * }]
 *
 * @author kalle
 * @since 2012-03-03 23:54
 */
public class NaringslivsregistretCommand extends Command {

  public static String COMMAND_NAME = "uppdatera med post från näringslivsregistret";
  /**
   * todo copy to each and every command class!
   *
   * when updating this class so it accepts other incoming data
   * then stop now!
   * <p/>
   * create a clone and increase this value.
   * <p/>
   * when only changing the logic in this class
   * make sure that all code that use this constant are synchronized
   * or create a clone and increase this value.
   *
   * if you are not sure about what to change it to, then here are some ideas:
   * from 1 to 1-myUniqueNameHack,
   * from 1.0.1 to 1.0.1-myUniqueNameHack
   *
   * if you are the official author of the original command
   * you should probably change it from 1 to 1.0.1.
   */
  public static String COMMAND_VERSION = "1";

  static {
    CommandManager.getInstance().registerCommandClass(NaringslivsregistretCommand.class, COMMAND_NAME, COMMAND_VERSION);
  }

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getCommandVersion() {
    return COMMAND_VERSION;
  }

  public static Source defaultSourceFactory() {
    return defaultSourceFactory(new Date());
  }

  public static Source defaultSourceFactory(Date timestamp) {
    Source source = new Source();
    source.setTimestamp(timestamp);
    source.setLicense("public domain");
    source.setAuthor("Bolagsverket/Näringslivsregistret");
    source.setTrustworthiness(1f);
    return source;
  }

  public static Event eventFactory(NaringslivsregistretResult result) {
    return eventFactory(result, defaultSourceFactory());
  }

  public static Event eventFactory(NaringslivsregistretResult result, Date timestamp) {
    return eventFactory(result, defaultSourceFactory(timestamp));
  }

  public static Event eventFactory(NaringslivsregistretResult result, Source source) {
    Event event = new Event();
    event.setCommandName(NaringslivsregistretCommand.COMMAND_NAME);
    event.setCommandVersion(NaringslivsregistretCommand.COMMAND_VERSION);

    event.setSources(new ArrayList<Source>(1));
    event.getSources().add(source);

    JSONObject json = new JSONObject();
    json.put("nummerprefix", result.getNummerprefix());
    json.put("nummer", result.getNummer());
    json.put("nummersuffix", result.getNummersuffix());
    json.put("namn", result.getNamn());
    json.put("länsnummer", result.getLänsnummer());
    json.put("firmaform", result.getFirmaform());
    json.put("firmatyp", result.getFirmatyp());
    json.put("status", result.getStatus());
    event.setJsonData(json.toString());

    return event;
  }


  @Override
  public void execute(Event event, JSONParser jsonParser) throws Exception {

    JSONObject data = (JSONObject) jsonParser.parse(event.getJsonData());

    String nummerPrefix = (String) data.remove("nummerprefix");
    String nummer = (String) data.remove("nummer");
    String nummerSuffix = (String) data.remove("nummersuffix");

    String namn = (String) data.remove("namn");

    // todo använd!!
    String firmatyp = (String) data.remove("firmatyp");

    String firmaform = (String) data.remove("firmaform");

    String länsnummer = (String) data.remove("länsnummer");

    String status = (String) data.remove("status");

    if (data.size() > 0) {
      throw new Exception("unknown data in event: " + data.toString());
    }

    Organisation organisation = DomainStore.getInstance().getOrganisationByNummer().get(nummer);
    if (organisation == null) {
      organisation = new Organisation();
      updateSourced(organisation, event);
    }

    updateSourcedValue(organisation.getNamn(), namn, event);

    updateSourcedValue(organisation.getNummer(), nummer, event);
    updateSourcedValue(organisation.getNummerprefix(), nummerPrefix, event);
    updateSourcedValue(organisation.getNummersuffix(), nummerSuffix, event);

    /**
     * todo lista ut vad det här är för data!
     *
     * todo osäker på hur detta hänger ihop, är det samma organisationsnummer med olika namn?
     * todo lagras för närvarande inte i bdb.
     *
     *
     * https://snr4.bolagsverket.se/snrgate/hjalp.do?method=hjalp.firmatyper
     * Näringslivsregistret - Firmatyper
     * Följande firmatyper kan visas:
     * Firma: Företagsnamn
     * Bifirma: Ett extra företagsnamn för en del av företagets verksamhet
     * Parallellfirma: Företagets namn (firma) översatt till annat språk
     */
//    updateSourcedValue(organisation.getFirmatyp(), namn, event);

    updateSourcedValue(organisation.getFirmaform(), firmaform, event);

    if (status != null) {
      boolean statusFound = false;
      for (SourcedValue<String> otherStatus : organisation.getStatus()) {
        if (status.equals(otherStatus.get())) {
          // todo can same text occur in multiple statuses? a, b, a, c, d ? well not according to this logic!
          updateSourcedValue(otherStatus, status, event);
          statusFound = true;
        }
      }
      if (!statusFound) {
        SourcedValue<String> newStatus = new SourcedValue<String>(status);
        updateSourcedValue(newStatus, status, event);
        // todo fit it in between other statusar as fitting with timestamps!
        organisation.getStatus().add(0, newStatus);
      }
    }


    if (länsnummer != null) {
      Lan län = DomainStore.getInstance().getLänByNummerkod().get(länsnummer);
      if (län == null) {
        län = new Lan();
        updateSourced(län, event);
        updateSourcedValue(län.getNummerkod(), länsnummer, event);
        DomainStore.getInstance().put(län);

        updateSourcedValue(län.getNummerkod(), län.getIdentity(), event);
      }
      updateSourcedValue(organisation.getLänIdentity(), län.getIdentity(), event);
    }

    DomainStore.getInstance().put(organisation);
  }

}
