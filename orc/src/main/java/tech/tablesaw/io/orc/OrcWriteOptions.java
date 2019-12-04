package tech.tablesaw.io.orc;

import org.apache.orc.OrcFile;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

import java.io.IOException;

public class OrcWriteOptions extends WriteOptions {

  private OrcFile.WriterOptions writerOptions;

  protected OrcWriteOptions(Builder builder) {
    super(builder);
    writerOptions = builder.writerOptions;
  }

  public OrcFile.WriterOptions getWriterOptions() {
    return writerOptions;
  }

  public static Builder builder(String stringPath) throws IOException {
    return new Builder(stringPath);
  }

  public static Builder builder(Destination destination) {
    return new Builder(destination);
  }

  static class Builder extends WriteOptions.Builder {
    private OrcFile.WriterOptions writerOptions;

    protected Builder(Destination dest) {
      super(dest);
    }

    public Builder(String stringPath) throws IOException {
      super(stringPath);
    }

    public Builder ocrWriteOptions(OrcFile.WriterOptions writerOptions) {
      this.writerOptions = writerOptions;
      return this;
    }

    public OrcWriteOptions build() {
      return new OrcWriteOptions(this);
    }
  }
}
