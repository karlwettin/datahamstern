package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2012-03-05 05:11
 */
@Entity(version = 1)
public class Dokument extends AbstractSourced implements DomainEntityObject {

  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private List<SourcedValue<String>> dokumentversionerIdentity = new ArrayList<SourcedValue<String>>();

  @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity = Dokumentversion.class)
  private List<String> _index_dokumentversionerIdentity;

  private SourcedValue<String> beskrivning = new SourcedValue<String>();

  @Override
  public String toString() {
    return "Dokument{" +
        "beskrivning=" + beskrivning +
        ", dokumentversionerIdentity=" + dokumentversionerIdentity +
        ", identity='" + identity + '\'' +
        ", _index_dokumentversionerIdentity=" + _index_dokumentversionerIdentity +
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

  public SourcedValue<String> getBeskrivning() {
    return beskrivning;
  }

  public void setBeskrivning(SourcedValue<String> beskrivning) {
    this.beskrivning = beskrivning;
  }

  public List<SourcedValue<String>> getDokumentversionerIdentity() {
    return dokumentversionerIdentity;
  }

  public void setDokumentversionerIdentity(List<SourcedValue<String>> dokumentversionerIdentity) {
    this.dokumentversionerIdentity = dokumentversionerIdentity;
  }

  public List<String> get_index_dokumentversionerIdentity() {
    return _index_dokumentversionerIdentity;
  }

  public void set_index_dokumentversionerIdentity(List<String> _index_dokumentversionerIdentity) {
    this._index_dokumentversionerIdentity = _index_dokumentversionerIdentity;
  }
}
