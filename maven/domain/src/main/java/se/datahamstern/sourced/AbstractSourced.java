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

  private List<Source> sources = new ArrayList<Source>();


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
  public List<Source> getSources() {
    return sources;
  }

  @Override
  public void setSources(List<Source> sources) {
    this.sources = sources;
  }
}
