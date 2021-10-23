package tech.tablesaw.io;

import java.io.IOException;

/**
 * An Runtime exception that wraps IOException so that client code need not catch IOException in
 * nearly every use of Tablesaw
 */
public class RuntimeIOException extends RuntimeException {
  /**
   * Constructs a new Runtime exception from the given checked exception
   *
   * @param cause An IO Exception
   */
  public RuntimeIOException(IOException cause) {
    super(cause);
  }
}
