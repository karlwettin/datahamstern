package se.datahamstern.external.naringslivsregistret;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.command.Event;
import se.datahamstern.domain.Lan;
import se.datahamstern.domain.Organisation;
import se.datahamstern.sourced.SourcedValue;

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

  public static String COMMAND_NAME = "uppdatera post från näringslivsregistret";
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

  @Override
  public void execute(Event event, JSONParser jsonParser) throws Exception {

    JSONObject data = (JSONObject) jsonParser.parse(event.getJsonData());

    String nummerPrefix = (String) data.remove("nummerprefix");
    String nummer = (String) data.remove("nummer");
    String nummerSuffix = (String) data.remove("nummersuffix");

    String namn = (String) data.remove("namn");
    String typ = (String) data.remove("typ");
    String länsnummer = (String) data.remove("länsnummer");

    String status = (String) data.remove("status");

    if (data.size() > 0) {
      throw new Exception("unknown data in event: " + data.toString());
    }

    Date timestamp = event.getSources().get(0).getTimestamp();

    Organisation organisation = Datahamstern.getInstance().getDomainStore().getOrganisationByNummer().get(nummer);
    if (organisation == null) {
      organisation = new Organisation();
      updateSourced(organisation, event);
    }

    updateSourcedValue(organisation.getNummer(), nummer, event);
    updateSourcedValue(organisation.getNummerPrefix(), nummerPrefix, event);
    updateSourcedValue(organisation.getNummerSuffix(), nummerSuffix, event);
    updateSourcedValue(organisation.getNamn(), namn, event);
    updateSourcedValue(organisation.getTyp(), typ, event);

    if (status != null) {
      boolean statusFound = false;
      for (SourcedValue<String> otherStatus : organisation.getStatus()) {
        if (status.equals(otherStatus.get())) {
          // todo update
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
      Lan län = Datahamstern.getInstance().getDomainStore().getLänByNummerkod().get(länsnummer);
      if (län == null) {
        län = new Lan();
        updateSourced(län, event);
        Datahamstern.getInstance().getDomainStore().put(län);

        updateSourcedValue(län.getNummerkod(), län.getIdentity(), event);
      }
      updateSourcedValue(organisation.getLänIdentity(), län.getIdentity(), event);
    }

    Datahamstern.getInstance().getDomainStore().put(organisation);
  }

}
