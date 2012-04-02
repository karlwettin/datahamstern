package se.datahamstern.domain.postnummer;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainEntityObjectVisitor;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

  private List<SourcedValue<String>> stereotyper = new ArrayList<SourcedValue<String>>();

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Postnummer that = (Postnummer) o;

    if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
    if (postnummer != null ? !postnummer.equals(that.postnummer) : that.postnummer != null) return false;
    if (postortIdentity != null ? !postortIdentity.equals(that.postortIdentity) : that.postortIdentity != null) return false;
    if (stereotyper != null ? !stereotyper.equals(that.stereotyper) : that.stereotyper != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (postnummer != null ? postnummer.hashCode() : 0);
    result = 31 * result + (postortIdentity != null ? postortIdentity.hashCode() : 0);
    result = 31 * result + (stereotyper != null ? stereotyper.hashCode() : 0);
    return result;
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

  public List<SourcedValue<String>> getStereotyper() {
    return stereotyper;
  }

  public void setStereotyper(List<SourcedValue<String>> stereotyper) {
    this.stereotyper = stereotyper;
  }

}
