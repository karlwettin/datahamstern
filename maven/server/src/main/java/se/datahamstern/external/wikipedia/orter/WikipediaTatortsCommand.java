package se.datahamstern.external.wikipedia.orter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Ort;
import se.datahamstern.event.Event;

/**
 * @author kalle
 * @since 2012-03-07 04:31
 */
public class WikipediaTatortsCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * @see #COMMAND_VERSION
   *
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   */
  public static String COMMAND_NAME = "uppdatera med post från wikipedias tätortslista";

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



  public void register(CommandManager commandManager) {
    commandManager.registerCommandClass(WikipediaTatortsCommand.class, COMMAND_NAME, COMMAND_VERSION);
  }

  @Override
  public void execute(Event event, JSONParser jsonParser) throws Exception {

    JSONObject jsonObject = (JSONObject)jsonParser.parse(event.getJsonData());

    String tätortsnamn = (String)jsonObject.remove("tätortsnamn");
    String tätortskod = (String)jsonObject.remove("tätortskod");
    String kommunnamn = (String)jsonObject.remove("kommunnamn");
    String hektarLandareal = (String)jsonObject.remove("hektarLandareal");
    String folkmängd = (String)jsonObject.remove("folkmängd");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    if (tätortskod == null) {
      throw new RuntimeException("No tätortskod available in event!");
    }

    Ort ort = DomainStore.getInstance().getOrtByTätortskod().get(tätortskod);
    if (ort == null) {
      ort = new Ort();
    }

    updateSourced(ort, event);
    updateSourcedValue(ort.getTätortskod(), tätortskod, event);
    updateSourcedValue(ort.getNamn(), tätortsnamn, event);

    // todo landareal, folkmängd.

    Kommun kommun = DomainStore.getInstance().getKommunByNamn().get(kommunnamn);
    if (kommun == null) {
      throw new RuntimeException("Could not find kommun with name " + kommunnamn);
    }
    updateSourcedValue(ort.getKommunIdentity(), kommun.getIdentity(), event);

    DomainStore.getInstance().put(ort);


    // todo implement your command here!
    throw new UnsupportedOperationException();
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
