package se.datahamstern;

import junit.framework.TestCase;
import org.json.JSONObject;
import se.datahamstern.command.Event;
import se.datahamstern.command.Source;
import se.datahamstern.external.naringslivsregistret.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-04 03:43
 */
public class TestNaringslivsregistret extends TestCase {

  public void test() throws Exception {

    Datahamstern.getInstance().open();
    try {

      final Date started = new Date();

      HarvestNaringslivsregistret harvestNaringslivsregistret = new HarvestNaringslivsregistret();
      harvestNaringslivsregistret.harvest("5560743087", "5560743090", new CreateEventsVisitor());

      final Date finished = new Date();


      Datahamstern.getInstance().getEventStore().executeUpdatedEvents();


    } finally {
      Datahamstern.getInstance().close();
    }
  }

}
