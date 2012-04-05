package se.datahamstern.domain;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import se.datahamstern.Datahamstern;
import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gata;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;
import se.datahamstern.io.FileUtils;

import java.io.File;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-02 03:09
 */
public class DomainStore {

  private static DomainStore instance = new DomainStore();

  private DomainStore() {
  }

  public static DomainStore getInstance() {
    return instance;
  }


  private File path;

  private String storeName = "datahamstern/domain";

  private Environment environment;
  private com.sleepycat.persist.EntityStore entityStore;
  private int cacheMB;
  private boolean readOnly;


  private PrimaryIndex<String, Lan> län;
  private SecondaryIndex<String, String, Lan> länByNamn;
  private SecondaryIndex<String, String, Lan> länByNummerkod;
  private SecondaryIndex<String, String, Lan> länByAlfakod;

  private PrimaryIndex<String, Kommun> kommuner;
  private SecondaryIndex<String, String, Kommun> kommunByNamn;
  private SecondaryIndex<String, String, Kommun> kommunByNummerkod;
  private SecondaryIndex<String, String, Kommun> kommunerByLän;

  private PrimaryIndex<String, Ort> orter;
  private SecondaryIndex<String, String, Ort> orterByKommun;
  private SecondaryIndex<String, String, Ort> ortByTätortskod;

  private PrimaryIndex<String, Organisation> organisationer;
  private SecondaryIndex<String, String, Organisation> organisationByNummer;
  private SecondaryIndex<String, String, Organisation> organisationerByLän;

  private PrimaryIndex<String, Arsredovisning> årsredovisningar;
  private PrimaryIndex<String, EkonomiskPlan> ekonomiskaPlaner;
  private PrimaryIndex<String, Stadgar> stadgar;

  private PrimaryIndex<String, Dokument> dokument;
  private PrimaryIndex<String, Dokumentversion> dokumentversioner;

  private PrimaryIndex<String, Postort> postorter;
  private SecondaryIndex<String, String, Postort> postortByNamn;

  private PrimaryIndex<String, Gatuadress> gatuadresser;
  private SecondaryIndex<String, String, Gatuadress> gatuadresserByPostnummer;
  private SecondaryIndex<Gatuadress.GataAndGatunummer, String, Gatuadress> gatuadressByGataAndGatunummer;
  private SecondaryIndex<String, String, Gatuadress> gatuadresserByGata;

  private PrimaryIndex<String, Gata> gator;
  private SecondaryIndex<String, String, Gata> gatorByPostnummer;
  private SecondaryIndex<String, String, Gata> gatorByPostort;
  private SecondaryIndex<Gata.NamnAndPostort, String, Gata> gatorByNamnAndPostort;

  private PrimaryIndex<String, Postnummer> postnummer;
  private SecondaryIndex<String, String, Postnummer> postnummerByPostnummer;
  private SecondaryIndex<String, String, Postnummer> postnummerByPostort;


  public void open() throws Exception {

//    InstantiatedDomainStore store = new InstantiatedDomainStore();
//    entityStore = store.new EntityStore(storeName);

    entityStore = jeEntityStoreFactory();

    organisationer = entityStore.getPrimaryIndex(String.class, Organisation.class);
    organisationByNummer = entityStore.getSecondaryIndex(organisationer, String.class, "_index_nummer");
    organisationerByLän = entityStore.getSecondaryIndex(organisationer, String.class, "_index_länIdentity");

    län = entityStore.getPrimaryIndex(String.class, Lan.class);
    länByNamn = entityStore.getSecondaryIndex(län, String.class, "_index_namn");
    länByAlfakod = entityStore.getSecondaryIndex(län, String.class, "_index_alfakod");
    länByNummerkod = entityStore.getSecondaryIndex(län, String.class, "_index_nummerkod");

    kommuner = entityStore.getPrimaryIndex(String.class, Kommun.class);
    kommunByNamn = entityStore.getSecondaryIndex(kommuner, String.class, "_index_namn");
    kommunByNummerkod = entityStore.getSecondaryIndex(kommuner, String.class, "_index_nummerkod");
    kommunerByLän = entityStore.getSecondaryIndex(kommuner, String.class, "_index_länIdentity");

    orter = entityStore.getPrimaryIndex(String.class, Ort.class);
    orterByKommun = entityStore.getSecondaryIndex(orter, String.class, "_index_kommunIdentity");
    ortByTätortskod = entityStore.getSecondaryIndex(orter, String.class, "_index_tätortskod");

    gatuadresser = entityStore.getPrimaryIndex(String.class, Gatuadress.class);
    gatuadresserByPostnummer = entityStore.getSecondaryIndex(gatuadresser, String.class, "_index_postnummerIdentity");
    gatuadressByGataAndGatunummer = entityStore.getSecondaryIndex(gatuadresser, Gatuadress.GataAndGatunummer.class, "_index_gataAndGatunummer");
    gatuadresserByGata = entityStore.getSecondaryIndex(gatuadresser, String.class, "_index_gataIdentity");

    gator = entityStore.getPrimaryIndex(String.class, Gata.class);
    gatorByNamnAndPostort = entityStore.getSecondaryIndex(gator, Gata.NamnAndPostort.class, "_index_namnAndPostort");
    gatorByPostort = entityStore.getSecondaryIndex(gator, String.class, "_index_postortIdentity");
    gatorByPostnummer = entityStore.getSecondaryIndex(gator, String.class, "_index_postnummerIdentities");

    postnummer = entityStore.getPrimaryIndex(String.class, Postnummer.class);
    postnummerByPostnummer = entityStore.getSecondaryIndex(postnummer, String.class, "_index_postnummer");
    postnummerByPostort = entityStore.getSecondaryIndex(postnummer, String.class, "_index_postortIdentity");

    postorter = entityStore.getPrimaryIndex(String.class, Postort.class);
    postortByNamn = entityStore.getSecondaryIndex(postorter, String.class, "_index_namn");


    // hydda stuff, todo not quite implemented yet
    årsredovisningar = entityStore.getPrimaryIndex(String.class, Arsredovisning.class);
    ekonomiskaPlaner = entityStore.getPrimaryIndex(String.class, EkonomiskPlan.class);
    stadgar = entityStore.getPrimaryIndex(String.class, Stadgar.class);
    dokument = entityStore.getPrimaryIndex(String.class, Dokument.class);
    dokumentversioner = entityStore.getPrimaryIndex(String.class, Dokumentversion.class);


//    log.info("BDB has been opened.");

  }

  private EntityStore jeEntityStoreFactory() throws Exception {
    //    log.info("Opening BDB...");

    cacheMB = Integer.valueOf(Datahamstern.getInstance().getProperty("domainStore.cacheMB", "50"));
    if (cacheMB < 5) {
      cacheMB = 5;
    }

    readOnly = Boolean.valueOf(Datahamstern.getInstance().getProperty("domainStore.readOnly", "false"));

    if (!path.exists()) {
      path = FileUtils.mkdirs(path);

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

      entityStore = new EntityStore(environment, storeName, storeConfig);

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

    return new EntityStore(environment, storeName, storeConfig);
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

  public void put(DomainEntityObject object)  throws Exception{
    object.accept(putVisitor);
  }

  public void assignIdentity(DomainEntityObject object) {
    if (object.getIdentity() == null) {
      object.setIdentity(UUID.randomUUID().toString());
    }
  }

  /**
   * Ensures that values for all secondary indices are set
   * and that instances are added to the correct primary index
   */
  private DomainEntityObjectVisitor putVisitor = new CountingDomainEntityObjectVisitor(new DomainEntityObjectPutVisitor());


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

  public PrimaryIndex<String, Arsredovisning> getÅrsredovisningar() {
    return årsredovisningar;
  }

  public SecondaryIndex<String, String, Gatuadress> getGatuadresserByGata() {
    return gatuadresserByGata;
  }

  public void setGatuadresserByGata(SecondaryIndex<String, String, Gatuadress> gatuadresserByGata) {
    this.gatuadresserByGata = gatuadresserByGata;
  }

  public PrimaryIndex<String, Gata> getGator() {
    return gator;
  }

  public void setGator(PrimaryIndex<String, Gata> gator) {
    this.gator = gator;
  }

  public SecondaryIndex<String, String, Gata> getGatorByPostnummer() {
    return gatorByPostnummer;
  }

  public void setGatorByPostnummer(SecondaryIndex<String, String, Gata> gatorByPostnummer) {
    this.gatorByPostnummer = gatorByPostnummer;
  }

  public SecondaryIndex<String, String, Gata> getGatorByPostort() {
    return gatorByPostort;
  }

  public void setGatorByPostort(SecondaryIndex<String, String, Gata> gatorByPostort) {
    this.gatorByPostort = gatorByPostort;
  }

  public SecondaryIndex<Gata.NamnAndPostort, String, Gata> getGatorByNamnAndPostort() {
    return gatorByNamnAndPostort;
  }

  public void setGatorByNamnAndPostort(SecondaryIndex<Gata.NamnAndPostort, String, Gata> gatorByNamnAndPostort) {
    this.gatorByNamnAndPostort = gatorByNamnAndPostort;
  }

  public void setÅrsredovisningar(PrimaryIndex<String, Arsredovisning> årsredovisningar) {
    this.årsredovisningar = årsredovisningar;
  }

  public PrimaryIndex<String, EkonomiskPlan> getEkonomiskaPlaner() {
    return ekonomiskaPlaner;
  }

  public void setEkonomiskaPlaner(PrimaryIndex<String, EkonomiskPlan> ekonomiskaPlaner) {
    this.ekonomiskaPlaner = ekonomiskaPlaner;
  }

  public PrimaryIndex<String, Stadgar> getStadgar() {
    return stadgar;
  }

  public void setStadgar(PrimaryIndex<String, Stadgar> stadgar) {
    this.stadgar = stadgar;
  }

  public PrimaryIndex<String, Dokument> getDokument() {
    return dokument;
  }

  public void setDokument(PrimaryIndex<String, Dokument> dokument) {
    this.dokument = dokument;
  }

  public PrimaryIndex<String, Dokumentversion> getDokumentversioner() {
    return dokumentversioner;
  }

  public void setDokumentversioner(PrimaryIndex<String, Dokumentversion> dokumentversioner) {
    this.dokumentversioner = dokumentversioner;
  }

  public PrimaryIndex<String, Kommun> getKommuner() {
    return kommuner;
  }

  public void setKommuner(PrimaryIndex<String, Kommun> kommuner) {
    this.kommuner = kommuner;
  }

  public SecondaryIndex<String, String, Kommun> getKommunerByLän() {
    return kommunerByLän;
  }

  public void setKommunerByLän(SecondaryIndex<String, String, Kommun> kommunerByLän) {
    this.kommunerByLän = kommunerByLän;
  }

  public PrimaryIndex<String, Ort> getOrter() {
    return orter;
  }

  public void setOrter(PrimaryIndex<String, Ort> orter) {
    this.orter = orter;
  }

  public SecondaryIndex<String, String, Ort> getOrterByKommun() {
    return orterByKommun;
  }

  public void setOrterByKommun(SecondaryIndex<String, String, Ort> orterByKommun) {
    this.orterByKommun = orterByKommun;
  }

  public SecondaryIndex<String, String, Kommun> getKommunByNummerkod() {
    return kommunByNummerkod;
  }

  public void setKommunByNummerkod(SecondaryIndex<String, String, Kommun> kommunByNummerkod) {
    this.kommunByNummerkod = kommunByNummerkod;
  }

  public PrimaryIndex<String, Gatuadress> getGatuadresser() {
    return gatuadresser;
  }

  public void setGatuadresser(PrimaryIndex<String, Gatuadress> gatuadresser) {
    this.gatuadresser = gatuadresser;
  }

  public SecondaryIndex<String, String, Gatuadress> getGatuadresserByPostnummer() {
    return gatuadresserByPostnummer;
  }

  public void setGatuadresserByPostnummer(SecondaryIndex<String, String, Gatuadress> gatuadresserByPostnummer) {
    this.gatuadresserByPostnummer = gatuadresserByPostnummer;
  }

  public PrimaryIndex<String, Postnummer> getPostnummer() {
    return postnummer;
  }

  public void setPostnummer(PrimaryIndex<String, Postnummer> postnummer) {
    this.postnummer = postnummer;
  }

  public SecondaryIndex<String, String, Postnummer> getPostnummerByPostnummer() {
    return postnummerByPostnummer;
  }

  public void setPostnummerByPostnummer(SecondaryIndex<String, String, Postnummer> postnummerByPostnummer) {
    this.postnummerByPostnummer = postnummerByPostnummer;
  }

  public SecondaryIndex<String, String, Postnummer> getPostnummerByPostort() {
    return postnummerByPostort;
  }

  public void setPostnummerByPostort(SecondaryIndex<String, String, Postnummer> postnummerByPostort) {
    this.postnummerByPostort = postnummerByPostort;
  }

  public SecondaryIndex<Gatuadress.GataAndGatunummer, String, Gatuadress> getGatuadressByGataAndGatunummer() {
    return gatuadressByGataAndGatunummer;
  }

  public void setGatuadressByGataAndGatunummer(SecondaryIndex<Gatuadress.GataAndGatunummer, String, Gatuadress> gatuadressByGataAndGatunummer) {
    this.gatuadressByGataAndGatunummer = gatuadressByGataAndGatunummer;
  }

  public PrimaryIndex<String, Postort> getPostorter() {
    return postorter;
  }

  public void setPostorter(PrimaryIndex<String, Postort> postorter) {
    this.postorter = postorter;
  }

  public SecondaryIndex<String, String, Postort> getPostortByNamn() {
    return postortByNamn;
  }

  public void setPostortByNamn(SecondaryIndex<String, String, Postort> postortByNamn) {
    this.postortByNamn = postortByNamn;
  }

  public SecondaryIndex<String, String, Lan> getLänByNamn() {
    return länByNamn;
  }

  public void setLänByNamn(SecondaryIndex<String, String, Lan> länByNamn) {
    this.länByNamn = länByNamn;
  }

  public SecondaryIndex<String, String, Kommun> getKommunByNamn() {
    return kommunByNamn;
  }

  public void setKommunByNamn(SecondaryIndex<String, String, Kommun> kommunByNamn) {
    this.kommunByNamn = kommunByNamn;
  }

  public SecondaryIndex<String, String, Ort> getOrtByTätortskod() {
    return ortByTätortskod;
  }

  public void setOrtByTätortskod(SecondaryIndex<String, String, Ort> ortByTätortskod) {
    this.ortByTätortskod = ortByTätortskod;
  }

}
