package se.datahamstern.domain;

import com.sleepycat.persist.model.*;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainEntityObjectVisitor;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * todo se till att alla gatunamn verkligen är unika per postort
 * todo och bygg eventlog med undantagsfallen
 *
 * @author kalle
 * @since 2012-04-02 17:59
 */
@Entity(version = 1)
public class Gata extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @Override
  public void accept(DomainEntityObjectVisitor visitor)  throws Exception{
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private SourcedValue<String> namn = new SourcedValue<String>();

  private List<SourcedValue<String>> postnummerIdentities = new ArrayList<SourcedValue<String>>();

  @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity = Postnummer.class)
  private List<String> _index_postnummerIdentities = new ArrayList<String>();

  private SourcedValue<String> postortIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Postort.class)
  private String _index_postortIdentity;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private NamnAndPostort _index_namnAndPostort;

  @Persistent(version = 1)
  public static class NamnAndPostort {
    @KeyField(1)
    private String gatunamn;
    @KeyField(2)
    private String postortIdentity;

    public NamnAndPostort() {
    }

    public NamnAndPostort(String gatunamn, String postortIdentity) {
      this.gatunamn = gatunamn;
      this.postortIdentity = postortIdentity;
    }

    public String getGatunamn() {
      return gatunamn;
    }

    public void setGatunamn(String gatunamn) {
      this.gatunamn = gatunamn;
    }

    public String getPostortIdentity() {
      return postortIdentity;
    }

    public void setPostortIdentity(String postortIdentity) {
      this.postortIdentity = postortIdentity;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NamnAndPostort that = (NamnAndPostort) o;

      if (gatunamn != null ? !gatunamn.equals(that.gatunamn) : that.gatunamn != null) return false;
      if (postortIdentity != null ? !postortIdentity.equals(that.postortIdentity) : that.postortIdentity != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = gatunamn != null ? gatunamn.hashCode() : 0;
      result = 31 * result + (postortIdentity != null ? postortIdentity.hashCode() : 0);
      return result;
    }
  }

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Gata gata = (Gata) o;

    if (identity != null ? !identity.equals(gata.identity) : gata.identity != null) return false;
    if (namn != null ? !namn.equals(gata.namn) : gata.namn != null) return false;
    if (postnummerIdentities != null ? !postnummerIdentities.equals(gata.postnummerIdentities) : gata.postnummerIdentities != null) return false;
    if (postortIdentity != null ? !postortIdentity.equals(gata.postortIdentity) : gata.postortIdentity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (namn != null ? namn.hashCode() : 0);
    result = 31 * result + (postnummerIdentities != null ? postnummerIdentities.hashCode() : 0);
    result = 31 * result + (postortIdentity != null ? postortIdentity.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Gata{" +
        "namn=" + namn +
        ", identity='" + identity + '\'' +
        ", postortIdentity=" + postortIdentity +
        ", postnummerIdentities=" + postnummerIdentities +
        '}';
  }

  @Override
  public String getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public SourcedValue<String> getNamn() {
    return namn;
  }

  public void setNamn(SourcedValue<String> namn) {
    this.namn = namn;
  }

  public List<SourcedValue<String>> getPostnummerIdentities() {
    return postnummerIdentities;
  }

  public void setPostnummerIdentities(List<SourcedValue<String>> postnummerIdentities) {
    this.postnummerIdentities = postnummerIdentities;
  }

  public List<String> get_index_postnummerIdentities() {
    return _index_postnummerIdentities;
  }

  public void set_index_postnummerIdentities(List<String> _index_postnummerIdentities) {
    this._index_postnummerIdentities = _index_postnummerIdentities;
  }

  public SourcedValue<String> getPostortIdentity() {
    return postortIdentity;
  }

  public void setPostortIdentity(SourcedValue<String> postortIdentity) {
    this.postortIdentity = postortIdentity;
  }

  public String get_index_postortIdentity() {
    return _index_postortIdentity;
  }

  public void set_index_postortIdentity(String _index_postortIdentity) {
    this._index_postortIdentity = _index_postortIdentity;
  }

  public NamnAndPostort get_index_namnAndPostort() {
    return _index_namnAndPostort;
  }

  public void set_index_namnAndPostort(NamnAndPostort _index_namnAndPostort) {
    this._index_namnAndPostort = _index_namnAndPostort;
  }
}
