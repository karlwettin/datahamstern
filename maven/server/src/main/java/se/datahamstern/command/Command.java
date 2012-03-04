package se.datahamstern.command;

import org.json.simple.parser.JSONParser;
import se.datahamstern.sourced.SourcedInterface;
import se.datahamstern.sourced.SourcedValue;

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

    boolean addSources = false;

    if (primarySource.getTrustworthiness() != null
        && sourced.getTrustworthiness() != null
        && sourced.getTrustworthiness() <= primarySource.getTrustworthiness()) {

      // we trust this event at least as much as the data in the store

      if (sourced.getLastSeen() == null || sourced.getLastSeen().before(primarySource.getTimestamp())) {
        sourced.setLastSeen(primarySource.getTimestamp());
      }

      sourced.setTrustworthiness(primarySource.getTrustworthiness());
      addSources = true;
    }

    if (addSources) {
      for (int i = event.getSources().size() - 1; i >= 0; i++) {
        Source source = event.getSources().get(i);
        if (!sourced.getSources().contains(source)) {
          sourced.getSources().add(0, source);
        }
      }
    }


  }


  public void updateSourcedValue(SourcedValue sourcedValue, Object value, Event event) {

    Source primarySource = event.getSources().get(0);

    if (value != null) {

      if (value.equals(sourcedValue.get())) {

        // we already knew this

        // todo select data from event with preferred license or trustworthiness, time axis etc

        boolean addSources = false;

        if (primarySource.getTrustworthiness() != null
            && sourcedValue.getTrustworthiness() != null
            && sourcedValue.getTrustworthiness() <= primarySource.getTrustworthiness()) {

          // we trust this event at least as much as the data in the store

          if (sourcedValue.getLastSeen() == null || sourcedValue.getLastSeen().before(primarySource.getTimestamp())) {
            sourcedValue.setLastSeen(primarySource.getTimestamp());
          }

          sourcedValue.setTrustworthiness(primarySource.getTrustworthiness());
          addSources = true;
        }

        if (addSources) {
          for (int i = event.getSources().size() - 1; i >= 0; i--) {
            Source source = event.getSources().get(i);
            if (!sourcedValue.getSources().contains(source)) {
              sourcedValue.getSources().add(0, source);
            }
          }
        }

      } else {

        // a new value


        // todo logic that:

        // filter on license

        // filter on identity

        // let through events with the same value but a greater trustworthiness even if a new one was created later with same value but less trust

        // let through events that are much more trustworthy even if they are a bit old? really think this one through though, new data might be skipped!!

        if (primarySource.getTrustworthiness() != null
            && sourcedValue.getTrustworthiness() != null
            && sourcedValue.getTrustworthiness() > primarySource.getTrustworthiness()) {
          System.currentTimeMillis();
          // todo continue; ? why replace with data we don't trust?
        }


        // todo above
        // currently let through any event which is newer than the currently known value

        if (sourcedValue.getLastSeen() != null && sourcedValue.getLastSeen().after(primarySource.getTimestamp())) {
          return;
        }

        sourcedValue.setTrustworthiness(primarySource.getTrustworthiness());

        sourcedValue.set(value);

        if (sourcedValue.getLastSeen() == null || sourcedValue.getLastSeen().before(primarySource.getTimestamp())) {
          sourcedValue.setLastSeen(primarySource.getTimestamp());
        }
        if (sourcedValue.getFirstSeen() == null || sourcedValue.getFirstSeen().after(primarySource.getTimestamp())) {
          sourcedValue.setFirstSeen(primarySource.getTimestamp());
        }

        sourcedValue.setSources(event.getSources());
      }
    }
  }

}
