package se.datahamstern;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import org.json.simple.parser.JSONParser;
import se.datahamstern.command.CommandManager;
import se.datahamstern.command.Event;
import se.datahamstern.command.Source;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-02 03:09
 */
public class EventStore {

  private File path;

  private String storeName = "datahamstern/events";

  private Environment environment;
  private com.sleepycat.persist.EntityStore entityStore;
  private int cacheMB;
  private boolean readOnly;


  private PrimaryIndex<String, Event> events;
  private SecondaryIndex<Date, String, Event> eventsByTimestamp;


  public void open() throws Exception {

//    log.info("Opening BDB...");

    cacheMB = Integer.valueOf(Datahamstern.getInstance().getProperty("eventStore.cacheMB", "5"));
    if (cacheMB < 5) {
      cacheMB = 5;
    }
    readOnly = Boolean.valueOf(Datahamstern.getInstance().getProperty("eventStore.readOnly", "false"));

    if (!path.exists()) {
//      log.info("Creating directory " + path.getAbsolutePath());
      if (!path.mkdirs()) {
        throw new IOException("Could not create directory " + path.getAbsolutePath());
      }

      EnvironmentConfig envConfig = new EnvironmentConfig();
      envConfig.setAllowCreate(true);
      envConfig.setTransactional(false);
      envConfig.setLocking(true);
      envConfig.setReadOnly(false);


//      log.info("Creating environment " + envConfig.toString());

      environment = new Environment(path, envConfig);

      StoreConfig storeConfig = new StoreConfig();
      storeConfig.setAllowCreate(true);
      storeConfig.setTransactional(false);
      storeConfig.setReadOnly(false);

//      log.info("Creating store '" + storeName + "' " + storeConfig.toString());

      entityStore = new com.sleepycat.persist.EntityStore(environment, storeName, storeConfig);

      entityStore.close();
      environment.close();

//      log.info("BDB has been created");

    }

    // open

    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    envConfig.setTransactional(false);
    envConfig.setLocking(false);
    envConfig.setReadOnly(readOnly);
    envConfig.setCacheSize(cacheMB * 1024 * 1024); //


//    log.info("Opening environment " + envConfig.toString());

    environment = new Environment(path, envConfig);

    StoreConfig storeConfig = new StoreConfig();
    storeConfig.setAllowCreate(true);
    storeConfig.setTransactional(false);
    storeConfig.setReadOnly(readOnly);

//    log.info("Opening store '" + storeName + "' " + storeConfig.toString());

    entityStore = new com.sleepycat.persist.EntityStore(environment, storeName, storeConfig);

    events = entityStore.getPrimaryIndex(String.class, Event.class);
    eventsByTimestamp = entityStore.getSecondaryIndex(events, Date.class, "_local_timestamp");

//    log.info("BDB has been opened.");

  }

  public void close() throws Exception {

//    log.info("Closing BDB...");

    if (entityStore != null) {
      entityStore.close();
    }
    if (environment != null) {
      environment.close();
    }

    entityStore = null;
    environment = null;

//    log.info("BDB has been closed");

  }


  private synchronized String identityFactory() throws Exception {
    StringBuilder identity = new StringBuilder(100);
    identity.append(Datahamstern.getInstance().getSystemUUID());
    identity.append("/");
    identity.append(UUID.randomUUID().toString());
    return identity.toString();
  }

  public Event put(Event event) throws Exception {
    if (event.getIdentity() == null) {
      event.setIdentity(identityFactory());
    }
    assertWellDescribedEvent(event);

    // add to queue of events to be executed
    event.set_local_timestamp(new Date());

    // todo save audit log serialized as json.

    return events.put(event);
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

  public synchronized void executeUpdatedEvents() {

    int totalCounter = 0;
    int failedCounter = 0;

    Date started = new Date();
    Date lastRunStartedTimestamp = this.lastRunStartedTimestamp;

    JSONParser jsonParser = new JSONParser();
    EntityCursor<Event> events = Datahamstern.getInstance().getEventStore().getEventsByTimestamp().entities(lastRunStartedTimestamp, true, started, false);
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


  public File getPath() {
    return path;
  }

  public void setPath(File path) {
    this.path = path;
  }

  public int getCacheMB() {
    return cacheMB;
  }

  public void setCacheMB(int cacheMB) {
    this.cacheMB = cacheMB;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public PrimaryIndex<String, Event> getEvents() {
    return events;
  }

  public void setEvents(PrimaryIndex<String, Event> events) {
    this.events = events;
  }

  public SecondaryIndex<Date, String, Event> getEventsByTimestamp() {
    return eventsByTimestamp;
  }

  public void setEventsByTimestamp(SecondaryIndex<Date, String, Event> eventsByTimestamp) {
    this.eventsByTimestamp = eventsByTimestamp;
  }
}
