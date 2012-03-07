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
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private SourcedValue<String> namn = new SourcedValue<String>();

  private SourcedValue<String> nummerkod = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_nummerkod;

  private SourcedValue<String> länIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Lan.class)
  private String _index_länIdentity;

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

  @Override
  public String getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(String identity) {
    this.identity = identity;
  }
}
