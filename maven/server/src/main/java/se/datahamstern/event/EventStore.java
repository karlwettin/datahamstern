package se.datahamstern.event;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import se.datahamstern.Datahamstern;

import java.io.*;
import java.util.Date;
import java.util.UUID;

/**
 * This is where events are stored.
 *
 * In order to add a new event see {@link EventManager#queue(Event)}
 *
 * @author kalle
 * @since 2012-03-02 03:09
 */
public class EventStore {

  private static EventStore instance = new EventStore();

  private EventStore() {
  }

  public static EventStore getInstance() {
    return instance;
  }

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


  public synchronized String identityFactory() throws Exception {
    StringBuilder identity = new StringBuilder(100);
    identity.append(Datahamstern.getInstance().getSystemUUID());
    identity.append("/");
    identity.append(UUID.randomUUID().toString());
    return identity.toString();
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
