package se.datahamstern.domain.postnummer;

import com.sleepycat.persist.model.*;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainObjectVisitor;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-26 02:19
 */
@Entity(version = 1)
public class Gatuadress extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @PrimaryKey
  private String identity;

  private SourcedValue<String> gatunamn = new SourcedValue<String>();
  private SourcedValue<Integer> gatunummer = new SourcedValue<Integer>();

  // todo also contains postort so we can find it in case switched postnummer?
  // todo but first find out if there can be more than one unique gatunamn per postort.
  // todo for instance i think there are two drottningatan in helsingborg?

  private SourcedValue<String> postnummerIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Postnummer.class)
  private String _index_postnummerIdentity;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private UniqueIndex _index_unique;

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Gatuadress that = (Gatuadress) o;

    if (gatunamn != null ? !gatunamn.equals(that.gatunamn) : that.gatunamn != null) return false;
    if (gatunummer != null ? !gatunummer.equals(that.gatunummer) : that.gatunummer != null) return false;
    if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
    if (postnummerIdentity != null ? !postnummerIdentity.equals(that.postnummerIdentity) : that.postnummerIdentity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (gatunamn != null ? gatunamn.hashCode() : 0);
    result = 31 * result + (gatunummer != null ? gatunummer.hashCode() : 0);
    result = 31 * result + (postnummerIdentity != null ? postnummerIdentity.hashCode() : 0);
    return result;
  }

  @Persistent(version = 1)
  public static class UniqueIndex {
    @KeyField(1)
    private String gatunamn;
    @KeyField(2)
    private int gatunummer;
    @KeyField(3)
    private String postnummerIdentity;

    public String getGatunamn() {
      return gatunamn;
    }

    public void setGatunamn(String gatunamn) {
      this.gatunamn = gatunamn;
    }

    public int getGatunummer() {
      return gatunummer;
    }

    public void setGatunummer(int gatunummer) {
      this.gatunummer = gatunummer;
    }

    public String getPostnummerIdentity() {
      return postnummerIdentity;
    }

    public void setPostnummerIdentity(String postnummerIdentity) {
      this.postnummerIdentity = postnummerIdentity;
    }
  }


  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public UniqueIndex get_index_unique() {
    return _index_unique;
  }

  public void set_index_unique(UniqueIndex _index_unique) {
    this._index_unique = _index_unique;
  }

  public String get_index_postnummerIdentity() {
    return _index_postnummerIdentity;
  }

  public void set_index_postnummerIdentity(String _index_postnummerIdentity) {
    this._index_postnummerIdentity = _index_postnummerIdentity;
  }

  public SourcedValue<String> getGatunamn() {
    return gatunamn;
  }

  public void setGatunamn(SourcedValue<String> gatunamn) {
    this.gatunamn = gatunamn;
  }

  public SourcedValue<String> getPostnummerIdentity() {
    return postnummerIdentity;
  }

  public void setPostnummerIdentity(SourcedValue<String> postnummerIdentity) {
    this.postnummerIdentity = postnummerIdentity;
  }

  public SourcedValue<Integer> getGatunummer() {
    return gatunummer;
  }

  public void setGatunummer(SourcedValue<Integer> gatunummer) {
    this.gatunummer = gatunummer;
  }

}
