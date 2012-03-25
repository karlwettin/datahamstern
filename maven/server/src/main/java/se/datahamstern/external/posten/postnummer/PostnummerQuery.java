package se.datahamstern.external.posten.postnummer;

/**
 * @author kalle
 * @since 2012-03-25 19:09
 */
public class PostnummerQuery {

  private String postnummer;
  private String postort;
  private String gatunamn;

  public String getPostnummer() {
    return postnummer;
  }

  public void setPostnummer(String postnummer) {
    this.postnummer = postnummer;
  }

  public String getPostort() {
    return postort;
  }

  public void setPostort(String postort) {
    this.postort = postort;
  }

  public String getGatunamn() {
    return gatunamn;
  }

  public void setGatunamn(String gatunamn) {
    this.gatunamn = gatunamn;
  }
}
