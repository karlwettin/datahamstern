package se.datahamstern.command;

/**
 * @author kalle
 * @since 2012-03-03 23:35
 */
public class UnsupportedCommandException extends Exception {

  public UnsupportedCommandException() {
  }

  public UnsupportedCommandException(String message) {
    super(message);
  }

  public UnsupportedCommandException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsupportedCommandException(Throwable cause) {
    super(cause);
  }
}
