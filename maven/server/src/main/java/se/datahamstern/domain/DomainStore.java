package se.datahamstern.domain;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import se.datahamstern.Datahamstern;
import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;
import se.datahamstern.io.FileUtils;
import se.datahamstern.sourced.SourcedValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
  private SecondaryIndex<Gatuadress.UniqueIndex, String, Gatuadress> gatuadressByUniqueIndex;

  private PrimaryIndex<String, Postnummer> postnummer;
  private SecondaryIndex<String, String, Postnummer> postnummerByPostnummer;
  private SecondaryIndex<String, String, Postnummer> postnummerByPostort;


  public void open() throws Exception {

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
    gatuadressByUniqueIndex = entityStore.getSecondaryIndex(gatuadresser, Gatuadress.UniqueIndex.class, "_index_unique");

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

  /**
   * Ensures that values for all secondary indices are set
   * and that instances are added to the correct primary index
   */
  private DomainObjectVisitor putVisitor = new DomainObjectVisitor() {
    @Override
    public void visit(Lan län) {

      if (län.getIdentity() != null) {
        Lan current = getLän().get(län.getIdentity());
        if (current.equals(län)) {
          return;
        }
      }

      assignIdentity(län);
      län.set_index_alfakod(län.getAlfakod().get());
      län.set_index_nummerkod(län.getNummerkod().get());
      getLän().put(län);
    }

    @Override
    public void visit(Kommun kommun) {
      assignIdentity(kommun);
      kommun.set_index_länIdentity(kommun.getLänIdentity().get());
      kommun.set_index_namn(kommun.getNamn().get());
      getKommuner().put(kommun);
    }

    @Override
    public void visit(Ort ort) {
      assignIdentity(ort);
      ort.set_index_kommunIdentity(ort.getKommunIdentity().get());
      getOrter().put(ort);
    }

    @Override
    public void visit(Organisation organisation) {
      assignIdentity(organisation);
      organisation.set_index_nummer(organisation.getNummer().get());
      organisation.set_index_länIdentity(organisation.getLänIdentity().get());
      getOrganisationer().put(organisation);
    }

    @Override
    public void visit(Arsredovisning årsredovisning) {
      assignIdentity(årsredovisning);
      årsredovisning.set_index_organisationIdentity(årsredovisning.getOrganisationIdentity().get());
      if (årsredovisning.getDatumFrom().get() != null) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(årsredovisning.getDatumFrom().get());
        årsredovisning.set_index_år(calendar.get(Calendar.YEAR));
      } else {
        årsredovisning.set_index_år(null);
      }
      getÅrsredovisningar().put(årsredovisning);

    }

    @Override
    public void visit(EkonomiskPlan ekonomiskPlan) {
      assignIdentity(ekonomiskPlan);
      ekonomiskPlan.set_index_organisationIdentity(ekonomiskPlan.getOrganisationIdentity().get());
      getEkonomiskaPlaner().put(ekonomiskPlan);

    }

    @Override
    public void visit(Stadgar stadgar) {
      assignIdentity(stadgar);
      stadgar.set_index_organisationIdentity(stadgar.getOrganisationIdentity().get());
      getStadgar().put(stadgar);
    }

    @Override
    public void visit(Dokument dokument) {
      assignIdentity(dokument);
      if (dokument.getDokumentversionerIdentity() != null && !dokument.getDokumentversionerIdentity().isEmpty()) {
        dokument.set_index_dokumentversionerIdentity(new ArrayList<String>(dokument.getDokumentversionerIdentity().size()));
        for (SourcedValue<String> dokumentVersionIdentity : dokument.getDokumentversionerIdentity()) {
          dokument.get_index_dokumentversionerIdentity().add(dokumentVersionIdentity.get());
        }
      }
      getDokument().put(dokument);

    }

    @Override
    public void visit(Dokumentversion dokumentversion) {
      assignIdentity(dokumentversion);
      getDokumentversioner().put(dokumentversion);
    }

    @Override
    public void visit(Gatuadress gatuaddress) {
      assignIdentity(gatuaddress);
      gatuaddress.set_index_postnummerIdentity(gatuaddress.getPostnummerIdentity().get());

      Gatuadress.UniqueIndex uniqueIndex = gatuaddress.get_index_unique();
      if (uniqueIndex == null) {
        gatuaddress.set_index_unique(uniqueIndex = new Gatuadress.UniqueIndex());
      }
      uniqueIndex.setGatunamn(gatuaddress.getGatunamn().get());
      uniqueIndex.setGatunummer(gatuaddress.getGatunummer().get());
      uniqueIndex.setPostnummerIdentity(gatuaddress.getPostnummerIdentity().get());

      getGatuadresser().put(gatuaddress);
    }

    @Override
    public void visit(Postnummer postnummer) {
      assignIdentity(postnummer);
      postnummer.set_index_postnummer(postnummer.getPostnummer().get());
      postnummer.set_index_postortIdentity(postnummer.getPostortIdentity().get());
      getPostnummer().put(postnummer);
    }

    @Override
    public void visit(Postort postort) {
      assignIdentity(postort);
      postort.set_index_namn(postort.getNamn().get());
      getPostorter().put(postort);
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

  public PrimaryIndex<String, Arsredovisning> getÅrsredovisningar() {
    return årsredovisningar;
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

  public SecondaryIndex<Gatuadress.UniqueIndex, String, Gatuadress> getGatuadressByUniqueIndex() {
    return gatuadressByUniqueIndex;
  }

  public void setGatuadressByUniqueIndex(SecondaryIndex<Gatuadress.UniqueIndex, String, Gatuadress> gatuadressByUniqueIndex) {
    this.gatuadressByUniqueIndex = gatuadressByUniqueIndex;
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
