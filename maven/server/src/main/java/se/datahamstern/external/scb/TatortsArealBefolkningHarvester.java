package se.datahamstern.external.scb;

import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.JsonEventLogWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2012-05-31 15:10
 */
public class TatortsArealBefolkningHarvester {

  public static void main(String[] args) throws Exception {

    JsonEventLogWriter eventLog = new JsonEventLogWriter(new File("/tmp/" + System.currentTimeMillis() + "-scb-tatorts-befolkning-landareal-2005-2010.log.json"));

    Source source = new Source();
    source.setTimestamp(new Date());
    source.setLicense("public domain");
    source.setDetails("http://www.scb.se/Statistik/MI/MI0810/2010A01X/Tatorternami0810tab1_ny.xls");
    source.setAuthor("scb.se");
    source.setTrustworthiness(1f);


    BufferedReader csv = new BufferedReader(new InputStreamReader(new FileInputStream("server/src/main/java/se/datahamstern/external/scb/Tatorternami0810tab1_ny.csv"), "UTF8"));
    String row;
    while ((row = csv.readLine()) != null) {
      String[] columns = row.split("\t");

      String tätortskod = columns[0].replaceAll("\\s", "");
      String landarealHektar05 = columns[5].replaceAll(",", ".").replaceAll("\\s", "");
      String folkmängd05 = columns[6].replaceAll("\\s", "");
      String landarealHektar10 = columns[9].replaceAll(",", ".").replaceAll("\\s", "");
      String folkmängd10 = columns[10].replaceAll("\\s", "");

      Event event;

      // 2005

      event = new Event();
      event.setIdentity(UUID.randomUUID().toString());
      event.setCommandName(TatortsArealBefolkningCommand.COMMAND_NAME);
      event.setCommandVersion(TatortsArealBefolkningCommand.COMMAND_VERSION);
      event.setSources(new ArrayList<Source>());
      event.getSources().add(source);

      StringBuilder jsonData;

      jsonData = new StringBuilder();
      jsonData.append("{");
      jsonData.append("\"tätortskod\":\"").append(tätortskod).append("\", ");
      jsonData.append("\"år\":2005, ");
      jsonData.append("\"befolkning\":").append(folkmängd05).append(", ");
      jsonData.append("\"hektarLandareal\":").append(landarealHektar05);
      jsonData.append("}");
      event.setJsonData(jsonData.toString());
      eventLog.consume(event);

      // 2010

      event = new Event();
      event.setIdentity(UUID.randomUUID().toString());
      event.setCommandName(TatortsArealBefolkningCommand.COMMAND_NAME);
      event.setCommandVersion(TatortsArealBefolkningCommand.COMMAND_VERSION);
      event.setSources(new ArrayList<Source>());
      event.getSources().add(source);

      jsonData = new StringBuilder();
      jsonData.append("{");
      jsonData.append("\"tätortskod\":\"").append(tätortskod).append("\", ");
      jsonData.append("\"år\":2010, ");
      jsonData.append("\"befolkning\":").append(folkmängd10).append(", ");
      jsonData.append("\"hektarLandareal\":").append(landarealHektar10);
      jsonData.append("}");
      event.setJsonData(jsonData.toString());
      eventLog.consume(event);

      Nop.breakpoint();
    }

    csv.close();

    eventLog.close();

  }

}
