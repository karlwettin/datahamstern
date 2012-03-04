package se.datahamstern;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainObjectVisitor;
import se.datahamstern.domain.Lan;
import se.datahamstern.domain.Organisation;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-02 03:09
 */
public class DomainStore {

  private File path;

  private String storeName = "datahamstern/domain";

  private Environment environment;
  private com.sleepycat.persist.EntityStore entityStore;
  private int cacheMB;
  private boolean readOnly;


  private PrimaryIndex<String, Lan> län;
  private SecondaryIndex<String, String, Lan> länByNummerkod;
  private SecondaryIndex<String, String, Lan> länByAlfakod;


  private PrimaryIndex<String, Organisation> organisationer;
  private SecondaryIndex<String, String, Organisation> organisationByNummer;
  private SecondaryIndex<String, String, Organisation> organisationerByLän;


  public void open() throws Exception {

//    log.info("Opening BDB...");

    cacheMB = Integer.valueOf(Datahamstern.getInstance().getProperty("domainStore.cacheMB", "50"));
    if (cacheMB < 5) {
      cacheMB = 5;
    }

    readOnly = Boolean.valueOf(Datahamstern.getInstance().getProperty("domainStore.readOnly", "false"));

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

    organisationer = entityStore.getPrimaryIndex(String.class, Organisation.class);
    organisationByNummer = entityStore.getSecondaryIndex(organisationer, String.class, "_index_nummer");
    organisationerByLän = entityStore.getSecondaryIndex(organisationer, String.class, "_index_länIdentity");

    län = entityStore.getPrimaryIndex(String.class, Lan.class);
    länByAlfakod = entityStore.getSecondaryIndex(län, String.class, "_index_alfakod");
    länByNummerkod = entityStore.getSecondaryIndex(län, String.class, "_index_nummerkod");

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

  public void put(DomainEntityObject object) {
    object.accept(putVisitor);
  }

  public void assignIdentity(DomainEntityObject object) {
    if (object.getIdentity() == null) {
      object.setIdentity(UUID.randomUUID().toString());
    }
  }

  private DomainObjectVisitor putVisitor = new DomainObjectVisitor() {
    @Override
    public void visit(Lan län) {
      assignIdentity(län);
      if (län.getAlfakod().get() != null) {
        län.set_index_alfakod(län.getAlfakod().get());
      }
      if (län.getNummerkod().get() != null) {
        län.set_index_nummerkod(län.getNummerkod().get());
      }
      getLän().put(län);
    }

    @Override
    public void visit(Organisation organisation) {
      assignIdentity(organisation);
      if (organisation.getNummer().get() != null) {
        organisation.set_index_nummer(organisation.getNummer().get());
      }
      if (organisation.getLänIdentity().get() != null) {
        organisation.set_index_länIdentity(organisation.getLänIdentity().get());
      }
      getOrganisationer().put(organisation);
    }

  };


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

  public PrimaryIndex<String, Organisation> getOrganisationer() {
    return organisationer;
  }

  public void setOrganisationer(PrimaryIndex<String, Organisation> organisationer) {
    this.organisationer = organisationer;
  }

  public SecondaryIndex<String, String, Organisation> getOrganisationByNummer() {
    return organisationByNummer;
  }

  public void setOrganisationByNummer(SecondaryIndex<String, String, Organisation> organisationByNummer) {
    this.organisationByNummer = organisationByNummer;
  }

  public SecondaryIndex<String, String, Organisation> getOrganisationerByLän() {
    return organisationerByLän;
  }

  public void setOrganisationerByLän(SecondaryIndex<String, String, Organisation> organisationerByLän) {
    this.organisationerByLän = organisationerByLän;
  }

  public PrimaryIndex<String, Lan> getLän() {
    return län;
  }

  public void setLän(PrimaryIndex<String, Lan> län) {
    this.län = län;
  }

  public SecondaryIndex<String, String, Lan> getLänByNummerkod() {
    return länByNummerkod;
  }

  public void setLänByNummerkod(SecondaryIndex<String, String, Lan> länByNummerkod) {
    this.länByNummerkod = länByNummerkod;
  }

  public SecondaryIndex<String, String, Lan> getLänByAlfakod() {
    return länByAlfakod;
  }

  public void setLänByAlfakod(SecondaryIndex<String, String, Lan> länByAlfakod) {
    this.länByAlfakod = länByAlfakod;
  }
}
