package se.datahamstern.event.v0;

import org.json.simple.parser.JSONStreamReader;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventQueue;
import se.datahamstern.event.JsonEventLogReader;
import se.datahamstern.event.JsonEventLogWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.UUID;

/**
 * Reads *well formatted* events from a log in JSON format.
 * <p/>
 * This is the same log format as produced by {@link se.datahamstern.event.EventQueue#queue(se.datahamstern.event.Event)}
 * and the same event JSON as produced by {@link se.datahamstern.event.EventQueue#toJSON(se.datahamstern.event.Event)}.
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
public class StreamingJsonEventLogReader extends StreamingJsonEventReader implements JsonEventLogReader {

  public static void main(String[] args) throws Exception {

    Datahamstern.getInstance().open();

    for (File file : new File("data/event/outbox_v0").listFiles()) {
      if (file.isFile()) {

        JsonEventLogWriter w = new JsonEventLogWriter(new File(EventQueue.getInstance().getOutbox(), file.getName()));

        try {
          StreamingJsonEventLogReader r = new StreamingJsonEventLogReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

          Event event;
          while ((event = r.next()) != null) {
            event.setIdentity(UUID.randomUUID().toString());
            w.write(event);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        w.close();


      }
    }

    Datahamstern.getInstance().close();

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
