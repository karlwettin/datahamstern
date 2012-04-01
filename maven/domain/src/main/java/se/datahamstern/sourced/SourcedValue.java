package se.datahamstern.sourced;

import com.sleepycat.persist.model.Persistent;

/**
 * @author kalle
 * @since 2012-03-04 00:40
 */
@Persistent(version = 1)
public class SourcedValue<T> extends AbstractSourced implements SourcedValueInterface<T> {

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
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SourcedValue that = (SourcedValue) o;

    if (value != null ? !value.equals(that.value) : that.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
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
