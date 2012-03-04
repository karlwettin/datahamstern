package se.datahamstern;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-02 00:00
 */
public class Datahamstern {

  private static Datahamstern instance = new Datahamstern();

  private Datahamstern() {
  }

  /** for storing meta data when messing via bsh console, todo move to client session */
  public transient Map glue = new HashMap();

  private File homePath;

  private Properties properties;

  private DomainStore domainStore;
  private EventStore eventStore;

  private String systemUUID;

  public void open() throws Exception {

    systemUUID = getProperty("system.uuid", (String)null);
    if (systemUUID == null) {
      throw new RuntimeException("property system.uuid not set! How about " + UUID.randomUUID().toString());
    }

    if (homePath == null) {
      homePath = new File("./");
    }

    System.out.println("Using home path " + homePath.getAbsolutePath());

    if (domainStore == null) {
      domainStore = new DomainStore();
      domainStore.setPath(new File(homePath, "data/domainStore"));
    }
    domainStore.open();

    if (eventStore == null) {
      eventStore = new EventStore();
      eventStore.setPath(new File(homePath, "data/eventStore"));
    }
    eventStore.open();
  }

  public void close() throws Exception {
    if (domainStore != null) {
      domainStore.close();
    }
    if (eventStore != null) {
      eventStore.close();
    }
  }

  public String getProperty(String key, String defaultValue) throws Exception {
    if (properties == null) {
      properties = new Properties();
      properties.load(getClass().getResourceAsStream("/datahamstern.properties"));
    }
    String value = properties.getProperty(key);
    return value == null ? defaultValue : value;
  }

  private static String nullValue = new String();

  public Integer getProperty(String key, Integer defaultValue) throws Exception {
    String value = getProperty(key, nullValue);
    if (value == nullValue || value == null) {
      return defaultValue;
    } else {
      return Integer.valueOf(value);
    }
  }


  public static Datahamstern getInstance() {
    return instance;
  }

  public DomainStore getDomainStore() {
    return domainStore;
  }

  public void setDomainStore(DomainStore domainStore) {
    this.domainStore = domainStore;
  }

  public File getHomePath() {
    return homePath;
  }

  public void setHomePath(File homePath) {
    this.homePath = homePath;
  }

  public EventStore getEventStore() {
    return eventStore;
  }

  public void setEventStore(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public String getSystemUUID() {
    return systemUUID;
  }

  public void setSystemUUID(String systemUUID) {
    this.systemUUID = systemUUID;
  }
}

