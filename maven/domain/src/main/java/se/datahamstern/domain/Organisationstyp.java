package se.datahamstern.domain;

/**
 * @author kalle
 * @since 2012-04-18 11:01
 */
public enum Organisationstyp {

  // företagsformer

  AB("Aktiebolag"),
  BAB("Bankaktiebolag"),
  BF("Bostadsförening"),
  BFL("Utländsk banks filial"),
  BRF("Bostadsrättsförening"),
  E("Enskild näringsidkare / enskild firma"),
  EGTS("Europeiska grupperingar för territoriellt samarbete"),
  EK("Ekonomisk förening"),
  FAB("Försäkringsaktiebolag"),
  FL("Filial"),
  FOF("Försäkringsförening"),
  HB("Handelsbolag"),
  I("Ideell förening som bedriver näring"),
  KB("Kommanditbolag"),
  KHF("Kooperativ hyresrättsförening"),
  MB("Medlemsbank"),
  OFB("Ömsesidigt försäkringsbolag"),
  SB("Sparbank"),
  SCE("Europakooperativ"),
  SE("Europabolag"),
  SF("Sambruksförening"),
  S("Stiftelse som bedriver näring"),
  TSF("Trossamfund"),

  // övriga organisationer med organisationsnummer

  M("Statlig myndighet");


  private String beskrivning;

  private Organisationstyp(String beskrivning) {
    this.beskrivning = beskrivning;
  }

  public String getBeskrivning() {
    return beskrivning;
  }

}
