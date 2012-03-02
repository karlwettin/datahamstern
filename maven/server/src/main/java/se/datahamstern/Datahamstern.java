package se.datahamstern;

import java.io.File;

/**
 * @author kalle
 * @since 2012-03-02 00:00
 */
public class Datahamstern {

  private static Datahamstern instance = new Datahamstern();

  private Datahamstern() {
  }

  private File homePath;

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

  public String getProperty(String key, String defaultValue) {
    // todo!
    return defaultValue;
  }

  public Integer getProperty(String key, Integer defaultValue) {
    String nullValue = new String();
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

