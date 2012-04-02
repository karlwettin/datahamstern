package se.datahamstern.domain.wikipedia;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainEntityObjectVisitor;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-02 03:22
 */
@Entity(version = 1)
public class Lan extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;


  @Override
  public void accept(DomainEntityObjectVisitor visitor) {
    visitor.visit(this);
  }

  /** for example "Hallands län" */
  private SourcedValue<String> namn =  new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String _index_namn;


  /** for example "N" as in "Hallands län" */
  private SourcedValue<String> alfakod=  new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String _index_alfakod;

  /** for example "13" as in "Hallands län"  */
  private SourcedValue<String> nummerkod=  new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String _index_nummerkod;

  /** uuid, local bdb identity. */
  @PrimaryKey
  private String identity;

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Lan lan = (Lan) o;

    if (alfakod != null ? !alfakod.equals(lan.alfakod) : lan.alfakod != null) return false;
    if (identity != null ? !identity.equals(lan.identity) : lan.identity != null) return false;
    if (namn != null ? !namn.equals(lan.namn) : lan.namn != null) return false;
    if (nummerkod != null ? !nummerkod.equals(lan.nummerkod) : lan.nummerkod != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (namn != null ? namn.hashCode() : 0);
    result = 31 * result + (alfakod != null ? alfakod.hashCode() : 0);
    result = 31 * result + (nummerkod != null ? nummerkod.hashCode() : 0);
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
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

  public SourcedValue<String> getNamn() {
    return namn;
  }

  public void setNamn(SourcedValue<String> namn) {
    this.namn = namn;
  }

  public String get_index_alfakod() {
    return _index_alfakod;
  }

  public void set_index_alfakod(String _index_alfakod) {
    this._index_alfakod = _index_alfakod;
  }

  public SourcedValue<String> getAlfakod() {
    return alfakod;
  }

  public void setAlfakod(SourcedValue<String> alfakod) {
    this.alfakod = alfakod;
  }

  public String get_index_nummerkod() {
    return _index_nummerkod;
  }

  public void set_index_nummerkod(String _index_nummerkod) {
    this._index_nummerkod = _index_nummerkod;
  }

  public SourcedValue<String> getNummerkod() {
    return nummerkod;
  }

  public void setNummerkod(SourcedValue<String> nummerkod) {
    this.nummerkod = nummerkod;
  }

  public String get_index_namn() {
    return _index_namn;
  }

  public void set_index_namn(String _index_namn) {
    this._index_namn = _index_namn;
  }
}
