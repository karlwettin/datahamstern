package se.datahamstern.io;


import se.datahamstern.Datahamstern;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2011-02-01 18.46
 */
public class SeleniumConnectionParameters implements Serializable {

  private static final long serialVersionUID = 1l;

  public static final String defaultServerHost = "localhost";
  public static final int defaultServerPort = 4444;
  public static final String defaultBrowserStartCommand = "*firefox";

  public static final String defaultBrowserURL = "file:///";


  private String serverHost;
  private int serverPort;
  private String browserStartCommand;
  private String browserURL;

  public SeleniumConnectionParameters() throws Exception {
    this(
        Datahamstern.getInstance().getProperty("selenium.host", defaultServerHost),
        Integer.valueOf(Datahamstern.getInstance().getProperty("selenium.port", String.valueOf(defaultServerPort))),
        Datahamstern.getInstance().getProperty("selenium.browser", defaultBrowserStartCommand),
        Datahamstern.getInstance().getProperty("selenium.url", defaultBrowserURL)
    );
  }

  public SeleniumConnectionParameters(String browserStartCommand) throws Exception {
    this(
        Datahamstern.getInstance().getProperty("selenium.host", defaultServerHost),
        Integer.valueOf(Datahamstern.getInstance().getProperty("selenium.port", String.valueOf(defaultServerPort))),
        browserStartCommand,
        Datahamstern.getInstance().getProperty("selenium.url", defaultBrowserURL)
    );
  }

  public SeleniumConnectionParameters(String serverHost, int serverPort, String browserStartCommand, String browserURL) {
    this.serverHost = serverHost;
    this.serverPort = serverPort;
    this.browserStartCommand = browserStartCommand;
    this.browserURL = browserURL;
  }

  public String getServerHost() {
    return serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public String getBrowserStartCommand() {
    return browserStartCommand;
  }

  public void setBrowserStartCommand(String browserStartCommand) {
    this.browserStartCommand = browserStartCommand;
  }

  public String getBrowserURL() {
    return browserURL;
  }

  public void setBrowserURL(String browserURL) {
    this.browserURL = browserURL;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SeleniumConnectionParameters that = (SeleniumConnectionParameters) o;

    if (serverPort != that.serverPort) return false;
    if (browserStartCommand != null ? !browserStartCommand.equals(that.browserStartCommand) : that.browserStartCommand != null) return false;
    if (browserURL != null ? !browserURL.equals(that.browserURL) : that.browserURL != null) return false;
    if (serverHost != null ? !serverHost.equals(that.serverHost) : that.serverHost != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = serverHost != null ? serverHost.hashCode() : 0;
    result = 31 * result + serverPort;
    result = 31 * result + (browserStartCommand != null ? browserStartCommand.hashCode() : 0);
    result = 31 * result + (browserURL != null ? browserURL.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SelectionConnectionParameters{" +
        "serverHost='" + serverHost + '\'' +
        ", serverPort=" + serverPort +
        ", browser='" + browserStartCommand + '\'' +
        ", browserURL='" + browserURL + '\'' +
        '}';
  }
}


