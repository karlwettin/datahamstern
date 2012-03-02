package se.datahamstern.io;

/**
 * @author kalle
 * @since 2012-03-02 01:38
 */
public class SourceChangedException extends Exception {

  public SourceChangedException() {
  }

  public SourceChangedException(String message) {
    super(message);
  }

  public SourceChangedException(String message, Throwable cause) {
    super(message, cause);
  }

  public SourceChangedException(Throwable cause) {
    super(cause);
  }
}
