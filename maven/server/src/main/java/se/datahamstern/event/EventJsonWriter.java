package se.datahamstern.event;

import org.apache.commons.lang.StringEscapeUtils;
import se.datahamstern.command.Source;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * @author kalle
 * @since 2012-03-06 01:51
 */
public class EventJsonWriter {

  public static String toJSON(Event event) throws IOException {
    StringWriter sw = new StringWriter(4096);
    writeJSON(sw, event);
    return sw.toString();
  }

  public static void writeJSON(Writer json, Event event) throws IOException {

    json.write("{");
    json.write("\n  \"command\" : { ");
    json.write("\n    \"name\" : \"");
    json.write(event.getCommandName());
    json.write("\"");
    json.write(",\n    \"version\" : \"");
    json.write(event.getCommandVersion());
    json.write("\"");
    json.write("\n  }");

    json.write(",\n  \"data\" : ");
    json.write(event.getJsonData());

    json.write(",\n  \"sources\" : ");
    if (event.getSources() == null || event.getSources().isEmpty()) {
      json.write("null");
    } else {
      json.write("[");
      for (Iterator<Source> sourceIterator = event.getSources().iterator(); sourceIterator.hasNext(); ) {
        Source source = sourceIterator.next();

        json.write("{");


        json.write("\n    \"author\" : ");
        if (source.getAuthor() == null) {
          json.write("null");
        } else {
          json.write('"');
          json.write(StringEscapeUtils.escapeJavaScript(source.getAuthor()));
          json.write('"');
        }

        json.write(",\n    \"trustworthiness\" : ");
        if (source.getTrustworthiness() == null) {
          json.write("null");
        } else {
          json.write(source.getTrustworthiness().toString());
        }

        json.write(",\n    \"details\" : ");
        if (source.getDetails() == null) {
          json.write("null");
        } else {
          json.write('"');
          json.write(StringEscapeUtils.escapeJavaScript(source.getDetails()));
          json.write('"');
        }

        json.write(",\n    \"licence\" : ");
        if (source.getLicense() == null) {
          json.write("null");
        } else {
          json.write('"');
          json.write(StringEscapeUtils.escapeJavaScript(source.getLicense()));
          json.write('"');
        }

        json.write(",\n    \"timestamp\" : ");
        if (source.getTrustworthiness() == null) {
          json.write("null");
        } else {
          json.write(String.valueOf(source.getTimestamp().getTime()));
        }

        json.write("\n  }");

        if (sourceIterator.hasNext()) {
          json.write(", ");
        }
      }
      json.write("]\n");
    }

    json.write("}\n");

  }

}
