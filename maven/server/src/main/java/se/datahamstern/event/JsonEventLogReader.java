package se.datahamstern.event;

import org.json.simple.parser.BufferedJSONStreamReader;
import org.json.simple.parser.JSONStreamReader;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Reads *well formatted* events from a log in JSON format.
 * <p/>
 * This is the same log format as produced by {@link EventManager#queue(Event)}
 * and the same event JSON as produced by {@link EventManager#toJSON(Event)}.
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
 *   "created" : 1330988126187
 *   "events" : [{
 *   "command" : {
 *   "name" : "foo"
 *   "version" : "1"
 *   },
 *   "data" : null,
 *   "sources" : [{
 *     "author" : "should be someone",
 *     "trustworthiness" : 1.0,
 *     "details" : null,
 *     "licence" : "should be something",
 *     "timestamp" : 1330988126187
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
public class JsonEventLogReader {

  public static void main(String[] args) throws Exception {

    JsonEventLogReader r = new JsonEventLogReader();
    r.open(new InputStreamReader(new FileInputStream(new File("data/eventManager/1330980462488.events.json")), "UTF8"));

    Event event;
    while ((event = r.next()) != null) {
      System.out.println(EventManager.toJSON(event));
    }

    Nop.breakpoint();
  }

  private BufferedJSONStreamReader jsr;

  /**
   * @param reader containing json, will be fully consumed by this class.
   * @throws Exception
   */
  public void open(Reader reader) throws Exception {
    jsr = new BufferedJSONStreamReader(reader);

    assertNextAnyOfEvents(JSONStreamReader.Event.START_DOCUMENT);
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
  public Event next() throws Exception {

    assertNextAnyOfEvents(JSONStreamReader.Event.START_OBJECT, JSONStreamReader.Event.END_ARRAY);

    if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {

      assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
      created = new Date(assertNextElementValueLong("closed"));

      assertNextAnyOfEvents(JSONStreamReader.Event.END_OBJECT);
      assertNextAnyOfEvents(JSONStreamReader.Event.END_DOCUMENT);

      return null;


    }

    Event event = new Event();


//    "command" : {
//      "name" : "uppdatera med post från näringslivsregistret",
//      "version" : "1"
//    },

    assertNextElementKey("command");
    assertNextStartObject();

    event.setCommandName(assertNextElementValueString("name"));
    assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
    event.setCommandVersion(assertNextElementValueString("version"));

    assertNextEndObject();

//    "data" : {"firmatyp":"Firma","länsnummer":"01","status":"Konkurs avslutad","namn":"Bertil Enström Anläggnings & Byggnads Aktiebolag","nummersuffix":null,"nummer":"5564437282","nummerprefix":null,"firmaform":"AB"},


    // read content from field "data" which is of any type.

    assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
    assertNextElementKey("data");


    assertNextAnyOfEvents(JSONStreamReader.Event.START_ELEMENT_VALUE, JSONStreamReader.Event.START_ARRAY, JSONStreamReader.Event.START_OBJECT);
    if (jsr.getEvent() == JSONStreamReader.Event.START_ELEMENT_VALUE) {
      Object object = jsr.getObjectValue();
      // todo this instanceof is terrible for performance!
      // todo extend json-simple-kalle in order to read the raw data instead!
      if (object == null) {
        // do nothing
      } else if (object instanceof String) {
        event.setJsonData("\"" + object + "\"");
      } else if (object instanceof Double) {
        event.setJsonData(object.toString());
      } else if (object instanceof Long) {
        event.setJsonData(object.toString());
      } else if (object instanceof Boolean) {
        event.setJsonData((Boolean) object ? "true" : "false");
      }
    } else {

      JSONStreamReader.Event levelUpEvent = jsr.getEvent();
      JSONStreamReader.Event levelDownEvent;

      if (jsr.getEvent() == JSONStreamReader.Event.START_ARRAY) {
        levelDownEvent = JSONStreamReader.Event.END_ARRAY;
      } else /* if (jsr.getEvent() == JSONStreamReader.Event.START_OBJECT) */ {
        levelDownEvent = JSONStreamReader.Event.END_OBJECT;
      }

      StringBuilder jsonData = new StringBuilder(4096);


      jsr.back(1);

      int level = 0;
      do {
        jsr.next();

        if (jsr.getEvent() == levelUpEvent) {
          level++;
        } else if (jsr.getEvent() == levelDownEvent) {
          level--;
        }


        if (jsr.getEvent() == JSONStreamReader.Event.START_OBJECT) {
          jsonData.append("{");
        } else if (jsr.getEvent() == JSONStreamReader.Event.END_OBJECT) {
          jsonData.append("}");
        } else if (jsr.getEvent() == JSONStreamReader.Event.START_ELEMENT_KEY) {
          jsonData.append('"');
          jsonData.append(jsr.getStringValue());
          jsonData.append("\":");
        } else if (jsr.getEvent() == JSONStreamReader.Event.START_ELEMENT_VALUE) {

          // todo this instanceof is terrible for performance!
          // todo extend json-simple-kalle in order to read the raw data instead!

          Object object = jsr.getObjectValue();
          if (object == null) {
            jsonData.append("null");
          } else if (object instanceof String) {
            jsonData.append("\"").append(object).append("\"");
          } else if (object instanceof Number) {
            jsonData.append(object.toString());
          } else if (object instanceof Boolean) {
            jsonData.append((Boolean) object ? "true" : "false");
          } else {
            throw new RuntimeException("Unexpected event value: " + object);
          }

        } else if (jsr.getEvent() == JSONStreamReader.Event.START_ARRAY) {
          jsonData.append("[");
        } else if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {
          jsonData.append("]");
        } else if (jsr.getEvent() == JSONStreamReader.Event.NEXT_VALUE) {
          jsonData.append(",");
        } else {
          throw new RuntimeException("Unexpected event " + jsr.getEvent());
        }


      } while (level > 0);

      event.setJsonData(jsonData.toString());

    }

//    "sources" : [{
//      "author" : "Bolagsverket\/N\u00E4ringslivsregistret",
//      "trustworthiness" : 1.0,
//      "details" : null,
//      "licence" : "public domain",
//      "timestamp" : 1330980473965
//    }]

    assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
    assertNextElementKey("sources");
    assertNextAnyOfEvents(JSONStreamReader.Event.START_ELEMENT_VALUE, JSONStreamReader.Event.START_ARRAY);
    if (jsr.getEvent() == JSONStreamReader.Event.START_ELEMENT_VALUE) {

      if (jsr.getObjectValue() != null) {
        throw new RuntimeException("Expected null value or array but found " + jsr.getObjectValue());
      }

    } else /*if (jsr.getEvent() == JSONStreamReader.Event.START_ARRAY)*/ {

      event.setSources(new ArrayList<Source>());

      while (true) {

        assertNextAnyOfEvents(JSONStreamReader.Event.START_OBJECT, JSONStreamReader.Event.END_ARRAY);
        if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {
          break;
        }

        Source source = new Source();

        source.setAuthor(assertNextElementValueString("author"));

        assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
        source.setTrustworthiness(assertNextElementValueNumber("trustworthiness").floatValue());

        assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
        source.setDetails(assertNextElementValueString("details"));

        assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
        source.setLicense(assertNextElementValueString("licence"));

        assertNextAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
        source.setTimestamp(new Date(assertNextElementValueLong("timestamp")));

        assertNextAnyOfEvents(JSONStreamReader.Event.END_OBJECT);

        event.getSources().add(source);

        assertNextAnyOfEvents(JSONStreamReader.Event.END_ARRAY, JSONStreamReader.Event.NEXT_VALUE);
        if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {
          break;
        }
      }

    }

    assertNextAnyOfEvents(JSONStreamReader.Event.END_OBJECT);

    assertNextAnyOfEvents(JSONStreamReader.Event.END_ARRAY, JSONStreamReader.Event.NEXT_VALUE);
    if (jsr.getEvent() == JSONStreamReader.Event.END_ARRAY) {
      jsr.back(1);
    }


    return event;
  }

  private void assertNextEndObject() throws Exception {
    jsr.next(); // assert end object
    assertAnyOfEvents(JSONStreamReader.Event.END_OBJECT);
  }

  public void close() throws Exception {
    jsr.next(); // closed
    jsr.next(); // end object
    jsr.next(); // end document
  }

  private String assertNextElementValueString() throws Exception {
    jsr.next();
    assertAnyOfEvents(JSONStreamReader.Event.START_ELEMENT_VALUE);
    return jsr.getStringValue();
  }


  private String assertNextElementValueString(String key) throws Exception {
    assertNextElementKeyAndValue(key);
    return jsr.getStringValue();
  }

  private Double assertNextElementValueDouble(String key) throws Exception {
    assertNextElementKeyAndValue(key);
    return jsr.getDoubleValue();
  }

  private Long assertNextElementValueLong(String key) throws Exception {
    assertNextElementKeyAndValue(key);
    return jsr.getLongValue();
  }

  private Number assertNextElementValueNumber(String key) throws Exception {
    assertNextElementKeyAndValue(key);
    return jsr.getNumberValue();
  }

  private void assertNextElementKeyAndValue(String key) throws Exception {
    assertNextElementKey(key);
    jsr.next();
    assertAnyOfEvents(JSONStreamReader.Event.START_ELEMENT_VALUE);
  }

  private void assertNextElementKey(String string) throws Exception {
    jsr.next();
    assertElementKey(string);
  }

  private void assertNextStartObject() throws Exception {
    jsr.next();
    assertAnyOfEvents(JSONStreamReader.Event.START_OBJECT);
  }

  private void assertNextNextValue() throws Exception {
    jsr.next();
    assertAnyOfEvents(JSONStreamReader.Event.NEXT_VALUE);
  }

  private void assertElementKey(String string) throws Exception {
    assertAnyOfEvents(JSONStreamReader.Event.START_ELEMENT_KEY);
    if (!string.equals(jsr.getStringValue())) {
      throw new RuntimeException("Expected element key " + string + " but found " + jsr.getStringValue());
    }
  }

  private void assertNextAnyOfEvents(JSONStreamReader.Event... events) throws Exception {
    jsr.next();
    assertAnyOfEvents(events);
  }

  private void assertAnyOfEvents(JSONStreamReader.Event... events) throws Exception {
    for (JSONStreamReader.Event event : events) {
      if (jsr.getEvent() == event) {
        return;
      }
    }
    throw new RuntimeException("Expected event of type " + Arrays.toString(events) + " but found " + jsr.getEvent());

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
