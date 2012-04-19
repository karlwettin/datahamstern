package se.datahamstern.external.wikipedia.kommuner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Kommun;
import se.datahamstern.domain.Lan;
import se.datahamstern.event.Event;

/**
 * @author kalle
 * @since 2012-03-07 04:31
 */
public class WikipediaKommunCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * @see #COMMAND_VERSION
   *
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   */
  public static String COMMAND_NAME = "uppdatera med post från wikipedias kommunlista";

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
  public void execute(Event event, JSONParser jsonParser) throws Exception {

    JSONObject jsonObject = (JSONObject)jsonParser.parse(event.getJsonData());

    String kommunnummerkod = (String)jsonObject.remove("kommunnummerkod");
    String kommunnamn = (String)jsonObject.remove("kommunnamn");
    String länsnamn = (String)jsonObject.remove("länsnamn");
    Number folkmängd = (Number)jsonObject.remove("folkmängd");
    Number hektarAreal = (Number)jsonObject.remove("hektarAreal");
    Number hektarLandareal = (Number)jsonObject.remove("hektarLandareal");
    Number hektarSjöareal = (Number)jsonObject.remove("hektarSjöareal");
    Number hektarHavsareal = (Number)jsonObject.remove("hektarHavsareal");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    if(kommunnummerkod == null || kommunnummerkod.trim().isEmpty()) {
      throw new RuntimeException("Kommunnummerkod is not set!");
    }

    Kommun kommun = DomainStore.getInstance().getKommunByNummerkod().get(kommunnummerkod);
    if (kommun == null) {
      kommun = new Kommun();
    }
    updateSourced(kommun, event);
    updateSourcedValue(kommun.getNamn(), kommunnamn, event);
    updateSourcedValue(kommun.getNummerkod(), kommunnummerkod, event);

    Lan län = DomainStore.getInstance().getLänByNamn().get(länsnamn);
    if (län == null) {
      throw new RuntimeException("Could not find län with name " +länsnamn);
    }
    // we never update knowledge of län from this end
    // as it would create so many postings to the bdb


    updateSourcedValue(kommun.getLänIdentity(), län.getIdentity(), event);

    DomainStore.getInstance().put(kommun);

  }

  public void register(CommandManager commandManager) {
    commandManager.registerCommandClass(WikipediaKommunCommand.class, COMMAND_NAME, COMMAND_VERSION);
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
