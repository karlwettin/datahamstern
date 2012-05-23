package se.datahamstern.command;

import org.json.simple.parser.JSONParser;
import se.datahamstern.Nop;
import se.datahamstern.event.Event;
import se.datahamstern.sourced.SourcedInterface;
import se.datahamstern.sourced.SourcedValue;

import javax.annotation.Nullable;

/**
 * @author kalle
 * @since 2012-03-03 23:26
 */
public abstract class Command {

  public abstract String getCommandName();

  public abstract String getCommandVersion();

  public abstract void execute(Event event, JSONParser jsonParser) throws Exception;

  public void updateSourced(SourcedInterface sourced, Event event) {
    Source primarySource = event.getSources().get(0);

    // todo select data from event with preferred license or trustworthiness, time axis etc


    // we trust this event at least as much as the data in the store

    if (sourced.getLastSeen() == null || sourced.getLastSeen().before(primarySource.getTimestamp())) {
      sourced.setLastSeen(primarySource.getTimestamp());
    }

    sourced.setTrustworthiness(primarySource.getTrustworthiness());

    for (Source source : event.getSources()) {
      sourced.getSources().getAuthors().add(source.getAuthor());
      sourced.getSources().getLicenses().add(source.getLicense());
    }

    if (sourced.getLastSeen() == null || sourced.getLastSeen().before(primarySource.getTimestamp())) {
      sourced.setLastSeen(primarySource.getTimestamp());
    }
    if (sourced.getFirstSeen() == null || sourced.getFirstSeen().after(primarySource.getTimestamp())) {
      sourced.setFirstSeen(primarySource.getTimestamp());
    }

  }


  public void updateSourcedValue(SourcedValue sourcedValue, @Nullable Object value, Event event) {

    Source primarySource = event.getSources().get(0);

    if (value != null) {

      if (value.equals(sourcedValue.get())) {

        // we already knew this value

        // todo select data from event with preferred license or trustworthiness, time axis etc

        // we trust this event at least as much as the data in the store

        if (sourcedValue.getLastSeen() == null || sourcedValue.getLastSeen().before(primarySource.getTimestamp())) {
          sourcedValue.setLastSeen(primarySource.getTimestamp());
        }

        sourcedValue.setTrustworthiness(primarySource.getTrustworthiness());

      } else {

        // this is a new or changed value


        // todo logic that:

        // filter on license

        // filter on identity

        // let through events with the same value but a greater trustworthiness even if a new one was created later with same value but less trust

        // let through events that are much more trustworthy even if they are a bit old? really think this one through though, new data might be skipped!!


        // todo above
        // currently let through any event which is newer than the currently known value

        if (sourcedValue.getLastSeen() == null || sourcedValue.getLastSeen().before(primarySource.getTimestamp())) {
          sourcedValue.set(value);
        }


      }

      for (Source source : event.getSources()) {
        sourcedValue.getSources().getAuthors().add(source.getAuthor());
        sourcedValue.getSources().getLicenses().add(source.getLicense());
      }

      if (sourcedValue.getLastSeen() == null || sourcedValue.getLastSeen().before(primarySource.getTimestamp())) {
        sourcedValue.setLastSeen(primarySource.getTimestamp());
      }

      if (sourcedValue.getFirstSeen() == null || sourcedValue.getFirstSeen().after(primarySource.getTimestamp())) {
        sourcedValue.setFirstSeen(primarySource.getTimestamp());
      }

    } else {

      // value is null. todo does this mean we should null it out?!

    }
  }

}
