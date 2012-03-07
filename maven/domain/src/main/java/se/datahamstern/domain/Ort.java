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
 * @since 2012-03-07 03:20
 */
@Entity(version = 1)
public class Ort extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private SourcedValue<String> namn = new SourcedValue<String>();

  private SourcedValue<Integer> befolkningsmängd = new SourcedValue<Integer>();

  private SourcedValue<Long> kvadratmeterLandareal = new SourcedValue<Long>();

  private SourcedValue<String> tätortskod = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_tätortskod;

  private SourcedValue<String> kommunIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Kommun.class)
  private String _index_kommunIdentity;


  public SourcedValue<Integer> getBefolkningsmängd() {
    return befolkningsmängd;
  }

  public void setBefolkningsmängd(SourcedValue<Integer> befolkningsmängd) {
    this.befolkningsmängd = befolkningsmängd;
  }

  public SourcedValue<Long> getKvadratmeterLandareal() {
    return kvadratmeterLandareal;
  }

  public void setKvadratmeterLandareal(SourcedValue<Long> kvadratmeterLandareal) {
    this.kvadratmeterLandareal = kvadratmeterLandareal;
  }

  public SourcedValue<String> getNamn() {
    return namn;
  }

  public void setNamn(SourcedValue<String> namn) {
    this.namn = namn;
  }

  public SourcedValue<String> getTätortskod() {
    return tätortskod;
  }

  public void setTätortskod(SourcedValue<String> tätortskod) {
    this.tätortskod = tätortskod;
  }

  public String get_index_tätortskod() {
    return _index_tätortskod;
  }

  public void set_index_tätortskod(String _index_tätortskod) {
    this._index_tätortskod = _index_tätortskod;
  }

  public SourcedValue<String> getKommunIdentity() {
    return kommunIdentity;
  }

  public void setKommunIdentity(SourcedValue<String> kommunIdentity) {
    this.kommunIdentity = kommunIdentity;
  }

  public String get_index_kommunIdentity() {
    return _index_kommunIdentity;
  }

  public void set_index_kommunIdentity(String _index_kommunIdentity) {
    this._index_kommunIdentity = _index_kommunIdentity;
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
