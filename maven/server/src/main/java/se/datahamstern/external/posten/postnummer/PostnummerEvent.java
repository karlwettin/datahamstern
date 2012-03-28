package se.datahamstern.external.posten.postnummer;

/**
 * @author kalle
 * @since 2012-03-26 04:17
 */
public class PostnummerEvent {

  private String gatunamn;
  private int gatunummerFrom;
  private int gatunummerTo;
  private String postnummer;
  private String postort;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PostnummerEvent that = (PostnummerEvent) o;

    if (gatunummerFrom != that.gatunummerFrom) return false;
    if (gatunummerTo != that.gatunummerTo) return false;
    if (gatunamn != null ? !gatunamn.equals(that.gatunamn) : that.gatunamn != null) return false;
    if (postnummer != null ? !postnummer.equals(that.postnummer) : that.postnummer != null) return false;
    if (postort != null ? !postort.equals(that.postort) : that.postort != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = gatunamn != null ? gatunamn.hashCode() : 0;
    result = 31 * result + gatunummerFrom;
    result = 31 * result + gatunummerTo;
    result = 31 * result + (postnummer != null ? postnummer.hashCode() : 0);
    result = 31 * result + (postort != null ? postort.hashCode() : 0);
    return result;
  }

  public String getGatunamn() {
    return gatunamn;
  }

  public void setGatunamn(String gatunamn) {
    this.gatunamn = gatunamn;
  }

  public int getGatunummerFrom() {
    return gatunummerFrom;
  }

  public void setGatunummerFrom(int gatunummerFrom) {
    this.gatunummerFrom = gatunummerFrom;
  }

  public int getGatunummerTo() {
    return gatunummerTo;
  }

  public void setGatunummerTo(int gatunummerTo) {
    this.gatunummerTo = gatunummerTo;
  }

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
}
