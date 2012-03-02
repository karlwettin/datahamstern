package se.datahamstern;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

  private EntityStore entityStore;


  public void open() throws Exception {


    if (homePath == null) {
      homePath = new File("./");
    }

    System.out.println("Using home path " + homePath.getAbsolutePath());

    if (entityStore == null) {
      entityStore = new EntityStore();
      entityStore.setPath(new File(homePath, "data/entityStore"));
    }
    entityStore.open();
  }

  public void close() throws Exception {
    if (entityStore != null) {
      entityStore.close();
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

  public EntityStore getEntityStore() {
    return entityStore;
  }

  public void setEntityStore(EntityStore entityStore) {
    this.entityStore = entityStore;
  }

  public File getHomePath() {
    return homePath;
  }

  public void setHomePath(File homePath) {
    this.homePath = homePath;
  }
}

