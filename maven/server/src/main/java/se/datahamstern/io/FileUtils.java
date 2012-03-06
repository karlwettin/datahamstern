package se.datahamstern.io;

import java.io.File;
import java.io.IOException;

/**
 * @author kalle
 * @since 2012-03-06 01:18
 */
public class FileUtils {

  /**
   * @param path
   * @throws IOException if path not exists and could not be created.
   */
  public static File mkdirs(File path) throws IOException {
    path = getCleanAbsolutePath(path);
    // todo find parents and throw exception if the first existing is not a directory
    if (!path.exists()) {
      if (!path.mkdirs()) {
        throw new IOException("Could not mkdirs " + path.getAbsolutePath());
      }
//      log.info("Created directory " + path.getAbsolutePath());
    }
    return path;
  }

  /**
   * Makes "/tmp/a/b/c/./../.." into "/tmp/a/b/c"
   *
   * @param path
   * @return
   * @throws IOException
   */
  public static File getCleanAbsolutePath(File path) throws IOException {
    path = new File(path.getAbsolutePath());
    while (path.isDirectory()) {
      if (".".equals(path.getName())) {
        path = path.getParentFile();
      } else if ("..".equals(path.getName())) {
        path = path.getParentFile();
      } else {
        break;
      }
    }
    return path;
  }

}
