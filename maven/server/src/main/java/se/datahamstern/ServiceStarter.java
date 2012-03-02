package se.datahamstern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author kalle
 * @since 2012-03-02 04:11
 */
public class ServiceStarter implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      Datahamstern.getInstance().open();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    try {
      Datahamstern.getInstance().close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

