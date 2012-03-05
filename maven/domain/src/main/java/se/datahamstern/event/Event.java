package se.datahamstern.event;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.command.Source;

import java.util.Date;
import java.util.List;

/**
 * En händelse, dvs
 *
 * en föränding av något i databasen
 * tillammans med stöd för fullständig hänvisning om källan eller källorna till informationen
 * och vem som hittade informationen, exempelvis en robot som gruvar i existerande datamängd, en skördeströska eller en människa.
 *
 *
 * {
 *   "identity" : "kalles server/länsroboten/dspofiuasdpf98as7df978asdf987sdsd97",
 *   "command" : {
 *     "name" : "uppdatera län",
 *     "version" : "1",
 *   },
 *   "sources" : [{
 *     "author" : "janne1965@wikipedia.se",
 *     "trustworthiness" : 0.89,
 *     "details" : "ingen har ändrat på artikeln sedan tre månader då janne1965 städade efter ett troll",
 *     "license" : "public domain",
 *     "timestamp" : 1221112888483
 *   }, {
 *     "author" : "nominatim@open street map",
 *     "trustworthiness" : 0.4,
 *     "details" : "sökte upp 'stockholms län' med open street maps geocoder och fick en rektangel  som polygonsvar",
 *     "license" : "creative commons",
 *     "timestamp" : 1221112943233
 *   }],
 *   "data" : {
 *     "sifferkod" : "01",
 *     "bokstavskod" : "A",
 *     "namn" : "Stockholms län"
 *   }
 * }
 *
 * @author kalle
 * @since 2012-03-03 22:21
 */
@Entity(version = 1)
public class Event {

  /**
   * when this event was put to the local bdb event store
   * used to find out what events to execute
   */
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private Date _local_timestamp;

  private String commandName;
  private String commandVersion;

  /**
   * den första instansen är den primära källan ,
   * dvs vem det var som skapade den här händelsen
   **/
  private List<Source> sources;

  private String jsonData;


  /**
   * internal identity
   */
  @PrimaryKey
  private String identity;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Event event = (Event) o;

    if (identity != null ? !identity.equals(event.identity) : event.identity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return identity != null ? identity.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Event{" +
        "commandName='" + commandName + '\'' +
        ", commandVersion='" + commandVersion + '\'' +
        ", sources=" + sources +
        ", jsonData='" + jsonData + '\'' +
        ", identity='" + identity + '\'' +
        ", _local_timestamp=" + _local_timestamp +
        '}';
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }


  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  public String getCommandVersion() {
    return commandVersion;
  }

  public void setCommandVersion(String commandVersion) {
    this.commandVersion = commandVersion;
  }

  public List<Source> getSources() {
    return sources;
  }

  public void setSources(List<Source> sources) {
    this.sources = sources;
  }

  public String getJsonData() {
    return jsonData;
  }

  public void setJsonData(String jsonData) {
    this.jsonData = jsonData;
  }

  public Date get_local_timestamp() {
    return _local_timestamp;
  }

  public void set_local_timestamp(Date _local_timestamp) {
    this._local_timestamp = _local_timestamp;
  }
}
