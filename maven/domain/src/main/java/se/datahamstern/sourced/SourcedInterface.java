package se.datahamstern.sourced;

import com.sleepycat.persist.model.Persistent;
import se.datahamstern.command.Source;

import java.util.Date;
import java.util.List;

/**
 * @author kalle
 * @since 2012-03-04 00:40
 */
@Persistent(version = 1)
public interface SourcedInterface {

  public abstract Date getFirstSeen();
  public abstract void setFirstSeen(Date firstSeen);

  public abstract Date getLastSeen();
  public abstract void setLastSeen(Date lastSeen);

  public abstract Float getTrustworthiness();
  public abstract void setTrustworthiness(Float trustworthiness);



  public abstract List<Source> getSources();
  public abstract void setSources(List<Source> sources);
}
