package tech.tablesaw.io.arrow;

import java.io.*;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

public class ArrowWriteOptions extends WriteOptions {

  protected ArrowWriteOptions(Builder builder) {
    super(builder);
  }

  public static class Builder extends WriteOptions.Builder {

    protected Builder(Destination dest) {
      super(dest);
    }

    protected Builder(OutputStream dest) {
      super(dest);
    }

    protected Builder(Writer dest) {
      super(dest);
    }

    protected Builder(File dest) throws FileNotFoundException {
      super(new FileOutputStream(dest));
    }
  }
}
