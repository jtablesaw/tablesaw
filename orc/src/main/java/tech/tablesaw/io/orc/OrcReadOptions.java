package tech.tablesaw.io.orc;

import org.apache.orc.OrcFile;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OrcReadOptions extends ReadOptions {

  protected OrcFile.ReaderOptions readOptions;

  protected OrcReadOptions(Builder builder) {
    super(builder);
    this.readOptions = builder.readOptions;
  }

  public static Builder builder(Source source) {
    return new Builder(source);
  }

  public static Builder builder(File file) {
    return new Builder(file).tableName(file.getName());
  }

  public static Builder builder(String fileName) {
    return new Builder(new File(fileName));
  }

  public static Builder builderFromFile(String fileName) {
    return new Builder(new File(fileName));
  }

  public OrcFile.ReaderOptions orcReaderOptions() {
    return readOptions;
  }

  public static class Builder extends ReadOptions.Builder {

    protected OrcFile.ReaderOptions readOptions;

    protected Builder(Source source) {
      super(source);
    }

    protected Builder(URL url) throws IOException {
      super(url);
    }

    public Builder(File file) {
      super(file);
    }

    public Builder(InputStream stream) {
      super(stream);
    }

    public Builder(Reader reader) {
      super(reader);
    }

    @Override
    public OrcReadOptions build() {
      return new OrcReadOptions(this);
    }

    // Override super-class setters to return an instance of this class

    @Override
    public Builder header(boolean header) {
      super.header(header);
      return this;
    }

    @Override
    public Builder tableName(String tableName) {
      super.tableName(tableName);
      return this;
    }

    @Override
    public Builder sample(boolean sample) {
      super.sample(sample);
      return this;
    }

    @Override
    public Builder dateFormat(DateTimeFormatter dateFormat) {
      super.dateFormat(dateFormat);
      return this;
    }

    @Override
    public Builder timeFormat(DateTimeFormatter timeFormat) {
      super.timeFormat(timeFormat);
      return this;
    }

    @Override
    public Builder dateTimeFormat(DateTimeFormatter dateTimeFormat) {
      super.dateTimeFormat(dateTimeFormat);
      return this;
    }

    @Override
    public Builder locale(Locale locale) {
      super.locale(locale);
      return this;
    }

    @Override
    public Builder missingValueIndicator(String missingValueIndicator) {
      super.missingValueIndicator(missingValueIndicator);
      return this;
    }

    @Override
    public Builder minimizeColumnSizes() {
      super.minimizeColumnSizes();
      return this;
    }

    public Builder ocrReadOptions(OrcFile.ReaderOptions readOptions) {
      this.readOptions = readOptions;
      return this;
    }
  }
}
