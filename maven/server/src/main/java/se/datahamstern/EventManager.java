package se.datahamstern;

import com.sleepycat.persist.EntityCursor;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.CommandManager;
import se.datahamstern.command.Event;
import se.datahamstern.command.Source;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author kalle
 * @since 2012-03-04 23:07
 */
public class EventManager {

  private static EventManager instance = new EventManager();

  private EventManager() {
  }

  private File dataPath;

  public static EventManager getInstance() {
    return instance;
  }

  private Writer locallyCreatedEventsAudit;

  public void open() throws Exception {
    if (dataPath == null) {
      throw new NullPointerException("No data path set");
    }

    if (locallyCreatedEventsAudit == null) {
      locallyCreatedEventsAudit = new OutputStreamWriter(new FileOutputStream(new File(System.currentTimeMillis() + ".events.json")));
      locallyCreatedEventsAudit.write("{\n  \"created\" : ");
      locallyCreatedEventsAudit.write(String.valueOf(System.currentTimeMillis()));
      locallyCreatedEventsAudit.write(",\n  \"events\" : [\n");
    }
  }

  public void close() throws Exception {
    if (locallyCreatedEventsAudit != null) {
      locallyCreatedEventsAudit.write("\n],\n  \"closed\" : ");
      locallyCreatedEventsAudit.write(String.valueOf(System.currentTimeMillis()));
      locallyCreatedEventsAudit.write("\n}\n");
      locallyCreatedEventsAudit.close();
    }
  }

  public File getDataPath() {
    return dataPath;
  }

  public void setDataPath(File dataPath) {
    this.dataPath = dataPath;
  }

  public Writer getLocallyCreatedEventsAudit() {
    return locallyCreatedEventsAudit;
  }

  public void setLocallyCreatedEventsAudit(Writer locallyCreatedEventsAudit) {
    this.locallyCreatedEventsAudit = locallyCreatedEventsAudit;
  }

  /**
   * Adds an event to the queue.
   * Next time you call upon {@link se.datahamstern.EventManager#flushQueue()} it will be flushed to your database.
   *
   * @param event
   * @return
   * @throws Exception
   */
  public synchronized Event queue(Event event) throws Exception {
    assertWellDescribedEvent(event);

    if (event.getIdentity() == null) {
      event.setIdentity(EventStore.getInstance().identityFactory());
      locallyCreatedEventsAudit.write(toJSON(event));
      locallyCreatedEventsAudit.write("\n,\n");
    }

    // add to queue of events to be executed,
    // ie also old events that have been updated!
    event.set_local_timestamp(new Date());

    return EventStore.getInstance().getEvents().put(event);
  }

  private void assertWellDescribedEvent(Event event) {
    if (event.getCommandName() == null) {
      throw new NullPointerException("Command name not set in event!");
    }
    if (event.getCommandVersion() == null) {
      throw new NullPointerException("Command version not set in event!");
    }
    if (event.getSources() == null || event.getSources().isEmpty()) {
      throw new NullPointerException("No sources in event!");
    }
    if (event.getJsonData() == null) {
      throw new NullPointerException("No jsonData in event!");
    }

    for (Source source : event.getSources()) {
      if (source.getTimestamp() == null) {
        throw new NullPointerException("Source is missing timestamp! " + source);
      }
      if (source.getAuthor() == null) {
        throw new NullPointerException("Source is missing author! " + source);
      }
      if (source.getLicense() == null) {
        throw new NullPointerException("Source is missing license! " + source);
      }
      if (source.getTrustworthiness() == null) {
        throw new NullPointerException("Source is missing trustworthiness! " + source);
      }
      if (source.getDetails() == null) {
//        log.debug("Source is missing details! " + source);
      }
    }
  }

  private void execute(Event event, JSONParser jsonParser) throws Exception {
    CommandManager.getInstance().commandFactory(event.getCommandName(), event.getCommandVersion()).execute(event, jsonParser);
  }


  // todo persistent
  private Date lastRunStartedTimestamp = new Date(0);

  public synchronized void flushQueue() {

    int totalCounter = 0;
    int failedCounter = 0;

    Date started = new Date();
    Date lastRunStartedTimestamp = this.lastRunStartedTimestamp;

    JSONParser jsonParser = new JSONParser();
    EntityCursor<Event> events = EventStore.getInstance().getEventsByTimestamp().entities(lastRunStartedTimestamp, true, started, false);
    try {
      Event event;
      while ((event = events.next()) != null) {
        totalCounter++;
        try {
          execute(event, jsonParser);

        } catch (Exception e) {
          e.printStackTrace();
          // log.error("Exception while executing event " + event, e);
          failedCounter++;

        }
      }
    } finally {
      events.close();
    }

    this.lastRunStartedTimestamp = started;

    // log.info("Executed " + totalCounter + " events. " + failedCounter + " of them failed.");
  }


  public static Event fromJSON(Reader json) throws Exception {
    // todo BufferedJSONStreamReader jsr = new BufferedJSONStreamReader(json);

    // todo arrays (event logs) would be parsed with less ram,
    // todo commands should be instantiated and used to unmarshal data just the way it expects it.
    // todo it would be faster. lots of ++ here!


    JSONObject object = (JSONObject)new JSONParser().parse(json);
    Event domainEvent = new Event();
    domainEvent.setCommandName(object.getJSONObject("command").getString("name"));
    domainEvent.setCommandVersion(object.getJSONObject("command").getString("version"));
    if (object.get("data") != null) {
      // todo this silly thing would not be needed then
      domainEvent.setJsonData(object.getJSONObject("data").toString());
    }
    JSONArray sources = object.getJSONArray("sources");
    if (sources != null) {
      domainEvent.setSources(new ArrayList<Source>(sources.length()));
      for (int i=0; i<sources.length(); i++) {
        JSONObject source = (JSONObject)sources.get(i);
        Source domainSource = new Source();
        domainSource.setAuthor(source.getString("author"));
        if (source.get("trustworthiness") != null) {
          domainSource.setTrustworthiness(new Double(source.getDouble("trustworthiness")).floatValue());
        }
        domainSource.setDetails(source.getString("details"));
        domainSource.setLicense(source.getString("licence"));
        domainSource.setTimestamp(new Date(source.getLong("timestamp")));
        domainEvent.getSources().add(domainSource);
      }
    }

    return domainEvent;
  }

  public static String toJSON(Event event) throws IOException {
    StringWriter json = new StringWriter(4096);
    writeJSON(json, event);
    return json.toString();
  }

  public static void writeJSON(Writer json, Event event) throws IOException {

    json.write("{");

    json.write("\n  \"command\" : { ");
    json.write("\n    \"name\" : \"");
    json.write(event.getCommandName());
    json.write("\"");
    json.write(",\n    \"version\" : \"");
    json.write(event.getCommandVersion());
    json.write("\"");
    json.write("\n  }");

    json.write(",  \"data\" : ");
    json.write(event.getJsonData());

    json.write(",  \"sources\" : ");
    if (event.getSources() == null || event.getSources().isEmpty()) {
      json.write("null");
    } else {
      json.write("[");
      for (Iterator<Source> sourceIterator = event.getSources().iterator(); sourceIterator.hasNext(); ) {
        Source source = sourceIterator.next();

        json.write("{");


        json.write("\n      \"author\" : ");
        if (source.getAuthor() == null) {
          json.write(" null");
        } else {
          json.write('"');
          json.write(StringEscapeUtils.escapeJavaScript(source.getAuthor()));
          json.write('"');
        }

        json.write(",\n      \"trustworthiness\" :");
        if (source.getTrustworthiness() == null) {
          json.write(" null");
        } else {
          json.write(source.getTrustworthiness().toString());
        }

        json.write(",\n      \"details\" :");
        if (source.getDetails() == null) {
          json.write(" null");
        } else {
          json.write('"');
          json.write(StringEscapeUtils.escapeJavaScript(source.getDetails()));
          json.write('"');
        }

        json.write(",\n      \"licence\" :");
        if (source.getLicense() == null) {
          json.write(" null");
        } else {
          json.write('"');
          json.write(StringEscapeUtils.escapeJavaScript(source.getLicense()));
          json.write('"');
        }

        json.write(",\n      \"timestamp\" :");
        if (source.getTrustworthiness() == null) {
          json.write(" null");
        } else {
          json.write(String.valueOf(source.getTimestamp().getTime()));
        }

        json.write("\n    }");

        if (sourceIterator.hasNext()) {
          json.write(", ");
        }
      }
      json.write("  ]\n");
    }

    json.write("}\n");

  }


}
