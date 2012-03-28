package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author kalle
 * @since 2012-03-26 01:57
 */
@Entity(version = 1)
public class Postnummer extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @PrimaryKey
  private String identity;

  private SourcedValue<Boolean> utdelningsnummer = new SourcedValue<Boolean>(false);
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private String _index_postnummer;

  private SourcedValue<String> postnummer = new SourcedValue<String>();
  private SourcedValue<Boolean> boxnummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> svarspostsnummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> tävlingspostsnummer = new SourcedValue<Boolean>(false);
  private SourcedValue<Boolean> storkundsnummer = new SourcedValue<Boolean>(false);

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

  public SourcedValue<Boolean> getUtdelningsnummer() {
    return utdelningsnummer;
  }

  public void setUtdelningsnummer(SourcedValue<Boolean> utdelningsnummer) {
    this.utdelningsnummer = utdelningsnummer;
  }

  public SourcedValue<Boolean> getBoxnummer() {
    return boxnummer;
  }

  public void setBoxnummer(SourcedValue<Boolean> boxnummer) {
    this.boxnummer = boxnummer;
  }

  public SourcedValue<Boolean> getSvarspostsnummer() {
    return svarspostsnummer;
  }

  public void setSvarspostsnummer(SourcedValue<Boolean> svarspostsnummer) {
    this.svarspostsnummer = svarspostsnummer;
  }

  public SourcedValue<Boolean> getTävlingspostsnummer() {
    return tävlingspostsnummer;
  }

  public void setTävlingspostsnummer(SourcedValue<Boolean> tävlingspostsnummer) {
    this.tävlingspostsnummer = tävlingspostsnummer;
  }

  public SourcedValue<Boolean> getStorkundsnummer() {
    return storkundsnummer;
  }

  public void setStorkundsnummer(SourcedValue<Boolean> storkundsnummer) {
    this.storkundsnummer = storkundsnummer;
  }
}
