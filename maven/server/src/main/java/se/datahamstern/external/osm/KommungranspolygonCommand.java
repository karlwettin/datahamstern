package se.datahamstern.external.osm;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.command.Command;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Kommun;
import se.datahamstern.domain.Koordinat;
import se.datahamstern.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2012-05-26 15:38
 */
public class KommungranspolygonCommand extends Command {

  // private static Logger log = LoggerFactory.getLogger(MyCommand.class);

  /**
   * A name that combined with the version uniquely describes this command.
   * <p/>
   * If this value change (ie only by editing the code and recompile),
   * then version should also be set to 1.
   *
   * @see #COMMAND_VERSION
   */
  public static String COMMAND_NAME = "uppdatera med kommungräns från openstreetmap";

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

    String kommunnummerkod = (String)jsonObject.remove("kommunnummerkod");
    JSONArray jsonPolygon = (JSONArray)jsonObject.remove("polygon");

    if (!jsonObject.isEmpty()) {
      throw new RuntimeException("Unknown fields left in json: " + jsonObject.toJSONString());
    }

    Kommun kommun = DomainStore.getInstance().getKommunByNummerkod().get(kommunnummerkod);
    if (kommun == null) {
      kommun = new Kommun();
      updateSourcedValue(kommun.getNummerkod(), kommunnummerkod, event);
      updateSourced(kommun, event);
    } else if (Datahamstern.getInstance().isRenderFullySourced()) {
      updateSourcedValue(kommun.getNummerkod(), kommunnummerkod, event);
      updateSourced(kommun, event);
    }

    List<Koordinat> polygon = new ArrayList<Koordinat>(jsonPolygon.size());
    for (int i=0; i<jsonPolygon.size(); i++) {
      JSONObject jsonPolygonkoordinat = (JSONObject)jsonPolygon.get(i);
      Double latitude = (Double)jsonPolygonkoordinat.remove("latitud");
      Double longitude = (Double)jsonPolygonkoordinat.remove("longitud");

      if (!jsonObject.isEmpty()) {
        throw new RuntimeException("Unknown fields left in json koordinat: " + jsonObject.toJSONString());
      }

      polygon.add(new Koordinat(latitude, longitude));
    }

    updateSourcedValue(kommun.getGeografi().getPolygon(), polygon, event);

    DomainStore.getInstance().put(kommun);

  }
}