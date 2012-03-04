package se.datahamstern.external.naringslivsregistret;

import org.json.simple.JSONObject;
import se.datahamstern.Datahamstern;
import se.datahamstern.EventManager;
import se.datahamstern.Nop;
import se.datahamstern.command.Event;
import se.datahamstern.command.Source;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-04 06:30
 */
public class CreateEventsVisitor extends HarvestNaringslivsregistretVisitor {

  private Source source;

  @Override
  public void start(HarvestNaringslivsregistret harvester) throws Exception {
    source = new Source();
    source.setAuthor("bolagsverket/näringslivsregistret");
    source.setLicense("public domain");
    source.setTimestamp(new Date());
    source.setTrustworthiness(1f);
  }

  @Override
  public void found(HarvestNaringslivsregistret harvester, NaringslivsregistretResult result) throws Exception {
    Event event = NaringslivsregistretCommand.eventFactory(result, source);
    EventManager.getInstance().queue(event);
    Nop.breakpoint();
  }


}

