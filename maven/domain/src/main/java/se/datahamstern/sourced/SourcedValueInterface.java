package se.datahamstern.sourced;

import com.sleepycat.persist.model.Persistent;

/**
 * @author kalle
 * @since 2012-03-04 00:40
 */
@Persistent(version = 1)
public interface SourcedValueInterface<T> extends SourcedInterface {

  public abstract T get();
  public abstract void set(T value);

}
