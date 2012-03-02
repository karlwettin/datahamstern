package se.datahamstern.services.naringslivsregistret;

import se.datahamstern.Datahamstern;
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
      new HarvestNaringslivsregistret().harvest("5562990000", "5600000000");
    } finally {
      Datahamstern.getInstance().open();
    }
  }

  public HarvestNaringslivsregistret() throws Exception {
  }

  private int[] organizationNumber = new int[]{5, 5, 6, 0, 0, 0, 0, 0, 0, 0};
  private int[] end = new int[]{5, 6, 0, 0, 0, 0, 0, 0, 0, 0};
  private char[] chars = new char[10];


  private FileOutputStream fos;
  private ObjectOutputStream oos;

  private FileOutputStream fosFailures;
  private ObjectOutputStream oosFailures;

  private boolean abort = false;
  private AtomicInteger withResults = new AtomicInteger(0);


  public void harvest(String start, String end) throws Exception {
    setOrganizationNumber(start);
    setEnd(end);

    long started = System.currentTimeMillis();

    fos = new FileOutputStream("HarvestNaringslivsregistret.raw." + start + "." + started);
    oos = new ObjectOutputStream(fos);

    fosFailures = new FileOutputStream("HarvestNaringslivsregistret.failed.raw." + start + "." + started);
    oosFailures = new ObjectOutputStream(fosFailures);

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
                        writeFailurePosting(organizationNumber);
                      } catch (Exception e2) {
                        abort = true;
                        throw new RuntimeException(e2);
                      }
                      break;
                    }
                  }
                }
                if (results == null) {
                  continue;
                }
                if (!results.isEmpty()) {
                  withResults.incrementAndGet();

                  for (NaringslivsregistretResult result : results) {
                    try {
                      writeOrganizationPosting(result);
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


    oos.writeBoolean(false);
    oos.close();
    fos.close();

    oosFailures.writeBoolean(false);
    oosFailures.close();
    fosFailures.close();

  }

  private synchronized void writeOrganizationPosting(NaringslivsregistretResult result) throws IOException {

    oos.writeBoolean(true);
    oos.writeObject(new Date());
    oos.writeObject(result.getOrganizationNumberPrefix());
    oos.writeObject(result.getOrganizationNumber());
    oos.writeObject(result.getOrganizationNumberSuffix());
    oos.writeObject(result.getNumericLänskod());
    oos.writeObject(result.getType());
    oos.writeObject(result.getName());
    oos.writeObject(result.getStatus());
    oos.flush();

  }


  private synchronized void writeFailurePosting(String organizationNumber) throws IOException {
    oosFailures.writeBoolean(true);
    oosFailures.writeObject(organizationNumber);
    oosFailures.flush();
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
        Datahamstern.getInstance().glue.put("lastOrganizationNumber", string);
        return string;
      }

    }


    return null;
  }

  public int found() {
    return withResults.get();
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

}
