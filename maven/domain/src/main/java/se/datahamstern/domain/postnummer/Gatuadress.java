package se.datahamstern.domain.postnummer;

import com.sleepycat.persist.model.*;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainEntityObjectVisitor;
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

  private SourcedValue<String> gataIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Gata.class)
  private String _index_gataIdentity;

  private SourcedValue<Integer> gatunummer = new SourcedValue<Integer>();

  private SourcedValue<String> postnummerIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Postnummer.class)
  private String _index_postnummerIdentity;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private GataAndGatunummer _index_gataAndGatunummer;

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Gatuadress that = (Gatuadress) o;

    if (gataIdentity != null ? !gataIdentity.equals(that.gataIdentity) : that.gataIdentity != null) return false;
    if (gatunummer != null ? !gatunummer.equals(that.gatunummer) : that.gatunummer != null) return false;
    if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
    if (postnummerIdentity != null ? !postnummerIdentity.equals(that.postnummerIdentity) : that.postnummerIdentity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (gataIdentity != null ? gataIdentity.hashCode() : 0);
    result = 31 * result + (gatunummer != null ? gatunummer.hashCode() : 0);
    result = 31 * result + (postnummerIdentity != null ? postnummerIdentity.hashCode() : 0);
    return result;
  }

  @Persistent(version = 1)
  public static class GataAndGatunummer {
    @KeyField(1)
    private String gataIdentity;
    @KeyField(2)
    private int gatunummer;

    public String getGataIdentity() {
      return gataIdentity;
    }

    public void setGataIdentity(String gataIdentity) {
      this.gataIdentity = gataIdentity;
    }

    public int getGatunummer() {
      return gatunummer;
    }

    public void setGatunummer(int gatunummer) {
      this.gatunummer = gatunummer;
    }

  }


  @Override
  public void accept(DomainEntityObjectVisitor visitor) {
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

  public GataAndGatunummer get_index_gataAndGatunummer() {
    return _index_gataAndGatunummer;
  }

  public void set_index_gataAndGatunummer(GataAndGatunummer _index_gataAndGatunummer) {
    this._index_gataAndGatunummer = _index_gataAndGatunummer;
  }

  public String get_index_postnummerIdentity() {
    return _index_postnummerIdentity;
  }

  public void set_index_postnummerIdentity(String _index_postnummerIdentity) {
    this._index_postnummerIdentity = _index_postnummerIdentity;
  }

  public String get_index_gataIdentity() {
    return _index_gataIdentity;
  }

  public void set_index_gataIdentity(String _index_gataIdentity) {
    this._index_gataIdentity = _index_gataIdentity;
  }

  public SourcedValue<String> getGataIdentity() {
    return gataIdentity;
  }

  public void setGataIdentity(SourcedValue<String> gataIdentity) {
    this.gataIdentity = gataIdentity;
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
