package se.datahamstern.event;

import org.apache.commons.lang.StringEscapeUtils;
import se.datahamstern.Datahamstern;

import java.io.*;

/**
 * @author kalle
 * @since 2012-03-06 04:41
 */
public class JsonEventLogWriter implements EventConsumer {

  private boolean hasHeader = false;
  private Writer writer;
  private File file;

  /**
   * @param writer json output, will not be closed
   */
  public JsonEventLogWriter(Writer writer) {
    this.writer = writer;
  }

  /**
   * @param file this file will be created or overwritten when the first event is written.
   */
  public JsonEventLogWriter(File file) {
    this.file = file;
  }

  @Override
  public void consume(Event event) throws Exception {
    synchronized (this) {
      if (!hasHeader) {
        if (file != null) {
          writer = new OutputStreamWriter(new FileOutputStream(file), "UTF8");
        }
        writer.write("{\n  \"version\" : \"1\",");
        writer.write("\n  \"system\" : \"");
        writer.write(StringEscapeUtils.escapeJavaScript(Datahamstern.getInstance().getSystemUUID()));
        writer.write("\",");
        writer.write("\n  \"created\" : ");
        writer.write(String.valueOf(System.currentTimeMillis()));
        writer.write(",\n  \"events\" : [\n");
        hasHeader = true;
      }

      JsonEventWriter.writeJSON(writer, event);
      writer.write("\n,\n");
      writer.flush();
    }
  }

  public void close() throws IOException {
    synchronized (this) {
      if (writer != null) {
        writer.write("\n],\n  \"closed\" : ");
        writer.write(String.valueOf(System.currentTimeMillis()));
        writer.write("\n}\n");
        writer.flush();

        if (file != null) {
          writer.close();
        }
      }
    }

  }

}
