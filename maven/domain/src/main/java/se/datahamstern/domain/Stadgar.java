package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
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
  public void accept(DomainObjectVisitor visitor) {
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
