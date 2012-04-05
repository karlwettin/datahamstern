package se.datahamstern.domain.hydda;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainEntityObjectVisitor;
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
public class Stadgar extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;


  @Override
  public void accept(DomainEntityObjectVisitor visitor)  throws Exception{
    visitor.visit(this);
  }

  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Organisation.class)
  private String _index_organisationIdentity;

  private SourcedValue<String> organisationIdentity;


  /** när stadgarna registrerades hos bolagsverket */
  private SourcedValue<Date> registrerinsdatum = new SourcedValue<Date>();

  /**
   * när stadgarna antogs. det senare datumet om känt.
   * todo håll koll på datumet från både första och andra stämman då stadgarna antogs.
   * */
  private SourcedValue<Date> antagningsdatum = new SourcedValue<Date>();

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

    Stadgar stadgar = (Stadgar) o;

    if (antagningsdatum != null ? !antagningsdatum.equals(stadgar.antagningsdatum) : stadgar.antagningsdatum != null) return false;
    if (identity != null ? !identity.equals(stadgar.identity) : stadgar.identity != null) return false;
    if (organisationIdentity != null ? !organisationIdentity.equals(stadgar.organisationIdentity) : stadgar.organisationIdentity != null) return false;
    if (registrerinsdatum != null ? !registrerinsdatum.equals(stadgar.registrerinsdatum) : stadgar.registrerinsdatum != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (organisationIdentity != null ? organisationIdentity.hashCode() : 0);
    result = 31 * result + (registrerinsdatum != null ? registrerinsdatum.hashCode() : 0);
    result = 31 * result + (antagningsdatum != null ? antagningsdatum.hashCode() : 0);
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Stadgar{" +
        "organisationIdentity=" + organisationIdentity +
        ", registrerinsdatum=" + registrerinsdatum +
        ", antagningsdatum=" + antagningsdatum +
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

  public SourcedValue<Date> getAntagningsdatum() {
    return antagningsdatum;
  }

  public void setAntagningsdatum(SourcedValue<Date> antagningsdatum) {
    this.antagningsdatum = antagningsdatum;
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
