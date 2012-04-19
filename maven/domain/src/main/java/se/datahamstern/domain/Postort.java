package se.datahamstern.domain;

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
 * @since 2012-04-01 03:52
 */
@Entity(version = 1)
public class Postort extends AbstractSourced implements DomainEntityObject, Serializable {

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

  @Override
  public String toString() {
    return "Postort{" +
        "namn=" + namn +
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

    Postort postort = (Postort) o;

    if (identity != null ? !identity.equals(postort.identity) : postort.identity != null) return false;
    if (namn != null ? !namn.equals(postort.namn) : postort.namn != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (namn != null ? namn.hashCode() : 0);
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

  public String get_index_namn() {
    return _index_namn;
  }

  public void set_index_namn(String _index_namn) {
    this._index_namn = _index_namn;
  }

  public SourcedValue<String> getNamn() {
    return namn;
  }

  public void setNamn(SourcedValue<String> namn) {
    this.namn = namn;
  }
}
