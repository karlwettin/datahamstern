package se.datahamstern.external.wikipedia.lan;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.event.Event;

/**
 * @author kalle
 * @since 2012-03-07 04:31
 */
public class WikipediaLanCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * @see #COMMAND_VERSION
   *
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   */
  public static String COMMAND_NAME = "uppdatera med post från wikipedias länslista";

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

    String länsnamn = (String)jsonObject.remove("länsnamn");
    String alfakod = ((String)jsonObject.remove("alfakod")).replaceAll("\\s+", "");
    String nummerkod = ((String)jsonObject.remove("nummerkod")).replaceAll("\\s+", "");;
    Number kvadratkilometerLandareal = (Number)jsonObject.remove("kvadratkilometerLandareal");
    Number folkmängd = (Number)jsonObject.remove("folkmängd");
    String residensstad = (String)jsonObject.remove("residensstad");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    Lan län;

    län = DomainStore.getInstance().getLänByNummerkod().get(nummerkod);
    if (län == null) {
      län = DomainStore.getInstance().getLänByAlfakod().get(alfakod);
      if (län == null) {
        // todo search by namn
        län = new Lan();
      }
    }

    updateSourced(län, event);
    updateSourcedValue(län.getNamn(), länsnamn, event);
    updateSourcedValue(län.getAlfakod(), alfakod, event);
    updateSourcedValue(län.getNummerkod(), nummerkod, event);

    // todo find and set residensstad
    // todo this can not be done until the tätorter has been read!

    DomainStore.getInstance().put(län);
  }

  public void register(CommandManager commandManager) {
    commandManager.registerCommandClass(WikipediaLanCommand.class, COMMAND_NAME, COMMAND_VERSION);
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
