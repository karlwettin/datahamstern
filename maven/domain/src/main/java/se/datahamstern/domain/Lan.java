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
 * @since 2012-03-02 03:22
 */
@Entity(version = 1)
public class Lan extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;


  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  /** for example "Hallands län" */
  private SourcedValue<String> namn =  new SourcedValue<String>();


  /** for example "N" as in "Hallands län" */
  private SourcedValue<String> alfakod=  new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_alfakod;

  /** for example "13" as in "Hallands län"  */
  private SourcedValue<String> nummerkod=  new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_nummerkod;

  /** uuid, local bdb identity. */
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

  @Override
  public String toString() {
    return "Lan{" +
        "identity='" + identity + '\'' +
        ", alfakod=" + alfakod +
        ", nummerkod=" + nummerkod +
        ", namn=" + namn +
        ", _index_alfakod='" + _index_alfakod + '\'' +
        ", _index_nummerkod='" + _index_nummerkod + '\'' +
        '}';
  }
}
