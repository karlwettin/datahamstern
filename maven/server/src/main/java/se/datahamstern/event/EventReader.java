package se.datahamstern.event;

/**
 * @author kalle
 * @since 2012-03-06 04:23
 */
public interface EventReader {

  public abstract Event next() throws Exception;

}
