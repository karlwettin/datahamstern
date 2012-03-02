package se.datahamstern.io;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import se.datahamstern.xml.DomUtils;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2010-nov-17 23:15:23
 */
public class SeleniumAccessor extends DefaultSelenium {

  public static void main(String[] args) throws Exception {
    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {
      System.currentTimeMillis();
    } finally {
      selenium.stop();
    }
  }

  public static Set<SeleniumAccessor> started = new HashSet<SeleniumAccessor>();

//  protected Logger log = LoggerFactory.getLogger(getClass());

  protected String fast = "100";
  protected String slow = "5000";
  protected String verySlow = "20000";
  private Set<Integer> ignoredResponseCodes = new HashSet<Integer>(Arrays.asList(new Integer[]{403}));
  public XPath xpath = XPathFactory.newInstance().newXPath();
  private DOMParser parser = new DOMParser();
  private boolean parsed = false;

  private String htmlDOM;

  private SeleniumConnectionParameters connectionParameters;

  public SeleniumAccessor(SeleniumConnectionParameters connectionParameters) {
    super(connectionParameters.getServerHost(), connectionParameters.getServerPort(), connectionParameters.getBrowserStartCommand(), connectionParameters.getBrowserURL());
    this.connectionParameters = connectionParameters;
  }

  public SeleniumAccessor() throws Exception {
    this(new SeleniumConnectionParameters());
  }

  @Override
  public void start() {
    super.start();
    started.add(this);
  }

  @Override
  public void start(String optionsString) {
    super.start(optionsString);
    started.add(this);
  }

  @Override
  public void start(Object optionsObject) {
    super.start(optionsObject);
    started.add(this);
  }

  @Override
  public void stop() {
    super.stop();
    started.remove(this);
  }

  @Override
  public void close() {
    super.close();
  }

  @Deprecated
  public boolean isIgnoreForbidden() {
    return ignoredResponseCodes.contains(403);
  }

  @Deprecated
  public void setIgnoreForbidden(boolean ignoreForbidden) {
    if (ignoreForbidden) {
      ignoredResponseCodes.add(403);
    } else {
      ignoredResponseCodes.remove(403);
    }
  }

  private int defaultRetries = 1;


  public void openAndWaitForPageToLoad(URI uri) {
    openAndWaitForPageToLoad(uri.toString());
  }

  public void openAndWaitForPageToLoad(URI uri, String timeout) {
    openAndWaitForPageToLoad(uri.toString(), timeout);
  }


  public void openAndWaitForPageToLoad(String locator) {
    openAndWaitForPageToLoad(locator, verySlow);
  }

  public void openAndWaitForPageToLoad(String locator, String timeout) {
    openAndWaitForPageToLoad(locator, timeout, defaultRetries);
  }

  public void openAndWaitForPageToLoad(String locator, String timeout, int retries) {
    int failures = 0;
    while (true) {
      try {
        open(locator);
        waitForPageToLoad(timeout);
        return;
      } catch (SeleniumException se) {
        if (!se.getMessage().startsWith("Timed out after ")) {
          throw se;
        } else if (++failures > retries) {
          throw new RuntimeException("Timed out after " + failures + " failed attempts each with timeout set to " + timeout, se);
        }

//        if (log.isInfoEnabled()) {
//          log.info("Retrying(" + failures + "/" + retries + ") due to " + se.getMessage());
//        }
      }
    }
  }

  public void selectAndWaitForPageToLoad(String locator, String value) {
    selectAndWaitForPageToLoad(locator, value, verySlow);
  }

  public void selectAndWaitForPageToLoad(String locator, String value, String timeout) {
    selectAndWaitForPageToLoad(locator, value, timeout, defaultRetries);
  }

  public void selectAndWaitForPageToLoad(String locator, String value, String timeout, int retries) {
    int failures = 0;
    while (true) {
      select(locator, value);
      try {
        waitForPageToLoad(timeout);
        return;
      } catch (SeleniumException se) {
        if (!("Timed out after " + timeout + "ms").equals(se.getMessage())) {
          throw se;
        } else if (++failures > retries) {
          throw new RuntimeException("Timed out after " + failures + " failed attempts each with timeout set to " + timeout, se);
        }

//        if (log.isInfoEnabled()) {
//          log.info("Retrying(" + failures + "/" + retries + ") due to " + se.getMessage());
//        }
      }
    }
  }

  public void clickAndWaitForPageToLoad(String locator) {
    clickAndWaitForPageToLoad(locator, verySlow);
  }

  public void clickAndWaitForPageToLoad(String locator, String timeout) {
    clickAndWaitForPageToLoad(locator, timeout, defaultRetries);
  }

  public void clickAndWaitForPageToLoad(String locator, String timeout, int retries) {
    int failures = 0;
    while (true) {
      click(locator);
      try {
        waitForPageToLoad(timeout);
        return;
      } catch (SeleniumException se) {
        if (!("Timed out after " + timeout + "ms").equals(se.getMessage())) {
          throw se;
        } else if (++failures > retries) {
          throw new RuntimeException("Timed out after " + failures + " failed attempts each with timeout set to " + timeout, se);

        }



//        if (log.isInfoEnabled()) {
//          log.info("Retrying(" + failures + "/" + retries + ") due to " + se.getMessage());
//        }
      }
    }
  }

  @Override
  public void click(String locator) {
    try {
      super.click(locator);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;
  }

  @Override
  public void doubleClick(String locator) {
    try {
      super.doubleClick(locator);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void clickAt(String locator, String coordString) {
    try {
      super.clickAt(locator, coordString);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void doubleClickAt(String locator, String coordString) {
    try {
      super.doubleClickAt(locator, coordString);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void fireEvent(String locator, String eventName) {
    try {
      super.fireEvent(locator, eventName);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void check(String locator) {
    try {
      super.check(locator);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void uncheck(String locator) {
    try {
      super.uncheck(locator);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void select(String selectLocator, String optionLocator) {
    try {
      super.select(selectLocator, optionLocator);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void submit(String formLocator) {
    try {
      super.submit(formLocator);
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }
    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void open(String url) {
    open(url, 3);
  }

  public void open(String url, int retries) {

    long started = System.currentTimeMillis();
    int failures = 0;
    while (true) {
      try {
        super.open(url);
        break;
      } catch (SeleniumException se) {
        if (!se.getMessage().startsWith("Timed out after ")) {
          throw se;
        } else if (++failures > retries) {
          long spent = System.currentTimeMillis()  - started;
          throw new RuntimeException("Timed out after " + failures + " failed attempts and " + spent+ " milliseconds", se);
        }

//        if (log.isInfoEnabled()) {
//          log.info("Retrying(" + failures + "/" + retries + ") due to " + se.getMessage());
//        }
      }
    }


    parsed = false;
    htmlDOM = null;

  }

  private static Pattern pattern = Pattern.compile("Response_Code = ([0-9]+) ");
  private void ignoreDefaultResponseCodes(SeleniumException se) {

    Matcher matcher = pattern.matcher(se.getMessage());
    if (!matcher.find() || !ignoredResponseCodes.contains(Integer.valueOf(matcher.group(1)))) {
      throw se;
    }

  }


  public void ignoreResponseCodes(SeleniumException se, Integer... responseCodes) {
    Set<Integer> ignoredResponseCodes = new HashSet<Integer>(Arrays.asList(responseCodes));
    Matcher matcher = pattern.matcher(se.getMessage());
    if (!matcher.matches() || ignoredResponseCodes.contains(Integer.valueOf(matcher.group(1)))) {
      throw se;
    }
  }



  public Set<Integer> getIgnoredResponseCodes() {
    return ignoredResponseCodes;
  }

  public void setIgnoredResponseCodes(Set<Integer> ignoredResponseCodes) {
    this.ignoredResponseCodes = ignoredResponseCodes;
  }


  @Override
  public void goBack() {
    try {
      super.goBack();
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  @Override
  public void refresh() {
    try {
      super.refresh();
    } catch (SeleniumException e) {
      ignoreDefaultResponseCodes(e);
    }

    parsed = false;
    htmlDOM = null;

  }

  /**
   * @return HTML from the DOM rather than just the source.
   */
  public String getHtmlDOM() {
    if (htmlDOM == null) {
      htmlDOM = "<html>" + getEval("window.document.documentElement.innerHTML") + "</html>";
    }
    return htmlDOM;
  }

  public Document refreshDOM() throws Exception {
    htmlDOM = null;
    parsed = false;
    return getDOM();
  }


  public final Document getDOM() throws Exception {
    if (!parsed) {
      long ms = System.currentTimeMillis();
      parser.parse(new InputSource(new StringReader(getHtmlDOM())));
//      if (log.isDebugEnabled()) {
//        ms = System.currentTimeMillis() - ms;
//        log.debug("Took " + ms + " milliseconds to gather HTML and parse DOM");
//      }

      parsed = true;
    }
    return parser.getDocument();
  }

  public List<Option> getSelectOptionValues(String selectExpression) throws Exception {
    NodeList nodes = (NodeList) xpath.compile(selectExpression + "/OPTION").evaluate(getDOM(), XPathConstants.NODESET);
    if (nodes == null || nodes.getLength() == 0) {
      return null;
    } else {
      List<Option> list = new ArrayList<Option>();
      for (int i = 0; i < nodes.getLength(); i++) {
        Node node = nodes.item(i);
        list.add(new Option(node.getTextContent(), DomUtils.getAttributeValue(node, "value", false)));
      }
      return list;
    }
  }

  public static class Option {
    private String text;
    private String value;

    public Option(String text, String value) {
      this.text = text;
      this.value = value;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "Option{" +
          "value='" + value + '\'' +
          ", text='" + text + '\'' +
          '}';
    }
  }

  public XPath getXpath() {
    return xpath;
  }

  public String getFast() {
    return fast;
  }

  public void setFast(String fast) {
    this.fast = fast;
  }

  public String getSlow() {
    return slow;
  }

  public void setSlow(String slow) {
    this.slow = slow;
  }

  public String getVerySlow() {
    return verySlow;
  }

  public void setVerySlow(String verySlow) {
    this.verySlow = verySlow;
  }
}
