package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import org.w3c.dom.Document;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-05 04:47
 */
@Entity(version = 1)
public class Arsredovisning extends AbstractSourced implements DomainEntityObject {

  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Organisation.class)
  private String _index_organisationIdentity;

  private SourcedValue<String> organisationIdentity;

  /** stereotypsår, index med året från datumFrom. */
  private Integer _index_år;

  /** när årsrapporten registrerades hos bolagsverket */
  private SourcedValue<Date> registrerinsdatum = new SourcedValue<Date>();


  /** från och med vilken datum värdena i årsrapporten behandlar */
  private SourcedValue<Date> datumFrom;
  /** till och med vilken datum värdena i årsrapporten behandlar */
  private SourcedValue<Date> datumTo;

  //private Document dokument;

  @PrimaryKey
  private String identity;


  @Override
  public String toString() {
    return "Arsredovisning{" +
        "datumFrom=" + datumFrom +
        ", datumTo=" + datumTo +
        ", registrerinsdatum=" + registrerinsdatum +
        ", organisationIdentity=" + organisationIdentity +
        ", identity='" + identity + '\'' +
        ", _index_år=" + _index_år +
        ", _index_organisationIdentity='" + _index_organisationIdentity + '\'' +
        '}';
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

  public Integer get_index_år() {
    return _index_år;
  }

  public void set_index_år(Integer _index_år) {
    this._index_år = _index_år;
  }

  public SourcedValue<Date> getDatumFrom() {
    return datumFrom;
  }

  public void setDatumFrom(SourcedValue<Date> datumFrom) {
    this.datumFrom = datumFrom;
  }

  public SourcedValue<Date> getDatumTo() {
    return datumTo;
  }

  public void setDatumTo(SourcedValue<Date> datumTo) {
    this.datumTo = datumTo;
  }
}
