package se.datahamstern.domain.postnummer;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainObjectVisitor;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-26 01:57
 */
@Entity(version = 1)
public class Postnummer extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @PrimaryKey
  private String identity;

  private SourcedValue<String> postnummer = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_postnummer;

  private SourcedValue<String> postortIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Postort.class)
  private String _index_postortIdentity;

  private SourcedValue<Boolean> gatunummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> boxnummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> svarspostsnummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> tävlingspostsnummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> storkundsnummer = new SourcedValue<Boolean>(false);

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Postnummer that = (Postnummer) o;

    if (boxnummer != null ? !boxnummer.equals(that.boxnummer) : that.boxnummer != null) return false;
    if (gatunummer != null ? !gatunummer.equals(that.gatunummer) : that.gatunummer != null) return false;
    if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
    if (postnummer != null ? !postnummer.equals(that.postnummer) : that.postnummer != null) return false;
    if (postortIdentity != null ? !postortIdentity.equals(that.postortIdentity) : that.postortIdentity != null) return false;
    if (storkundsnummer != null ? !storkundsnummer.equals(that.storkundsnummer) : that.storkundsnummer != null) return false;
    if (svarspostsnummer != null ? !svarspostsnummer.equals(that.svarspostsnummer) : that.svarspostsnummer != null) return false;
    if (tävlingspostsnummer != null ? !tävlingspostsnummer.equals(that.tävlingspostsnummer) : that.tävlingspostsnummer != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (postnummer != null ? postnummer.hashCode() : 0);
    result = 31 * result + (postortIdentity != null ? postortIdentity.hashCode() : 0);
    result = 31 * result + (gatunummer != null ? gatunummer.hashCode() : 0);
    result = 31 * result + (boxnummer != null ? boxnummer.hashCode() : 0);
    result = 31 * result + (svarspostsnummer != null ? svarspostsnummer.hashCode() : 0);
    result = 31 * result + (tävlingspostsnummer != null ? tävlingspostsnummer.hashCode() : 0);
    result = 31 * result + (storkundsnummer != null ? storkundsnummer.hashCode() : 0);
    return result;
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

  public String get_index_postnummer() {
    return _index_postnummer;
  }

  public void set_index_postnummer(String _index_postnummer) {
    this._index_postnummer = _index_postnummer;
  }

  public SourcedValue<String> getPostnummer() {
    return postnummer;
  }

  public void setPostnummer(SourcedValue<String> postnummer) {
    this.postnummer = postnummer;
  }

  public SourcedValue<Boolean> getGatunummer() {
    return gatunummer;
  }

  public void setGatunummer(SourcedValue<Boolean> gatunummer) {
    this.gatunummer = gatunummer;
  }

  public SourcedValue<Boolean> getBoxnummer() {
    return boxnummer;
  }

  public void setBoxnummer(SourcedValue<Boolean> boxnummer) {
    this.boxnummer = boxnummer;
  }

  public SourcedValue<Boolean> getSvarspostsnummer() {
    return svarspostsnummer;
  }

  public void setSvarspostsnummer(SourcedValue<Boolean> svarspostsnummer) {
    this.svarspostsnummer = svarspostsnummer;
  }

  public SourcedValue<Boolean> getTävlingspostsnummer() {
    return tävlingspostsnummer;
  }

  public void setTävlingspostsnummer(SourcedValue<Boolean> tävlingspostsnummer) {
    this.tävlingspostsnummer = tävlingspostsnummer;
  }

  public SourcedValue<Boolean> getStorkundsnummer() {
    return storkundsnummer;
  }

  public void setStorkundsnummer(SourcedValue<Boolean> storkundsnummer) {
    this.storkundsnummer = storkundsnummer;
  }
}
