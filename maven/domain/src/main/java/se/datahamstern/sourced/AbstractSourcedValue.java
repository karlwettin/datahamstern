package se.datahamstern.sourced;

import com.sleepycat.persist.model.Persistent;

/**
 * @author kalle
 * @since 2012-03-04 00:40
 */
@Persistent(version = 1)
public abstract class AbstractSourcedValue<T> extends AbstractSourced implements SourcedValueInterface<T> {

  @Override
  public abstract T get();
  @Override
  public abstract void set(T value);

}
