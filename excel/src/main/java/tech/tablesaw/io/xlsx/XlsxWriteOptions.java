package tech.tablesaw.io.xlsx;

import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

public class XlsxWriteOptions extends WriteOptions {

  protected XlsxWriteOptions(Builder builder) {
    super(builder);
  }

  public static Builder builder(Destination dest) {
    return new Builder(dest);
  }

  public static Builder builder(OutputStream dest) {
    return new Builder(dest);
  }

  public static Builder builder(Writer dest) {
    return new Builder(dest);
  }

  public static Builder builder(File dest) {
    return new Builder(dest);
  }

  public static Builder builder(String fileName) {
    return builder(new File(fileName));
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

    protected Builder(File dest) {
      super(dest);
    }

    public XlsxWriteOptions build() {
      return new XlsxWriteOptions(this);
    }
  }
}
