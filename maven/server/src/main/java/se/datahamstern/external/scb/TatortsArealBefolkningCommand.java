package se.datahamstern.external.scb;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.Command;
import se.datahamstern.domain.Demografi;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Organisation;
import se.datahamstern.domain.Ort;
import se.datahamstern.event.Event;
import se.datahamstern.sourced.SourcedValue;

/**
 * @author kalle
 * @since 2012-04-17 15:01
 */
public class TatortsArealBefolkningCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * <p/>
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   *
   * @see #COMMAND_VERSION
   */
  public static String COMMAND_NAME = "uppdatera med post från scbs tätorts befolkning och landareal xls";

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

    String tätortskod = (String) jsonObject.remove("tätortskod");
    int år = (Integer) jsonObject.remove("år");
    int befolkning = (Integer) jsonObject.remove("befolkning");
    double hektarLandareal = ((Number) jsonObject.remove("hektarLandareal")).doubleValue();

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    Ort ort = DomainStore.getInstance().getOrtByTätortskod().get(tätortskod);
    if (ort == null) {
      ort = new Ort();
    }
    updateSourced(ort, event);

    updateSourcedValue(ort.getTätortskod(), tätortskod, event);


    Demografi demografi = ort.getDemografiByÅr().get(år);
    if (demografi == null) {
      demografi = new Demografi();
      demografi.setÅr(år);
      ort.getDemografiByÅr().put(år, demografi);
    }
    updateSourcedValue(demografi.getInvånare(), befolkning, event);


    updateSourcedValue(ort.getGeografi().getKvadratkilometerLandareal(), hektarLandareal / 100d, event);

    DomainStore.getInstance().put(ort);

  }
}
