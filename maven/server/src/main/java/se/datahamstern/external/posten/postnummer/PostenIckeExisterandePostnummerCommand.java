package se.datahamstern.external.posten.postnummer;

import com.sleepycat.persist.EntityCursor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Command;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Gata;
import se.datahamstern.domain.Gatuadress;
import se.datahamstern.domain.Postnummer;
import se.datahamstern.event.Event;
import se.datahamstern.sourced.SourcedValue;

import java.util.Iterator;

/**
 * @author kalle
 * @since 2012-04-16 10:47
 */
public class PostenIckeExisterandePostnummerCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   *
   * @see #COMMAND_VERSION
   *      <p/>
   *      If this value change (ie only by editing the code and recompile),
   *      then version should also be set to 1.
   */
  public static String COMMAND_NAME = "uppdatera med avsaknad postnummerpost från postens postnummerdatabas";

  /**
   * A version that combined with the name uniquely describes this command.
   * <p/>
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

      if (Datahamstern.getInstance().isRenderFullySourced()) {
        updateSourced(postnummer, event);
        updateSourcedValue(postnummer.getPostnummer(), postnummerValue, event);
      }

      // todo implement test cases!

      // find all use of this postnummer and set association to null!
      // currently this means all instances of Gata and Gatuadress

      Gata gata;
      EntityCursor<Gata> gator = DomainStore.getInstance().getGatorByPostnummer().entities(postnummer.getIdentity(), true, postnummer.getIdentity(), true);
      try {
        while ((gata = gator.next()) != null) {
          Iterator<SourcedValue<String>> postnummerIdentityIterator = gata.getPostnummerIdentities().iterator();
          while (postnummerIdentityIterator.hasNext()) {
            SourcedValue<String> postnummerIdentity = postnummerIdentityIterator.next();
            if (postnummer.getIdentity().equals(postnummerIdentity.get())) {
              postnummerIdentityIterator.remove();
              break;
            }
          }
          DomainStore.getInstance().put(gata);
        }
      } finally {
        gator.close();
      }

      Gatuadress gatuadress;
      EntityCursor<Gatuadress> gatuadresser = DomainStore.getInstance().getGatuadresserByPostnummer().entities(postnummer.getIdentity(), true, postnummer.getIdentity(), true);
      try {
        while ((gatuadress = gatuadresser.next()) != null) {
          updateSourcedValue(gatuadress.getGataIdentity(), null, event);
          DomainStore.getInstance().put(gata);
        }
      } finally {
        gator.close();
      }

    }

    updateSourcedValue(postnummer.getPostortIdentity(), null, event);
    updateSourcedValue(postnummer.getActive(), false, event);

    DomainStore.getInstance().put(postnummer);

  }
}
