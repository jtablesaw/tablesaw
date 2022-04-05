package tech.tablesaw.io.xlsx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.Source;

public class XlsxReadOptions extends ReadOptions {

  /** The sheet to read. Null means no specific index was set. First sheet has index 0. */
  protected Integer sheetIndex;

  protected XlsxReadOptions(Builder builder) {
    super(builder);
    sheetIndex = builder.sheetIndex;
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

  public static Builder builder(URL url) throws IOException {
    return new Builder(url);
  }

  public static Builder builderFromFile(String fileName) {
    return new Builder(new File(fileName));
  }

  public static Builder builderFromUrl(String url) throws IOException {
    return new Builder(new URL(url));
  }

  public Integer sheetIndex() {
    return sheetIndex;
  }

  public static class Builder extends ReadOptions.Builder {

    protected Integer sheetIndex;

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
    public XlsxReadOptions build() {
      return new XlsxReadOptions(this);
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
    public Builder missingValueIndicator(String... missingValueIndicators) {
      super.missingValueIndicator(missingValueIndicators);
      return this;
    }

    @Override
    public Builder minimizeColumnSizes() {
      super.minimizeColumnSizes();
      return this;
    }

    public Builder sheetIndex(int sheetIndex) {
      this.sheetIndex = sheetIndex;
      return this;
    }

    @Override
    public Builder columnTypes(ColumnType[] columnTypes) {
      super.columnTypes(columnTypes);
      return this;
    }

    @Override
    public Builder columnTypes(Function<String, ColumnType> columnTypeFunction) {
      super.columnTypes(columnTypeFunction);
      return this;
    }

    @Override
    public Builder columnTypesPartial(Function<String, Optional<ColumnType>> columnTypeFunction) {
      super.columnTypesPartial(columnTypeFunction);
      return this;
    }

    @Override
    public Builder columnTypesPartial(Map<String, ColumnType> columnTypeByName) {
      super.columnTypesPartial(columnTypeByName);
      return this;
    }
  }
}
