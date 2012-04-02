package se.datahamstern.event;

import com.sleepycat.persist.EntityCursor;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.command.CommandManager;
import se.datahamstern.command.Source;
import se.datahamstern.io.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is where you add and process incoming events
 *
 * @author kalle
 * @since 2012-03-04 23:07
 */
public class EventExecutor {

  private static EventExecutor instance = new EventExecutor();

  private EventExecutor() {
  }

  private File dataPath;

  /**
   * where event logs can be placed for automatic import to system
   */
  private File inbox;

  /**
   * where all events created by this system are saved in event logs.
   */
  private File outbox;

  public static EventExecutor getInstance() {
    return instance;
  }

  private Writer currentOutboxEventLog;

  public void open() throws Exception {
    if (dataPath == null) {
      throw new NullPointerException("No data path set");
    }
    dataPath = FileUtils.mkdirs(dataPath);

    outbox = FileUtils.mkdirs(new File(dataPath, "outbox"));

    inbox = FileUtils.mkdirs(new File(dataPath, "inbox"));

  }

  public void close() throws Exception {

    if (currentOutboxEventLog != null) {
      currentOutboxEventLog.write("\n],\n  \"closed\" : ");
      currentOutboxEventLog.write(String.valueOf(System.currentTimeMillis()));
      currentOutboxEventLog.write("\n}\n");
      currentOutboxEventLog.close();
    }
  }

  public File getDataPath() {
    return dataPath;
  }

  public void setDataPath(File dataPath) {
    this.dataPath = dataPath;
  }

  public Writer getCurrentOutboxEventLog() {
    return currentOutboxEventLog;
  }

  public void setCurrentOutboxEventLog(Writer currentOutboxEventLog) {
    this.currentOutboxEventLog = currentOutboxEventLog;
  }

  private static Pattern eventLogFilenamePattern = Pattern.compile("^([0-9]+).*\\.json$");

  public synchronized void pollInbox() throws Exception {
    File[] files = inbox.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isFile() && eventLogFilenamePattern.matcher(file.getName()).find();
      }
    });
    Arrays.sort(files, new Comparator<File>() {
      @Override
      public int compare(File file, File file1) {
        return get(file).compareTo(get(file1));
      }

      private Long get(File file) {
        Matcher matcher = eventLogFilenamePattern.matcher(file.getName());
        matcher.find();
        return Long.valueOf(matcher.group(1));
      }
    });


    final AtomicInteger totalCounter = new AtomicInteger(0);
    final AtomicInteger debugCounter = new AtomicInteger(0);

    JSONParser jsonParser = new JSONParser();

    for (File file : files) {
      File seen = new File(file.getAbsolutePath() + ".seen");
      if (!seen.exists()) {
        try {
          System.out.println("Processing " + file.getAbsoluteFile());
          new FileOutputStream(seen, false).close();
          StreamingJsonEventLogReader events = new StreamingJsonEventLogReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
          Event event;
          while ((event = events.next()) != null) {
            try {
              totalCounter.incrementAndGet();
              execute(event, jsonParser);
              if (debugCounter.incrementAndGet() >= 1431) {
                debugCounter.set(0);
                System.out.println(totalCounter.get()  + " events executed so far during this batch. Last event: " + event.toString());
              }
            } catch (Exception e) {
              System.out.println("Failed to execute event " + event.toString());
              e.printStackTrace(System.out);
            }
          }
          System.out.println("Done processing " + file.getAbsoluteFile());
        } catch (Exception e) {
          // log.error("Exception while importing event log " + file.getAbsolutePath(), e);
          e.printStackTrace(System.out);
        }
      }
    }
  }


  private void assertWellDescribedEvent(Event event) {
    if (event.getCommandName() == null) {
      throw new NullPointerException("Command name not set in event!");
    }
    if (event.getCommandVersion() == null) {
      throw new NullPointerException("Command version not set in event!");
    }
    if (event.getSources() == null || event.getSources().isEmpty()) {
      throw new NullPointerException("No sources in event!");
    }
    if (event.getJsonData() == null) {
      throw new NullPointerException("No jsonData in event!");
    }

    for (Source source : event.getSources()) {
      if (source.getTimestamp() == null) {
        throw new NullPointerException("Source is missing timestamp! " + source);
      }
      if (source.getAuthor() == null) {
        throw new NullPointerException("Source is missing author! " + source);
      }
      if (source.getLicense() == null) {
        throw new NullPointerException("Source is missing license! " + source);
      }
      if (source.getTrustworthiness() == null) {
        throw new NullPointerException("Source is missing trustworthiness! " + source);
      }
      if (source.getDetails() == null) {
//        log.debug("Source is missing details! " + source);
      }
    }
  }

  public void execute(Event event, JSONParser jsonParser) throws Exception {

    assertWellDescribedEvent(event);

    if (event.getIdentity() == null) {
      event.setIdentity(UUID.randomUUID().toString());

      if (currentOutboxEventLog == null) {
        currentOutboxEventLog = new OutputStreamWriter(new FileOutputStream(new File(outbox, System.currentTimeMillis() + "." + Datahamstern.getInstance().getSystemUUID() + ".events.json")));
        currentOutboxEventLog.write("{\n  \"system\" : \"");
        currentOutboxEventLog.write(StringEscapeUtils.escapeJavaScript(Datahamstern.getInstance().getSystemUUID()));
        currentOutboxEventLog.write("\",");
        currentOutboxEventLog.write("\n  \"created\" : ");
        currentOutboxEventLog.write(String.valueOf(System.currentTimeMillis()));
        currentOutboxEventLog.write(",\n  \"events\" : [\n");
        currentOutboxEventLog.flush();
      }

      JsonEventWriter.writeJSON(currentOutboxEventLog, event);
      currentOutboxEventLog.write("\n,\n");
      currentOutboxEventLog.flush();
    }


    CommandManager.getInstance().commandFactory(event.getCommandName(), event.getCommandVersion()).execute(event, jsonParser);
  }

  @Deprecated
  public static Event fromJSON(Reader json) throws Exception {
    return new StreamingJsonEventReader(json).next();
  }

  @Deprecated
  public static String toJSON(Event event) throws IOException {
    StringWriter json = new StringWriter(4096);
    JsonEventWriter.writeJSON(json, event);
    return json.toString();
  }


  public File getInbox() {
    return inbox;
  }

  public void setInbox(File inbox) {
    this.inbox = inbox;
  }

  public File getOutbox() {
    return outbox;
  }

  public void setOutbox(File outbox) {
    this.outbox = outbox;
  }
}
