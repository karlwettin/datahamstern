package se.datahamstern.external.naringslivsregistret;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-02-29 01:20
 */
public class NaringslivsregistretResult implements Serializable {

  private static final long serialVersionUID = 1l;


  /**
   * 2 siffor
   *
   * http://sv.wikipedia.org/wiki/Organisationsnummer
   * 12-siffriga organisationsnummer/personnummer:
   * för juridisk person: 16 + företagets tilldelade organisationsnummer (10 siffror),
   * för fysisk person: sekelsiffror 18, 19 el. 20 + personnummer (10 siffror).
   */
  private String nummerprefix;

  /**
   * 10 siffror
   *
   * http://sv.wikipedia.org/wiki/Organisationsnummer
   * Den första siffran i organisationsnumret anger vilken grupp av företagsformer organisationen tillhör, nämligen:
   * 2 – Stat, landsting, kommuner, församlingar
   * 5 – Aktiebolag
   * 6 – Enkelt bolag
   * 7 – Ekonomiska föreningar
   * 8 – Ideella föreningar och stiftelser
   * 9 – Handelsbolag, kommanditbolag och enkla bolag
   */
  private String nummer;

  /**
   * 3 siffror?
   * samma person med flera enkilda firmor har suffix.
   */
  private String nummersuffix;
  private String namn;
  private String typ;
  private String länsnummer;
  private String status;

  public void setNummerprefix(String nummerprefix) {
    this.nummerprefix = nummerprefix;
  }

  public void setNummer(String nummer) {
    this.nummer = nummer;
  }

  public void setNummersuffix(String nummersuffix) {
    this.nummersuffix = nummersuffix;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }

  public void setTyp(String typ) {
    this.typ = typ;
  }

  public void setLänsnummer(String länsnummer) {
    this.länsnummer = länsnummer;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNummerprefix() {
    return nummerprefix;
  }

  public String getNummer() {
    return nummer;
  }

  public String getNummersuffix() {
    return nummersuffix;
  }

  public String getNamn() {
    return namn;
  }

  public String getTyp() {
    return typ;
  }

  public String getLänsnummer() {
    return länsnummer;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "NaringslivsregistretResult{" +
        "nummerprefix='" + nummerprefix + '\'' +
        ", nummer='" + nummer + '\'' +
        ", nummersuffix='" + nummersuffix + '\'' +
        ", namn='" + namn + '\'' +
        ", typ='" + typ + '\'' +
        ", länsnummer='" + länsnummer + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}
