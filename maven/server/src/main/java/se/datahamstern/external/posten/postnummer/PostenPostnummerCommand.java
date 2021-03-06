package se.datahamstern.external.posten.postnummer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Command;
import se.datahamstern.command.CommandManager;
import se.datahamstern.domain.*;
import se.datahamstern.event.Event;
import se.datahamstern.sourced.SourcedValue;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kalle
 * @since 2012-03-07 04:31
 */
public class PostenPostnummerCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  private static AtomicInteger gatunummerHighScore = new AtomicInteger();

  /**
   * A name that combined with the version uniquely describes this command.
   * <p/>
   * <p/>
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   *
   * @see #COMMAND_VERSION
   */
  public static String COMMAND_NAME = "uppdatera med post från postens postnummerdatabas";

  /**
   * A version that combined with the name uniquely describes this command.
   * <p/>
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
  public void execute(Event event, JSONParser jsonParser) throws Exception {

    JSONObject jsonObject = (JSONObject) jsonParser.parse(event.getJsonData());

    String gatunamn = (String) jsonObject.remove("gatunamn");
    String gatunummer = (String) jsonObject.remove("gatunummer");
    String postnummerValue = ((String) jsonObject.remove("postnummer")).replaceAll("\\s+", "");
    String postortValue = (String) jsonObject.remove("postort");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    Postort postort = DomainStore.getInstance().getPostortByNamn().get(postortValue);
    if (postort == null) {
      postort = new Postort();
      // we never update knowledge of postort from this end
      // as it would create so many postings to the bdb
      // todo implement new events for knowledge of postort, created once per postnummer found
      updateSourced(postort, event);
      updateSourcedValue(postort.getNamn(), postortValue, event);
      DomainStore.getInstance().put(postort);
    } else if (Datahamstern.getInstance().isRenderFullySourced()) {
      updateSourced(postort, event);
      updateSourcedValue(postort.getNamn(), postortValue, event);
      DomainStore.getInstance().put(postort);
    }


    Postnummer postnummer = DomainStore.getInstance().getPostnummerByPostnummer().get(postnummerValue);
    if (postnummer == null) {
      postnummer = new Postnummer();
      // we never update knowledge of postnummer from this end
      // as it would create so many postings to the bdb
      updateSourced(postnummer, event);
      updateSourcedValue(postnummer.getPostnummer(), postnummerValue, event);
      updateSourcedValue(postnummer.getPostortIdentity(), postort.getIdentity(), event);
      updateSourcedValue(postnummer.getActive(), true, event);
      DomainStore.getInstance().put(postnummer);
    } else if (Datahamstern.getInstance().isRenderFullySourced()) {
      updateSourced(postnummer, event);
      updateSourcedValue(postnummer.getPostnummer(), postnummerValue, event);
      updateSourcedValue(postnummer.getPostortIdentity(), postort.getIdentity(), event);
      updateSourcedValue(postnummer.getActive(), true, event);
      DomainStore.getInstance().put(postnummer);
    }


    if (gatunamn != null && !gatunamn.isEmpty()
        // gator med namn tävlingspost, frisvar eller svarspost är inte gator
        && !gatunamn.toUpperCase().contains("TÄVLINGSPOST")
        && !gatunamn.toUpperCase().contains("FRISVAR")
        && !gatunamn.toUpperCase().contains("SVARSPOST")
        // gator med namn box är just boxar
        && !"BOX".equalsIgnoreCase(gatunamn)
        // gator utan gatunummer är boxadresser, frisvar, etc
        && gatunummer != null && !gatunummer.trim().isEmpty()) {

      String[] range = gatunummer.split("-");
      int from = Integer.valueOf(range[0].trim());
      int to = Integer.valueOf(range[1].trim());

      int size = (to - from) / 2;
      if (size > gatunummerHighScore.get()) {
        gatunummerHighScore.set(size);
        System.out.println("Gatunummer high score: " + size);
      }


      Gata gata = DomainStore.getInstance().getGatorByNamnAndPostort().get(new Gata.NamnAndPostort(gatunamn, postort.getIdentity()));
      if (gata == null) {
        gata = new Gata();
        // todo implement new event with knowledge of gata once per postort
        updateSourced(gata, event);
        gata.getNamn().set(gatunamn);
        gata.getPostortIdentity().set(postort.getIdentity());
        DomainStore.getInstance().put(gata);
      } else if (Datahamstern.getInstance().isRenderFullySourced()) {
        updateSourced(gata, event);
        gata.getNamn().set(gatunamn);
        gata.getPostortIdentity().set(postort.getIdentity());
        DomainStore.getInstance().put(gata);
      }

      if (!gata.get_index_postnummerIdentities().contains(postnummer.getIdentity())) {
        gata.getPostnummerIdentities().add(new SourcedValue<String>(postnummer.getIdentity()));
        DomainStore.getInstance().put(gata);
      }


      Gatuadress.GataAndGatunummer gataAndGatunummer = new Gatuadress.GataAndGatunummer();
      gataAndGatunummer.setGataIdentity(gata.getIdentity());


      // todo assert that ranges ALWAYS are even or odd sequence!!!
      // ie:
      // if gatunummer starts with odd number it should end with odd number, eg 3 - 7
      // if gatunummer starts with even number it should end with even number, eg 4 - 8
      // gatunummer may also be a single number, eg 4 - 4

      int nummerIncrease = 2;


      for (int nummer = from; nummer < to; nummer += nummerIncrease) {
        gataAndGatunummer.setGatunummer(nummer);
        Gatuadress gatuadress = DomainStore.getInstance().getGatuadressByGataAndGatunummer().get(gataAndGatunummer);
        if (gatuadress == null) {
          gatuadress = new Gatuadress();
        }

        updateSourced(gatuadress, event);
        updateSourcedValue(gatuadress.getPostnummerIdentity(), postnummer.getIdentity(), event);
        updateSourcedValue(gatuadress.getGataIdentity(), gata.getIdentity(), event);
        updateSourcedValue(gatuadress.getGatunummer(), nummer, event);
        DomainStore.getInstance().put(gatuadress);
      }


    } else {
      // todo handle BOX etc
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
