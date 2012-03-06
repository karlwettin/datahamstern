package se.datahamstern.event;

import org.json.simple.parser.JSONStreamReader;
import se.datahamstern.Nop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

/**
 * Reads *well formatted* events from a log in JSON format.
 * <p/>
 * This is the same log format as produced by {@link EventQueue#queue(Event)}
 * and the same event JSON as produced by {@link EventQueue#toJSON(Event)}.
 * <p/>
 * It uses a lexer for performance reasons,
 * which also is the reason for the following:
 * <p/>
 * Order of fields must be exactly as defined here
 * and all fields must be present but may contains value null.
 * <p/>
 * It throws an exception if there is a problem.
 * <p/>
 * <p/>
 * Example log:
 * <p/>
 * <pre>
 * {
 *   "system" : "my servers unique identity",
 *   "created" : 1330988126187,
 *   "events" : [{
 *     "command" : {
 *       "name" : "foo",
 *       "version" : "1",
 *     },
 *     "data" : null,
 *     "sources" : [{
 *       "author" : "should be someone",
 *       "trustworthiness" : 1.0,
 *       "details" : null,
 *       "licence" : "should be something",
 *       "timestamp" : 1330988126187
 *     }]
 *   }],
 *   "closed" : 1330988526643
 * }
 * </pre>
 * <p/>
 *
 * @author kalle
 * @since 2012-03-05 22:46
 */
public class StreamingJsonEventLogReader extends StreamingJsonReader {

  public static void main(String[] args) throws Exception {

    StreamingJsonEventLogReader r = new StreamingJsonEventLogReader(new InputStreamReader(new FileInputStream(new File("data/event/outbox/1330980462488.events.json")), "UTF8"));

    Event event;
    while ((event = r.next()) != null) {
      System.out.println(EventQueue.toJSON(event));
    }

    Nop.breakpoint();
  }

  /**
   * @param reader containing json, will be fully consumed by this class.
   * @throws Exception
   */
  public StreamingJsonEventLogReader(Reader reader) throws Exception {
    super(reader);

    assertNextAnyOfEvents(JSONStreamReader.Event.START_OBJECT);

    created = new Date(assertNextElementValueLong("created"));

    assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
    assertNextElementKey("events");

    assertNextAnyOfEvents(JSONStreamReader.Event.START_ARRAY);

  }

  private Date created;
  private Date closed;


  /**
   * @return
   * @throws Exception
   */
  @Override
  public Event next() throws Exception {

    assertNextAnyOfEvents(JSONStreamReader.Event.START_OBJECT, JSONStreamReader.Event.END_ARRAY);

    if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {

      assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
      created = new Date(assertNextElementValueLong("closed"));

      assertNextAnyOfEvents(JSONStreamReader.Event.END_OBJECT);
      assertNextAnyOfEvents(JSONStreamReader.Event.END_DOCUMENT);

      return null;


    }

    jsr.back(1);

    Event event = super.next();

    assertNextAnyOfEvents(JSONStreamReader.Event.END_ARRAY, JSONStreamReader.Event.NEXT_VALUE);
    if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {
      jsr.back(1);
    }

    return event;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getClosed() {
    return closed;
  }

  public void setClosed(Date closed) {
    this.closed = closed;
  }
}
