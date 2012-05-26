package se.datahamstern.external.kolada;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import se.datahamstern.Datahamstern;
import se.datahamstern.Nop;
import se.datahamstern.command.Source;
import se.datahamstern.event.Event;
import se.datahamstern.event.JsonEventLogWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * transforms json produced by the kolada hack api to datahamstern event log
 * <p/>
 * [
 * {
 * "nyckeltal" : {
 * "alias" : "N01817",
 * "localid" : "22816",
 * "title" : "Invånare 0 år, antal"
 * },
 * "år" : {
 * "alias" : "2009",
 * "localid" : "16886",
 * "title" : "2009"
 * },
 * "värde" : 323,0,
 * "kommun" : {
 * "alias" : "1440",
 * "localid" : "16680",
 * "title" : "Ale"
 * }
 * }, {
 *
 * @author kalle
 * @since 2012-05-23 13:34
 */
public class KoladaBefolkningsandelPerAlderJsonEventFactory {

  public static void main(String[] args) throws Exception {

    Datahamstern.getInstance().setSystemUUID("banarne.kodapan.se");

    Source source = new Source();
    source.setAuthor("kolada.se");
    source.setLicense("public domain");
    source.setTimestamp(new Date());
    source.setTrustworthiness(1f);

    JsonEventLogWriter eventLogWriter = new JsonEventLogWriter(new OutputStreamWriter(new FileOutputStream(new File("/tmp/" + System.currentTimeMillis() + ".kolada.befolkningsandel-per-alder.log.json")), "UTF8"));

    Pattern nyckeltalTitlePattern = Pattern.compile("Invånare ([0-9]+)((\\+)|( år)), antal");

    JSONParser parser = new JSONParser();

    File path = new File("/Users/kalle/Downloads/kolada/kolada/download/");
    for (File file : path.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().endsWith(".json");
      }
    })) {

      Reader input = new InputStreamReader(new FileInputStream(file), "UTF8");
      JSONArray array = (JSONArray) parser.parse(input);
      input.close();

      for (int i = 0; i < array.size(); i++) {
        JSONObject object = (JSONObject) array.get(i);
        String nyckeltal = (String) ((JSONObject) object.get("nyckeltal")).get("title");
        int år = Integer.valueOf((String) ((JSONObject) object.get("år")).get("title"));
        String kommunnummerkod = (String) ((JSONObject) object.get("kommun")).get("alias");
        int antal = Float.valueOf((String) object.get("värde")).intValue();

        Matcher matcher = nyckeltalTitlePattern.matcher(nyckeltal);
        if (!matcher.find()) {
          throw new RuntimeException();
        }

        int ålder = Integer.valueOf(matcher.group(1));
        // todo handle 100+

        Event event = new Event();
        event.setIdentity(UUID.randomUUID().toString());
        event.setCommandName(KoladaBefolkningsandelsPerAlderCommand.COMMAND_NAME);
        event.setCommandVersion(KoladaBefolkningsandelsPerAlderCommand.COMMAND_VERSION);
        event.setSources(new ArrayList<Source>(1));
        event.getSources().add(source);

        StringWriter json = new StringWriter();
        json.write("{");
        json.append("\"år\":").append(String.valueOf(år)).append(",");
        json.append("\"kommunnummerkod\":\"").append(kommunnummerkod).append("\",");
        json.append("\"ålder\":").append(String.valueOf(ålder)).append(",");
        json.append("\"antal\":").append(String.valueOf(antal));
        json.write("}");

        event.setJsonData(json.toString());

        eventLogWriter.consume(event);

        Nop.breakpoint();
      }

      Nop.breakpoint();
    }

    eventLogWriter.close();
  }

}
