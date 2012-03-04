package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2012-03-02 03:04
 */
@Entity(version = 1)
public class Organisation extends AbstractSourced implements DomainEntityObject {

  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * EK, BF, BRF, HB, KB, AB, etc
   * todo enumeration?
   */
  private SourcedValue<String> typ = new SourcedValue<String>();

  private SourcedValue<String> namn = new SourcedValue<String>();

  private SourcedValue<String> nummerPrefix = new SourcedValue<String>();

  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_nummer;

  private SourcedValue<String> nummer = new SourcedValue<String>();
  private SourcedValue<String> nummerSuffix = new SourcedValue<String>();

  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Lan.class)
  private String _index_länIdentity;

  private SourcedValue<String> länIdentity = new SourcedValue<String>();


  /**
   * statusinformation från näringslivsregistret.
   * försatt i konkurs, upplöst, fusion, etc.
   * // todo tolka informationen och lägg till den strukturerat i datamodellen!
   */
  private List<SourcedValue<String>> status = new ArrayList<SourcedValue<String>>();

  /** uuid, local database identity */
  @PrimaryKey
  private String identity;


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

  public SourcedValue<String> getNummerPrefix() {
    return nummerPrefix;
  }

  public void setNummerPrefix(SourcedValue<String> nummerPrefix) {
    this.nummerPrefix = nummerPrefix;
  }

  public SourcedValue<String> getNummer() {
    return nummer;
  }

  public void setNummer(SourcedValue<String> nummer) {
    this.nummer = nummer;
  }

  public SourcedValue<String> getNummerSuffix() {
    return nummerSuffix;
  }

  public void setNummerSuffix(SourcedValue<String> nummerSuffix) {
    this.nummerSuffix = nummerSuffix;
  }

  public SourcedValue<String> getTyp() {
    return typ;
  }

  public void setTyp(SourcedValue<String> typ) {
    this.typ = typ;
  }

  public List<SourcedValue<String>> getStatus() {
    return status;
  }

  public void setStatus(List<SourcedValue<String>> status) {
    this.status = status;
  }

  public String get_index_nummer() {
    return _index_nummer;
  }

  public void set_index_nummer(String _index_nummer) {
    this._index_nummer = _index_nummer;
  }

  public String get_index_länIdentity() {
    return _index_länIdentity;
  }

  public void set_index_länIdentity(String _index_länIdentity) {
    this._index_länIdentity = _index_länIdentity;
  }

  public SourcedValue<String> getLänIdentity() {
    return länIdentity;
  }

  public void setLänIdentity(SourcedValue<String> länIdentity) {
    this.länIdentity = länIdentity;
  }
}
