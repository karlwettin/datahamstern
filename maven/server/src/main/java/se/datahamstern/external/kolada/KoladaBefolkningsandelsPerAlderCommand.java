package se.datahamstern.external.kolada;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Command;
import se.datahamstern.domain.Demografi;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Kommun;
import se.datahamstern.event.Event;
import se.datahamstern.sourced.SourcedValue;

import java.io.File;

/**
 * @author kalle
 * @since 2012-03-04 20:48
 */
public class KoladaBefolkningsandelsPerAlderCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * <p/>
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   *
   * @see #COMMAND_VERSION
   */
  public static String COMMAND_NAME = "uppdatera med kommunbefolkningsandelspost per ålder från kolada";

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

    JSONObject jsonObject = (JSONObject)jsonParser.parse(event.getJsonData());

    int år = ((Number)jsonObject.remove("år")).intValue();
    String kommunnummerkod = (String)jsonObject.remove("kommunnummerkod");
    int ålder = ((Number)jsonObject.remove("ålder")).intValue();
    int antal = ((Number)jsonObject.remove("antal")).intValue();

    // todo implement genus in domain
    Boolean genus = (Boolean)jsonObject.remove("genus");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    Kommun kommun = DomainStore.getInstance().getKommunByNummerkod().get(kommunnummerkod);
    if (kommun == null) {
      kommun = new Kommun();
      updateSourced(kommun, event);
      updateSourcedValue(kommun.getNummerkod(), kommunnummerkod, event);
    } else if (Datahamstern.getInstance().isRenderFullySourced()) {
      updateSourced(kommun, event);
      updateSourcedValue(kommun.getNummerkod(), kommunnummerkod, event);
    }

    Demografi demografi = kommun.getDemografiByÅr().get(år);
    if (demografi == null) {
      demografi = new Demografi();
      demografi.setÅr(år);
      kommun.getDemografiByÅr().put(år, demografi);
    }

    SourcedValue<Integer> befolkningsandel = demografi.getBefolkningsandelarByÅlder().get(ålder);
    if (befolkningsandel == null) {
      befolkningsandel = new SourcedValue<Integer>();
      demografi.getBefolkningsandelarByÅlder().put(ålder, befolkningsandel);
    }
    updateSourcedValue(befolkningsandel, antal, event);

    DomainStore.getInstance().put(kommun);

  }
}
