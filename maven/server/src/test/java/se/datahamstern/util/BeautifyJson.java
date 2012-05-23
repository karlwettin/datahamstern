package se.datahamstern.util;

import org.json.simple.parser.JSONFormatter;

import java.io.*;

/**
 * @author kalle
 * @since 2012-05-22 15:30
 */
public class BeautifyJson {

  public static void main(String[] args) throws Exception {

    File file = new File(args[0]);
    if (file.isDirectory()) {
      for (File f : file.listFiles(new FileFilter() {
        @Override
        public boolean accept(File file) {
          return file.isFile() && file.getName().toLowerCase().endsWith(".json");
        }
      })) {
        beautify(f);
      }
    } else {
      beautify(file);
    }

  }

  public static void beautify(File file) throws Exception {

    File backupFile = new File(file.getAbsolutePath() + ".bak");
    File outputFile = new File(file.getAbsolutePath());
    file.renameTo(backupFile);

    InputStreamReader input = new InputStreamReader(new FileInputStream(backupFile), "UTF8");
    OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8");

    new JSONFormatter().format(input, output);

    input.close();
    output.close();

  }

}
