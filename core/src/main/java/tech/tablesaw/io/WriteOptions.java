package tech.tablesaw.io;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

public class WriteOptions {

  protected final Destination dest;

  /**
   * This value is not exposed as an actual option. It will determine whether the output stream
   * automatically closes after the table has been output. The default value is false, and it just
   * set true for File output.
   */
  protected final boolean autoClose;

  protected WriteOptions(Builder builder) {
    this.dest = builder.dest;
    this.autoClose = builder.autoClose;
  }

  public Destination destination() {
    return dest;
  }

  public static class Builder {

    protected Destination dest;
    protected boolean autoClose = false;

    protected Builder(Destination dest) {
      this.dest = dest;
    }

    protected Builder(OutputStream dest) {
      this.dest = new Destination(dest);
    }

    protected Builder(Writer dest) {
      this.dest = new Destination(dest);
    }

    protected Builder(File dest) {
      this.dest = new Destination(dest);
      this.autoClose = true;
    }
  }
}
