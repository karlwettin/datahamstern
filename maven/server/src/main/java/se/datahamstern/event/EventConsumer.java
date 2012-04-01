package se.datahamstern.event;

/**
 * @author kalle
 * @since 2012-04-01 05:16
 */
public interface EventConsumer {

  public void consume(Event event) throws Exception;

}
