package se.datahamstern.domain;

import com.sleepycat.persist.EntityCursor;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Enumeration;

/**
 * closes all session bound cursors in domain store as sessions expire
 *
 * @author kalle
 * @since 2012-04-09 20:32
 */
public class DomainStoreSessionListener implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent httpSessionEvent) {

  }

  @Override
  public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    for (Enumeration<String> enumeration = httpSessionEvent.getSession().getAttributeNames(); enumeration.hasMoreElements();) {
      String key = enumeration.nextElement();
      if (key.startsWith("cursor.")) {
        EntityCursor cursor = (EntityCursor)httpSessionEvent.getSession().getAttribute(key);
        try {
          cursor.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
}
