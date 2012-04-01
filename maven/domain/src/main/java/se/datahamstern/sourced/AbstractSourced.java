package se.datahamstern.sourced;

import com.sleepycat.persist.model.Persistent;
import se.datahamstern.command.Source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author kalle
 * @since 2012-03-04 00:40
 */
@Persistent(version = 1)
public abstract class AbstractSourced implements SourcedInterface {

  private Date firstSeen;
  private Date lastSeen;

  private Float trustworthiness;

  private Sources sources = new Sources();


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractSourced that = (AbstractSourced) o;

    if (firstSeen != null ? !firstSeen.equals(that.firstSeen) : that.firstSeen != null) return false;
    if (lastSeen != null ? !lastSeen.equals(that.lastSeen) : that.lastSeen != null) return false;
    if (sources != null ? !sources.equals(that.sources) : that.sources != null) return false;
    if (trustworthiness != null ? !trustworthiness.equals(that.trustworthiness) : that.trustworthiness != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = firstSeen != null ? firstSeen.hashCode() : 0;
    result = 31 * result + (lastSeen != null ? lastSeen.hashCode() : 0);
    result = 31 * result + (trustworthiness != null ? trustworthiness.hashCode() : 0);
    result = 31 * result + (sources != null ? sources.hashCode() : 0);
    return result;
  }

  @Override
  public Float getTrustworthiness() {
    return trustworthiness;
  }

  @Override
  public void setTrustworthiness(Float trustworthiness) {
    this.trustworthiness = trustworthiness;
  }

  @Override
  public Date getFirstSeen() {
    return firstSeen;
  }

  @Override
  public void setFirstSeen(Date firstSeen) {
    this.firstSeen = firstSeen;
  }

  @Override
  public Date getLastSeen() {
    return lastSeen;
  }

  @Override
  public void setLastSeen(Date lastSeen) {
    this.lastSeen = lastSeen;
  }

  @Override
  public Sources getSources() {
    return sources;
  }

  @Override
  public void setSources(Sources sources) {
    this.sources = sources;
  }
}
