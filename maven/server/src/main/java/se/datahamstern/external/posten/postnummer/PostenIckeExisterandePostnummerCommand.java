package se.datahamstern.external.posten.postnummer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.event.Event;

/**
 * @author kalle
 * @since 2012-04-16 10:47
 */
public class PostenIckeExisterandePostnummerCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * @see #COMMAND_VERSION
   *
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   */
  public static String COMMAND_NAME = "uppdatera med avsaknad postnummerpost från postens postnummerdatabas";

  /**
   * A version that combined with the name uniquely describes this command.
   * @see #COMMAND_NAME
   *
   * If updating this class so it accepts other incoming data
   * then stop now!
   * <p/>
   * Instead, create a clone and increase this value.
   * <p/>
   * If only changing the logic in this class
   * make sure that all code that use this constant are synchronized
   * or create a clone and increase this value.
   *
   * If you are not sure about what to change it to, then here are some ideas:
   * from 1 to 1-myUniqueNameHack,
   * from 1.0.1 to 1.0.1-myUniqueNameHack
   *
   * If you are the official author of the original command
   * you should probably change it from 1 to 1.0.1.
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

    String postnummerValue = ((String) jsonObject.remove("postnummer")).replaceAll("\\s+", "");

    Postnummer postnummer = DomainStore.getInstance().getPostnummerByPostnummer().get(postnummerValue);
    if (postnummer == null) {
      postnummer = new Postnummer();
      updateSourced(postnummer, event);
      updateSourcedValue(postnummer.getPostnummer(), postnummerValue, event);
    } else {
      // todo find all use of this postnummer and set association to null!
      // todo currently this means all instances of Postort and Gata
    }

    updateSourcedValue(postnummer.getPostortIdentity(), null, event);
    updateSourcedValue(postnummer.getActive(), false, event);

    DomainStore.getInstance().put(postnummer);

  }
}
