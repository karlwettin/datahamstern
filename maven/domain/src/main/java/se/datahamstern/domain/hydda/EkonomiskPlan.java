package se.datahamstern.domain.hydda;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainObjectVisitor;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-05 04:47
 */
@Entity(version = 1)
public class EkonomiskPlan extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;


  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Organisation.class)
  private String _index_organisationIdentity;

  private SourcedValue<String> organisationIdentity;


  /** när den ekonomiska planen registrerades hos bolagsverket */
  private SourcedValue<Date> registrerinsdatum = new SourcedValue<Date>();

  /** när styrelsen skrev under den ekonomiska planen */
  private SourcedValue<Date> signaturdatum = new SourcedValue<Date>();

  //private Document dokument;

  @PrimaryKey
  private String identity;

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EkonomiskPlan that = (EkonomiskPlan) o;

    if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
    if (organisationIdentity != null ? !organisationIdentity.equals(that.organisationIdentity) : that.organisationIdentity != null) return false;
    if (registrerinsdatum != null ? !registrerinsdatum.equals(that.registrerinsdatum) : that.registrerinsdatum != null) return false;
    if (signaturdatum != null ? !signaturdatum.equals(that.signaturdatum) : that.signaturdatum != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (organisationIdentity != null ? organisationIdentity.hashCode() : 0);
    result = 31 * result + (registrerinsdatum != null ? registrerinsdatum.hashCode() : 0);
    result = 31 * result + (signaturdatum != null ? signaturdatum.hashCode() : 0);
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "EkonomiskPlan{" +
        "organisationIdentity=" + organisationIdentity +
        ", registrerinsdatum=" + registrerinsdatum +
        ", signaturdatum=" + signaturdatum +
        ", identity='" + identity + '\'' +
        ", _index_organisationIdentity='" + _index_organisationIdentity + '\'' +
        '}';
  }

  public SourcedValue<Date> getRegistrerinsdatum() {
    return registrerinsdatum;
  }

  public void setRegistrerinsdatum(SourcedValue<Date> registrerinsdatum) {
    this.registrerinsdatum = registrerinsdatum;
  }

  public SourcedValue<Date> getSignaturdatum() {
    return signaturdatum;
  }

  public void setSignaturdatum(SourcedValue<Date> signaturdatum) {
    this.signaturdatum = signaturdatum;
  }

  @Override
  public String getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(String identity) {
    this.identity = identity;
  }




  public String get_index_organisationIdentity() {
    return _index_organisationIdentity;
  }

  public void set_index_organisationIdentity(String _index_organisationIdentity) {
    this._index_organisationIdentity = _index_organisationIdentity;
  }

  public SourcedValue<String> getOrganisationIdentity() {
    return organisationIdentity;
  }

  public void setOrganisationIdentity(SourcedValue<String> organisationIdentity) {
    this.organisationIdentity = organisationIdentity;
  }


}
