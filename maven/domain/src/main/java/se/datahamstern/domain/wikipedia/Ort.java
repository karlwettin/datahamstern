package se.datahamstern.domain.wikipedia;

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

  /** not unique! 12xLund? 23xBerg? */
  private SourcedValue<String> namn = new SourcedValue<String>();

  private SourcedValue<Integer> befolkningsmängd = new SourcedValue<Integer>();

  private SourcedValue<Long> kvadratmeterLandareal = new SourcedValue<Long>();

  private SourcedValue<String> tätortskod = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String _index_tätortskod;

  private SourcedValue<String> kommunIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Kommun.class)
  private String _index_kommunIdentity;


  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Ort ort = (Ort) o;

    if (befolkningsmängd != null ? !befolkningsmängd.equals(ort.befolkningsmängd) : ort.befolkningsmängd != null) return false;
    if (identity != null ? !identity.equals(ort.identity) : ort.identity != null) return false;
    if (kommunIdentity != null ? !kommunIdentity.equals(ort.kommunIdentity) : ort.kommunIdentity != null) return false;
    if (kvadratmeterLandareal != null ? !kvadratmeterLandareal.equals(ort.kvadratmeterLandareal) : ort.kvadratmeterLandareal != null) return false;
    if (namn != null ? !namn.equals(ort.namn) : ort.namn != null) return false;
    if (tätortskod != null ? !tätortskod.equals(ort.tätortskod) : ort.tätortskod != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (namn != null ? namn.hashCode() : 0);
    result = 31 * result + (befolkningsmängd != null ? befolkningsmängd.hashCode() : 0);
    result = 31 * result + (kvadratmeterLandareal != null ? kvadratmeterLandareal.hashCode() : 0);
    result = 31 * result + (tätortskod != null ? tätortskod.hashCode() : 0);
    result = 31 * result + (kommunIdentity != null ? kommunIdentity.hashCode() : 0);
    return result;
  }

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
