package se.datahamstern;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import se.datahamstern.domain.Organization;

import java.io.File;
import java.io.IOException;

/**
 * @author kalle
 * @since 2012-03-02 03:09
 */
public class EntityStore {

private File path;

  private String storeName = "datahamstern";

  private Environment environment;
  private com.sleepycat.persist.EntityStore entityStore;
  private int cacheMB;
  private boolean readOnly;




  private PrimaryIndex<Long, Organization> organizations;
  private SecondaryIndex<String, Long, Organization> organizationByNumber;



  public void open() throws Exception {

//    log.info("Opening BDB...");

    cacheMB = Integer.valueOf(Datahamstern.getInstance().getProperty("entityStore.cacheMB", "5"));
    readOnly = Boolean.valueOf(Datahamstern.getInstance().getProperty("entityStore.readOnly", "false"));

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

    organizations = entityStore.getPrimaryIndex(Long.class, Organization.class);
    organizationByNumber = entityStore.getSecondaryIndex(organizations, String.class, "number");

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


  public PrimaryIndex<Long, Organization> getOrganizations() {
    return organizations;
  }

  public void setOrganizations(PrimaryIndex<Long, Organization> organizations) {
    this.organizations = organizations;
  }

  public SecondaryIndex<String, Long, Organization> getOrganizationByNumber() {
    return organizationByNumber;
  }

  public void setOrganizationByNumber(SecondaryIndex<String, Long, Organization> organizationByNumber) {
    this.organizationByNumber = organizationByNumber;
  }
}
