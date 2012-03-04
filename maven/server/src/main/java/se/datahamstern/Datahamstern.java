package se.datahamstern;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-02 00:00
 */
public class Datahamstern {

  public static void main(String[] args) throws Exception {

    getInstance().open();
    try {

      System.currentTimeMillis();

    } finally {
      getInstance().close();
    }

  }

  private static Datahamstern instance = new Datahamstern();

  private Datahamstern() {
  }

  /** the maven home directory */
  private File mavenProjectPath;
  /** where bdb, events, etc is stored  */
  private File dataPath;

  private Properties properties;


  private String systemUUID;

  public void open() throws Exception {

    systemUUID = getProperty("system.uuid", (String)null);
    if (systemUUID == null) {
      throw new RuntimeException("property system.uuid not set! How about " + UUID.randomUUID().toString());
    }

    if (mavenProjectPath == null) {
      mavenProjectPath = new File("./");
    }

    System.out.println("Using maven project path " + mavenProjectPath.getAbsolutePath());

    if (dataPath == null) {
      dataPath = new File(mavenProjectPath, "data");
      if (!dataPath.exists()  && !dataPath.mkdirs()) {
        throw new IOException("Could not mkdirs " + dataPath.getAbsolutePath());
      }
    }

    System.out.println("Using data path " + dataPath.getAbsolutePath());




    DomainStore.getInstance().setPath(new File(dataPath, "domainStore/bdb"));
    DomainStore.getInstance().open();

    EventStore.getInstance().setPath(new File(dataPath, "eventStore/bdb"));
    EventStore.getInstance().open();

    EventManager.getInstance().setDataPath(new File(dataPath, "eventManager"));
    EventManager.getInstance().open();

  }

  public void close() throws Exception {
    DomainStore.getInstance().close();
    EventStore.getInstance().close();
    EventManager.getInstance().close();
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


  public File getMavenProjectPath() {
    return mavenProjectPath;
  }

  public void setMavenProjectPath(File mavenProjectPath) {
    this.mavenProjectPath = mavenProjectPath;
  }


  public String getSystemUUID() {
    return systemUUID;
  }

  public void setSystemUUID(String systemUUID) {
    this.systemUUID = systemUUID;
  }

  public File getDataPath() {
    return dataPath;
  }

  public void setDataPath(File dataPath) {
    this.dataPath = dataPath;
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }
}

