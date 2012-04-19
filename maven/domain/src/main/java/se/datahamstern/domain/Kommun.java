package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-03 22:53
 */
@Entity(version =1)
public class Kommun extends AbstractSourced implements DomainEntityObject, Serializable {

    private static final long serialVersionUID = 1l;

  @Override
  public void accept(DomainEntityObjectVisitor visitor) throws Exception {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private SourcedValue<String> namn = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String _index_namn;

  private SourcedValue<String> nummerkod = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_nummerkod;

  private SourcedValue<String> länIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Lan.class)
  private String _index_länIdentity;

  @Override
  public String toString() {
    return "Kommun{" +
        "namn=" + namn +
        ", nummerkod=" + nummerkod +
        ", länIdentity=" + länIdentity +
        ", identity='" + identity + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Kommun kommun = (Kommun) o;

    if (identity != null ? !identity.equals(kommun.identity) : kommun.identity != null) return false;
    if (länIdentity != null ? !länIdentity.equals(kommun.länIdentity) : kommun.länIdentity != null) return false;
    if (namn != null ? !namn.equals(kommun.namn) : kommun.namn != null) return false;
    if (nummerkod != null ? !nummerkod.equals(kommun.nummerkod) : kommun.nummerkod != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (namn != null ? namn.hashCode() : 0);
    result = 31 * result + (nummerkod != null ? nummerkod.hashCode() : 0);
    result = 31 * result + (länIdentity != null ? länIdentity.hashCode() : 0);
    return result;
  }

  @Override
  public String getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public SourcedValue<String> getNummerkod() {
    return nummerkod;
  }

  public void setNummerkod(SourcedValue<String> nummerkod) {
    this.nummerkod = nummerkod;
  }

  public String get_index_nummerkod() {
    return _index_nummerkod;
  }

  public void set_index_nummerkod(String _index_nummerkod) {
    this._index_nummerkod = _index_nummerkod;
  }

  public SourcedValue<String> getNamn() {
    return namn;
  }

  public void setNamn(SourcedValue<String> namn) {
    this.namn = namn;
  }

  public SourcedValue<String> getLänIdentity() {
    return länIdentity;
  }

  public void setLänIdentity(SourcedValue<String> länIdentity) {
    this.länIdentity = länIdentity;
  }

  public String get_index_länIdentity() {
    return _index_länIdentity;
  }

  public void set_index_länIdentity(String _index_länIdentity) {
    this._index_länIdentity = _index_länIdentity;
  }

  public String get_index_namn() {
    return _index_namn;
  }

  public void set_index_namn(String _index_namn) {
    this._index_namn = _index_namn;
  }
}
