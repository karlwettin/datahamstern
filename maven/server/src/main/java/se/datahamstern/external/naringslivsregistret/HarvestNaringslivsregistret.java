package se.datahamstern.external.naringslivsregistret;

import se.datahamstern.Datahamstern;
import se.datahamstern.Glue;
import se.datahamstern.util.Mod10;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * not thread safe!
 *
 * @author kalle
 * @since 2012-03-02 01:42
 */
public class HarvestNaringslivsregistret {

//  private static final Logger log = LoggerFactory.getLogger(HarvestNaringslivsregistret.class);

  public static void main(String[] args) throws Exception {
    Datahamstern.getInstance().open();
    try {
//      new HarvestNaringslivsregistret().harvest("5562990000", "5600000000");
      new HarvestNaringslivsregistret().harvest("5562999622", "5600000000", new RawAuditLogVisitor());
    } finally {
      Datahamstern.getInstance().open();
    }
  }

  public HarvestNaringslivsregistret() throws Exception {
  }

  private int[] organizationNumber = new int[]{5, 5, 6, 0, 0, 0, 0, 0, 0, 0};
  private int[] end = new int[]{5, 6, 0, 0, 0, 0, 0, 0, 0, 0};
  private char[] chars = new char[10];


  private boolean abort = false;


  public void harvest(String start, String end, final HarvestNaringslivsregistretVisitor visitor) throws Exception {

    setOrganizationNumber(start);
    setEnd(end);

    visitor.start(this);

    long started = System.currentTimeMillis();


    List<Thread> threads = new ArrayList<Thread>();
    for (int i = 0; i < Datahamstern.getInstance().getProperty("HarvestNaringslivsregistret.threads", 1); i++) {
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          Naringslivsregistret nlr = new Naringslivsregistret();
          try {
            nlr.open();
            try {

              String organizationNumber;
              while (!abort && (organizationNumber = pollOrganizationNumber()) != null) {

                List<NaringslivsregistretResult> results = null;
                int tries = 0;
                while (true) {
                  try {
                    tries++;
                    results = nlr.search(organizationNumber);
                    break;
                  } catch (Exception e) {
                    e.printStackTrace();
                    if (tries > 3) {
                      try {
                        visitor.failed(HarvestNaringslivsregistret.this, organizationNumber, e);
                      } catch (Exception e2) {
                        abort = true;
                        throw new RuntimeException(e2);
                      }
                      break;
                    }
                  }
                }
                if (results == null) {
                  visitor.missing(HarvestNaringslivsregistret.this, organizationNumber);
                  continue;
                }
                if (!results.isEmpty()) {

                  for (NaringslivsregistretResult result : results) {
                    try {
                      visitor.found(HarvestNaringslivsregistret.this, result);
                    } catch (Exception e) {
                      abort = true;
                      throw new RuntimeException(e);
                    }

                  }
                }
              }

            } finally {
              nlr.close();
            }
          } catch (Exception e) {
            abort = true;
            throw new RuntimeException(e);
          }


        }
      });
      threads.add(thread);
      thread.setDaemon(true);
      thread.setName("Näringslivsregistret harvest()#" + i);
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }

    visitor.end(this);


  }


  private synchronized String pollOrganizationNumber() {

    while (!Arrays.equals(organizationNumber, end)) {

      for (int i = 9; i >= 0; i--) {
        if (organizationNumber[i] != 9) {
          organizationNumber[i]++;
          break;
        } else {
          organizationNumber[i] = 0;
        }
      }

      for (int i = 0; i < 10; i++) {
        chars[i] = (char) (48 + organizationNumber[i]);
      }

      String string = new String(chars);
      if (Mod10.isValidSwedishOrganizationNumber(string)) {
        return string;
      }

    }


    return null;
  }

  public int[] getOrganizationNumber() {
    return organizationNumber;
  }

  public void setOrganizationNumber(String organizationNumber) {
    for (int i = 0; i < 10; i++) {
      this.organizationNumber[i] = organizationNumber.charAt(i) - 48;
    }

  }

  public void setOrganizationNumber(int[] organizationNumber) {
    this.organizationNumber = organizationNumber;
  }

  public int[] getEnd() {
    return end;
  }

  public void setEnd(int[] end) {
    this.end = end;
  }

  public void setEnd(String organizationNumber) {
    for (int i = 0; i < 10; i++) {
      this.end[i] = organizationNumber.charAt(i) - 48;
    }

  }

  public static String toString(int[] ints) {
    char[] chars = new char[ints.length];
    for (int i = 0; i < ints.length; i++) {
      chars[i] = (char) (48 + ints[i]);
    }
    return new String(chars);
  }


}
