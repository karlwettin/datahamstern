package se.datahamstern;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;

/**
 * @author kalle
 * @since 2012-03-06 03:36
 */
public abstract class DatahamsternTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    Datahamstern.getInstance().setDataPath(new File("/tmp/datahamstern/test/data/" + System.currentTimeMillis()));
    Datahamstern.getInstance().open();

  }

  @Override
  protected void tearDown() throws Exception {

    Datahamstern.getInstance().close();

  }
}
