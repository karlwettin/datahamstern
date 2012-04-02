package se.datahamstern.domain.hydda;

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
 * @since 2012-03-05 05:11
 */
@Entity(version = 1)
public class Dokument extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;

  @Override
  public void accept(DomainEntityObjectVisitor visitor) {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private List<SourcedValue<String>> dokumentversionerIdentity = new ArrayList<SourcedValue<String>>();

  @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity = Dokumentversion.class)
  private List<String> _index_dokumentversionerIdentity;

  private SourcedValue<String> beskrivning = new SourcedValue<String>();

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Dokument dokument = (Dokument) o;

    if (beskrivning != null ? !beskrivning.equals(dokument.beskrivning) : dokument.beskrivning != null) return false;
    if (dokumentversionerIdentity != null ? !dokumentversionerIdentity.equals(dokument.dokumentversionerIdentity) : dokument.dokumentversionerIdentity != null) return false;
    if (identity != null ? !identity.equals(dokument.identity) : dokument.identity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (dokumentversionerIdentity != null ? dokumentversionerIdentity.hashCode() : 0);
    result = 31 * result + (beskrivning != null ? beskrivning.hashCode() : 0);
    return result;
  }

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
