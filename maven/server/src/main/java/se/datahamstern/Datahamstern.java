package se.datahamstern;

import se.datahamstern.domain.DomainStore;
import se.datahamstern.event.EventQueue;
import se.datahamstern.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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

      EventQueue.getInstance().pollInbox();
      EventQueue.getInstance().flushQueue();

      System.currentTimeMillis();

    } finally {
      getInstance().close();
    }

  }

  private static Datahamstern instance = new Datahamstern();

  private Datahamstern() {
  }

  /**
   * what version of datahamster is running
   *
   * todo imprint this on all outgoing events
   */
  private String version;

  /**
   * unique identifier for this system.
   *
   * it would be nice
   * if everybody used a contactable email address
   *
   * google.account.name+datahamstern-1@gmail.com
   * google.account.name+datahamstern-2@gmail.com
   *
   * todo imprint this on all outgoing events
   */
  private String systemUUID;

  /**
   * the maven home directory
   */
  private File mavenProjectPath;
  /**
   * where bdb, events, etc is stored
   */
  private File dataPath;

  private Properties properties;


  public void open() throws Exception {

    systemUUID = getProperty("system.uuid", (String) null);
    if (systemUUID == null) {
      throw new RuntimeException("property system.uuid not set! How about " + UUID.randomUUID().toString());
    }

    if (mavenProjectPath == null) {
      mavenProjectPath = new File("./");
    }
    mavenProjectPath = FileUtils.getCleanAbsolutePath(mavenProjectPath);
    if (!mavenProjectPath.exists()) {
      throw new IOException("maven project path " + mavenProjectPath.getAbsolutePath() + " does not exist!");
    } else if (!mavenProjectPath.isDirectory()) {
      throw new IOException("maven project path " + mavenProjectPath.getAbsolutePath() + " is not a directory!");
    } else {
      File[] poms = mavenProjectPath.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return "pom.xml".equalsIgnoreCase(name);
        }
      });
      if (poms.length == 0) {
        throw new IOException("maven project path " + mavenProjectPath.getAbsolutePath() + " does not contain a pom.xml!");
      }
      // todo load pom to dom, assert its the right project and set version
      version = "0.0.0";
    }

    System.out.println("Using maven project path " + mavenProjectPath.getAbsolutePath());

    if (dataPath == null) {
      dataPath = new File(mavenProjectPath, "data");
      FileUtils.mkdirs(dataPath);
    }
    dataPath = FileUtils.getCleanAbsolutePath(dataPath);


    System.out.println("Using data path " + dataPath.getAbsolutePath());


    DomainStore.getInstance().setPath(new File(dataPath, "domain/store/bdb"));
    DomainStore.getInstance().open();


    EventQueue.getInstance().setDataPath(new File(dataPath, "event"));
    EventQueue.getInstance().open();

  }

  public void close() throws Exception {
    DomainStore.getInstance().close();
    EventQueue.getInstance().close();
  }

  public String getProperty(String key, String defaultValue) throws Exception {
    if (properties == null) {
      properties = new Properties();
      properties.load(getClass().getResourceAsStream("/datahamstern.properties"));
    }
    String value = properties.getProperty(key);
    return value == null ? defaultValue : value;
  }

  private static String defaultPropertyNullValue = new String();

  public Integer getProperty(String key, Integer defaultValue) throws Exception {
    String value = getProperty(key, defaultPropertyNullValue);
    if (value == defaultPropertyNullValue || value == null) {
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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}

