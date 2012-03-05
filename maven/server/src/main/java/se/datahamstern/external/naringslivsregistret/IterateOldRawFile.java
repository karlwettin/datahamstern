package se.datahamstern.external.naringslivsregistret;

import se.datahamstern.Datahamstern;
import se.datahamstern.event.Event;
import se.datahamstern.event.EventManager;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;

import java.io.*;
import java.util.Date;

/**
 * laddar in data från de råfiler jag först skapade för att skörda näringslivsregistret
 * <p/>
 * de heter något i formen av
 * <p/>
 * HarvestNaringslivsregistret.raw.ORGANISATIONSNUMMER FRÅN.TIDSSLAG DEN BÖRJADE
 *
 * @author kalle
 * @since 2012-03-04 20:13
 */
public class IterateOldRawFile {

  public static void main(String[] args) throws Exception {

    Datahamstern.getInstance().open();
    try {


      IterateOldRawFile iterator = new IterateOldRawFile();
      NaringslivsregistretResult result = new NaringslivsregistretResult();


      Source source = NaringslivsregistretCommand.defaultSourceFactory(null);

      File path = new File(Datahamstern.getInstance().getDataPath(), "services/bolagsverket/naringslivsregistret");
      if (!path.exists() && !path.mkdirs()) {
        throw new IOException("Could not mkdirs " + path.getAbsolutePath());
      }

      for (File file : path.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith("HarvestNaringslivsregistret.raw.");
        }
      })) {

        source.setDetails(file.getName());

        FileInputStream fis = new FileInputStream(file);
        iterator.open(fis);
        Posting posting;
        while ((posting = iterator.next()) != null) {

          result.setLänsnummer(posting.getLänsnummer());
          result.setNamn(posting.getNamn());
          result.setNummerprefix(posting.getNummerprefix());
          result.setNummer(posting.getNummer());
          result.setNummersuffix(posting.getNummersuffix());
          result.setFirmaform(posting.getTyp());
          result.setStatus(posting.getStatus());

          Event event = NaringslivsregistretCommand.eventFactory(result, source);
          EventManager.getInstance().queue(event);
          Nop.breakpoint();

        }
        iterator.close();
        fis.close();
      }

      EventManager.getInstance().flushQueue();

    } finally {
      Datahamstern.getInstance().close();
    }

  }


  private ObjectInputStream ois;

  public void open(InputStream inputStream) throws Exception {
    ois = new ObjectInputStream(inputStream);
  }

  public void close() throws Exception {
    ois.close();
  }

  public static class Posting {

    private Date timestamp;
    // lets not use a NaringslivsregistretResult in case that change...
    private String nummerprefix;
    private String nummer;
    private String nummersuffix;
    private String länsnummer;
    private String typ;
    private String namn;
    private String status;

    public Date getTimestamp() {
      return timestamp;
    }

    public String getNummerprefix() {
      return nummerprefix;
    }

    public String getNummer() {
      return nummer;
    }

    public String getNummersuffix() {
      return nummersuffix;
    }

    public String getLänsnummer() {
      return länsnummer;
    }

    public String getTyp() {
      return typ;
    }

    public String getNamn() {
      return namn;
    }

    public String getStatus() {
      return status;
    }
  }

  private Posting posting = new Posting();

  public Posting next() throws Exception {

    if (ois.readBoolean()) {
      try {

        posting.timestamp = (Date) ois.readObject();
        posting.nummerprefix = (String) ois.readObject();
        posting.nummer = (String) ois.readObject();
        posting.nummersuffix = (String) ois.readObject();
        posting.länsnummer = (String) ois.readObject();
        posting.typ = (String) ois.readObject();
        posting.namn = (String) ois.readObject();
        posting.status = (String) ois.readObject();

      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }

      return posting;
    } else {
      return null;
    }
  }

}
