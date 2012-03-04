package se.datahamstern.external.naringslivsregistret;

import org.json.simple.JSONObject;
import se.datahamstern.Datahamstern;
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
    Event event = new Event();
    event.setCommandName(NaringslivsregistretCommand.COMMAND_NAME);
    event.setCommandVersion(NaringslivsregistretCommand.COMMAND_VERSION);

    event.setSources(new ArrayList<Source>(1));
    event.getSources().add(source);

    JSONObject json = new JSONObject();
    json.put("nummerprefix", result.getNummerprefix());
    json.put("nummer", result.getNummer());
    json.put("nummersuffix", result.getNummersuffix());
    json.put("namn", result.getNamn());
    json.put("länsnummer", result.getLänsnummer());
    json.put("typ", result.getTyp());
    json.put("status", result.getStatus());
    event.setJsonData(json.toString());

    Datahamstern.getInstance().getEventStore().put(event);
  }
}

