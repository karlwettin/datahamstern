package se.datahamstern.external.posten.postnummer;

import com.sleepycat.persist.EntityCursor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.event.Event;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2012-03-07 04:31
 */
public class PostenPostnummerCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   *
   * @see #COMMAND_VERSION
   *      <p/>
   *      If this value change (ie only by editing the code and recompile),
   *      then version should also be set to 1.
   */
  public static String COMMAND_NAME = "uppdatera med post från postens postnummerdatabas";

  /**
   * A version that combined with the name uniquely describes this command.
   *
   * @see #COMMAND_NAME
   *      <p/>
   *      If updating this class so it accepts other incoming data
   *      then stop now!
   *      <p/>
   *      Instead, create a clone and increase this value.
   *      <p/>
   *      If only changing the logic in this class
   *      make sure that all code that use this constant are synchronized
   *      or create a clone and increase this value.
   *      <p/>
   *      If you are not sure about what to change it to, then here are some ideas:
   *      from 1 to 1-myUniqueNameHack,
   *      from 1.0.1 to 1.0.1-myUniqueNameHack
   *      <p/>
   *      If you are the official author of the original command
   *      you should probably change it from 1 to 1.0.1.
   */
  public static String COMMAND_VERSION = "1";


  @Override
  public void execute(Event event, JSONParser jsonParser) throws Exception {

    JSONObject jsonObject = (JSONObject) jsonParser.parse(event.getJsonData());

    String gatunamn = (String) jsonObject.remove("gatunamn");
    String gatunummer = (String) jsonObject.remove("gatunummer");
    String postnummerValue = (String) jsonObject.remove("postnummer");
    String postortValue = (String) jsonObject.remove("postort");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    Postort postort = DomainStore.getInstance().getPostortByNamn().get(postortValue);
    if (postort == null) {
      postort = new Postort();
    }
    updateSourced(postort, event);
    updateSourcedValue(postort.getNamn(), postortValue, event);
    DomainStore.getInstance().put(postort);


    Postnummer postnummer = DomainStore.getInstance().getPostnummerByPostnummer().get(postnummerValue);
    if (postnummer == null) {
      postnummer = new Postnummer();
    }
    updateSourced(postnummer, event);
    updateSourcedValue(postnummer.getPostnummer(), postnummerValue, event);
    updateSourcedValue(postnummer.getPostortIdentity(), postort.getIdentity(), event);
    DomainStore.getInstance().put(postnummer);


    if (gatunamn != null && !gatunamn.isEmpty()) {

      // todo if ("BOX".equals(gatunamn)) { }

      if (gatunummer != null && !gatunummer.trim().isEmpty()) {
        String[] range = gatunummer.split("-");
        int from = Integer.valueOf(range[0].trim());
        int to = Integer.valueOf(range[1].trim());

        Gatuadress.UniqueIndex uniqueIndex = new Gatuadress.UniqueIndex();
        uniqueIndex.setGatunamn(gatunamn);
        uniqueIndex.setPostnummerIdentity(postnummer.getIdentity());

        // todo assert that ranges ALWAYS are even or odd sequence!!!
        for (int nummer = from; nummer < to; nummer += 2) {
          uniqueIndex.setGatunummer(nummer);
          Gatuadress gatuadress = DomainStore.getInstance().getGatuadressByUniqueIndex().get(uniqueIndex);
          if (gatuadress == null) {
            gatuadress = new Gatuadress();
          }
          updateSourced(gatuadress, event);
          updateSourcedValue(gatuadress.getGatunamn(), gatunamn, event);
          updateSourcedValue(gatuadress.getGatunummer(), nummer, event);
          DomainStore.getInstance().put(gatuadress);
        }
      }


    }


  }

  public void register(CommandManager commandManager) {
    commandManager.registerCommandClass(PostenPostnummerCommand.class, COMMAND_NAME, COMMAND_VERSION);
  }

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getCommandVersion() {
    return COMMAND_VERSION;
  }


}
