package se.datahamstern.sourced;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

/**
 * @author kalle
 * @since 2012-03-04 00:40
 */
@Persistent(version = 1)
public class SourcedValue<T> extends AbstractSourcedValue<T> {

  public SourcedValue() {
  }

  public SourcedValue(T value) {
    this.value = value;
  }

  private T value;

  @Override
  public T get() {
    return value;
  }

  @Override
  public void set(T value) {
    this.value = value;
  }

  @Override
  public String toString() {
    if (value == null) {
      return "null";
    } else {
      return value.toString();
    }
  }
}
