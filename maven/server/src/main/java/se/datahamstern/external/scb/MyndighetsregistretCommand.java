package se.datahamstern.external.scb;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Organisation;
import se.datahamstern.event.Event;

/**
 * @author kalle
 * @since 2012-04-17 15:01
 */
public class MyndighetsregistretCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * <p/>
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   *
   * @see #COMMAND_VERSION
   */
  public static String COMMAND_NAME = "uppdatera med post från scbs myndighetsregister";

  /**
   * A version that combined with the name uniquely describes this command.
   * <p/>
   * If updating this class so it accepts other incoming data
   * then stop now!
   * <p/>
   * Instead, create a clone and increase this value.
   * <p/>
   * If only changing the logic in this class
   * make sure that all code that use this constant are synchronized
   * or create a clone and increase this value.
   * <p/>
   * If you are not sure about what to change it to, then here are some ideas:
   * from 1 to 1-myUniqueNameHack,
   * from 1.0.1 to 1.0.1-myUniqueNameHack
   * <p/>
   * If you are the official author of the original command
   * you should probably change it from 1 to 1.0.1.
   *
   * @see #COMMAND_NAME
   */
  public static String COMMAND_VERSION = "1";


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

    JSONObject jsonObject = (JSONObject) jsonParser.parse(event.getJsonData());

    String nummer = (String) jsonObject.remove("organisationsnummer");
    String namn = (String) jsonObject.remove("namn");

    // todo make use of the contact information!

    JSONObject postadress = (JSONObject) jsonObject.remove("postadress");
    JSONObject besöksadress = (JSONObject) jsonObject.remove("besöksadress");
    String epost = (String) jsonObject.remove("epost");
    String hemsida = (String) jsonObject.remove("hemsida");
    String telefon = (String) jsonObject.remove("telefon");
    String fax = (String) jsonObject.remove("fax");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }


    Organisation organisation = DomainStore.getInstance().getOrganisationByNummer().get(nummer);
    if (organisation == null) {
      organisation = new Organisation();
    }
    updateSourced(organisation, event);

    updateSourcedValue(organisation.getNamn(), namn, event);

    updateSourcedValue(organisation.getNummer(), nummer, event);
//    updateSourcedValue(organisation.getNummerprefix(), nummerPrefix, event);
//    updateSourcedValue(organisation.getNummersuffix(), nummerSuffix, event);

    // todo this information is not stored in the event, it is generalized from data source.
    // todo really should implemet an a priori event that means just that.
    updateSourcedValue(organisation.getTyp(), "M", event);

    DomainStore.getInstance().put(organisation);

  }
}
