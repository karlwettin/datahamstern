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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is where you add and process incoming events
 *
 * @author kalle
 * @since 2012-03-04 23:07
 */
public class EventQueue {

  private static EventQueue instance = new EventQueue();

  private EventQueue() {
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

  private EventStore eventStore;


  public static EventQueue getInstance() {
    return instance;
  }

  private Writer currentOutboxEventLog;

  public void open() throws Exception {
    if (dataPath == null) {
      throw new NullPointerException("No data path set");
    }
    dataPath = FileUtils.mkdirs(dataPath);

    if (eventStore == null) {
      eventStore = new EventStore();
      eventStore.setPath(new File(dataPath, "queue/bdb"));
    }
    eventStore.open();

    outbox = FileUtils.mkdirs(new File(dataPath, "outbox"));

    inbox = FileUtils.mkdirs(new File(dataPath, "inbox"));

  }

  public void close() throws Exception {
    if (eventStore != null) {
      eventStore.close();
    }

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

  public void pollInbox() throws Exception {
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

    for (File file : files) {
      File seen = new File(file.getAbsolutePath() + ".seen");
      if (!seen.exists()) {
        try {
          System.out.println("Processing " + file.getAbsoluteFile());
          new FileOutputStream(seen, false).close();
          StreamingJsonEventLogReader events = new StreamingJsonEventLogReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
          Event event;
          while ((event = events.next()) != null) {
            queue(event);
          }
          System.out.println("Done processing " + file.getAbsoluteFile());
        } catch (Exception e) {
          // log.error("Exception while importing event log " + file.getAbsolutePath(), e);
          e.printStackTrace(System.out);
        }
      }
    }
  }

  private long eventsQueued;
  private int debugEventsQueued = Integer.MAX_VALUE -1;

  /**
   * Adds an event to the queue.
   * Next time you call upon {@link EventQueue#flushQueue()} it will be flushed to your database.
   * <p/>
   * Before queue is flushed it is possible
   * to update events by assigning them with the same identity
   *
   * @param event event to be added to the queue.
   * @return previous revision of the event with this identity in the store we've replaced with method parameter event.
   * @throws Exception
   */
  public synchronized Event queue(Event event) throws Exception {
    assertWellDescribedEvent(event);

    if (event.getIdentity() == null) {
      event.setIdentity(eventStore.identityFactory());

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

    // add to queue of events to be executed,
    // ie also old events that have been updated!
    event.set_local_timestamp(new Date());

    Event response = eventStore.getEvents().put(event);

    if (response != null) {
      // todo count updates
      Nop.breakpoint();
    }

    eventsQueued++;
    if (debugEventsQueued++ >= 1431) {
      debugEventsQueued=0;
      System.out.println(eventsQueued + " events queued (or updated) since system startup. Last event: " + event.toString());
    }

    return response;
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

  private void execute(Event event, JSONParser jsonParser) throws Exception {
    CommandManager.getInstance().commandFactory(event.getCommandName(), event.getCommandVersion()).execute(event, jsonParser);
  }


  // todo persistent
  private Date lastFlushQueueStartedTimestamp = new Date(0);

  private int debugFlushCounter = Integer.MAX_VALUE -1;
  private int totalFlushCounter = 0;
  private int failedFlushedCounter = 0;

  public synchronized void flushQueue() {

    Date started = new Date();
    Date lastFlushQueueStartedTimestamp = this.lastFlushQueueStartedTimestamp;

    JSONParser jsonParser = new JSONParser();
    EntityCursor<Event> events = eventStore.getEventsByTimestamp().entities(lastFlushQueueStartedTimestamp, true, started, false);
    try {
      Event event;
      while ((event = events.next()) != null) {
        try {
          execute(event, jsonParser);
          // if this was turned off then we would keep all events in the bdb
          // todo but that requires that the most recent seen event date is PERSISTENT
          // property? setting? todo a file in data/eventManager
          if (true) {
            if (!events.delete()) {
              // todo create event store lock?
              Nop.breakpoint();
              throw new Exception("Why was event " + event.toString() + " already deleted? This is supposed to be synchronized but really isn't, so don't go touching the event store when executing this method!!");
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          // log.error("Exception while executing event " + event, e);
          failedFlushedCounter++;

        }

        totalFlushCounter++;
        if (debugFlushCounter++ >= 1431) {
          debugFlushCounter = 0;
          System.out.println(totalFlushCounter + " events flushed to command since system startup. Last event: " + event);
        }

      }
    } finally {
      events.close();
    }

    this.lastFlushQueueStartedTimestamp = started;

    // log.info("Executed " + totalCounter + " events. " + failedCounter + " of them failed.");
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

  public Date getLastFlushQueueStartedTimestamp() {
    return lastFlushQueueStartedTimestamp;
  }

  public void setLastFlushQueueStartedTimestamp(Date lastFlushQueueStartedTimestamp) {
    this.lastFlushQueueStartedTimestamp = lastFlushQueueStartedTimestamp;
  }

  public EventStore getEventStore() {
    return eventStore;
  }

  public void setEventStore(EventStore eventStore) {
    this.eventStore = eventStore;
  }
}
