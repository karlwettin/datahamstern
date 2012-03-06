package se.datahamstern;

import junit.framework.TestCase;
import se.datahamstern.command.Source;
import se.datahamstern.event.*;
import se.datahamstern.event.StreamingJsonEventReader;
import se.datahamstern.util.CloneUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-06 03:31
 */
public class TestEventSerialization extends TestCase {

  public void test() throws Exception {

    Event event = new Event();

    event.setIdentity("not serialized as json");
    event.set_local_timestamp(new Date());

    event.setJsonData("{\"serialized\":\"as\",\"json\":[null,1,2.3,false,true]}");
    event.setCommandName("the test command");
    event.setCommandVersion("the test command version");
    event.setCommandVersion("version 123");

    event.setSources(new ArrayList<Source>());
    event.getSources().add(new Source());
    event.getSources().get(0).setTimestamp(new Date());
    event.getSources().get(0).setAuthor("an identity");
    event.getSources().get(0).setDetails("this is a test source, not good for anything");
    event.getSources().get(0).setLicense("public domain");
    event.getSources().get(0).setTrustworthiness(0f);

    Event deepClone = CloneUtils.deepClone(event);

    String jsonClone = JsonEventWriter.toJSON(event);

    Event fromJsonClone = new StreamingJsonEventReader(new StringReader(jsonClone)).next();

    assertEquals(deepClone, event);
    assertEquals(event, deepClone);

    assertEquals(fromJsonClone, event);
    assertEquals(event, fromJsonClone);

    assertEquals(deepClone, fromJsonClone);
    assertEquals(fromJsonClone, deepClone);

  }

}
