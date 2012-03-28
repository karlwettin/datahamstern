package se.datahamstern.domain;

import com.sleepycat.persist.model.*;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-26 02:19
 */
@Entity(version = 1)
public class Gatuadress extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @PrimaryKey
  private String identity;

  private SourcedValue<String> gatunamn = new SourcedValue<String>();

  private SourcedValue<String> gatunummer = new SourcedValue<String>();
  private SourcedValue<GeographicCoordinate> coordinate = new SourcedValue<GeographicCoordinate>();

  private SourcedValue<String> postnummerIdentity = new SourcedValue<String>();
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Postnummer.class)
  private String _index_postnummerIdentity;

  /** todo postortIdentity -- implement class Postort */
  private SourcedValue<String> postort = new SourcedValue<String>();

  @Override
  public void accept(DomainObjectVisitor visitor) {
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

  public SourcedValue<String> getPostort() {
    return postort;
  }

  public void setPostort(SourcedValue<String> postort) {
    this.postort = postort;
  }

  public String get_index_postnummerIdentity() {
    return _index_postnummerIdentity;
  }

  public void set_index_postnummerIdentity(String _index_postnummerIdentity) {
    this._index_postnummerIdentity = _index_postnummerIdentity;
  }

  public SourcedValue<String> getGatunamn() {
    return gatunamn;
  }

  public void setGatunamn(SourcedValue<String> gatunamn) {
    this.gatunamn = gatunamn;
  }

  public SourcedValue<String> getPostnummerIdentity() {
    return postnummerIdentity;
  }

  public void setPostnummerIdentity(SourcedValue<String> postnummerIdentity) {
    this.postnummerIdentity = postnummerIdentity;
  }

  public SourcedValue<String> getGatunummer() {
    return gatunummer;
  }

  public void setGatunummer(SourcedValue<String> gatunummer) {
    this.gatunummer = gatunummer;
  }

  public SourcedValue<GeographicCoordinate> getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(SourcedValue<GeographicCoordinate> coordinate) {
    this.coordinate = coordinate;
  }
}
