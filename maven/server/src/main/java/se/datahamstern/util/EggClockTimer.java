package se.datahamstern.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kalle
 * @since 2012-04-02 22:25
 */
public class EggClockTimer {

  private long millisecondsBetweenTrigger = TimeUnit.SECONDS.toMillis(10);

  private boolean stopped = false;

  private Thread thread;

  public EggClockTimer(long millisecondsBetweenTrigger) {
    this.millisecondsBetweenTrigger = millisecondsBetweenTrigger;
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        started();
        while (!stopped) {
          try {
            Thread.sleep(EggClockTimer.this.millisecondsBetweenTrigger);
          } catch (InterruptedException e) {
            e.printStackTrace();
            break;
          }
          try {
            alarm();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        stopped();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  public void stop() throws InterruptedException {
    stopped = true;
    thread.join();
  }

  public void stopped() {
    alarm();
  }

  public void started() {
    alarm();
  }

  public void alarm() {

  }
}
