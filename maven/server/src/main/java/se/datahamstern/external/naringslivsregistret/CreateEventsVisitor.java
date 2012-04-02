package se.datahamstern.external.naringslivsregistret;

import org.json.simple.parser.JSONParser;
import se.datahamstern.event.Event;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.event.EventExecutor;

import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-04 06:30
 */
public class CreateEventsVisitor extends HarvestNaringslivsregistretVisitor {

  private Source source;
  private JSONParser jsonParser = new JSONParser();

  @Override
  public void start(HarvestNaringslivsregistret harvester) throws Exception {
    source = NaringslivsregistretCommand.defaultSourceFactory();
  }

  @Override
  public void found(HarvestNaringslivsregistret harvester, NaringslivsregistretResult result) throws Exception {
    source.setTimestamp(new Date());
    Event event = NaringslivsregistretCommand.eventFactory(result, source);
    EventExecutor.getInstance().execute(event, jsonParser);
    Nop.breakpoint();
  }


}

