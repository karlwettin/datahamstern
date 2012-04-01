package se.datahamstern.event;

import java.io.IOException;

/**
 * @author kalle
 * @since 2012-04-01 05:14
 */
public class EventQueueConsumer implements EventConsumer {

  @Override
  public void consume(Event event) throws Exception {
    EventQueue.getInstance().queue(event);
  }
}
