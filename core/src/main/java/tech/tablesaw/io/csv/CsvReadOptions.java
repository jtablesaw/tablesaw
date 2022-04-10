/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.csv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.Source;

public class CsvReadOptions extends ReadOptions {
  private final Character separator;
  private final Character quoteChar;
  private final Character escapeChar;
  private final String lineEnding;
  private final Integer maxNumberOfColumns;
  private final Character commentPrefix;
  private final boolean lineSeparatorDetectionEnabled;
  private final int sampleSize;

  private CsvReadOptions(CsvReadOptions.Builder builder) {
    super(builder);
    separator = builder.separator;
    quoteChar = builder.quoteChar;
    escapeChar = builder.escapeChar;
    lineEnding = builder.lineEnding;
    maxNumberOfColumns = builder.maxNumberOfColumns;
    commentPrefix = builder.commentPrefix;
    lineSeparatorDetectionEnabled = builder.lineSeparatorDetectionEnabled;
    sampleSize = builder.sampleSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CsvReadOptions that = (CsvReadOptions) o;
    return lineSeparatorDetectionEnabled == that.lineSeparatorDetectionEnabled
        && sampleSize == that.sampleSize
        && Objects.equals(separator, that.separator)
        && Objects.equals(quoteChar, that.quoteChar)
        && Objects.equals(escapeChar, that.escapeChar)
        && Objects.equals(lineEnding, that.lineEnding)
        && Objects.equals(maxNumberOfColumns, that.maxNumberOfColumns)
        && Objects.equals(commentPrefix, that.commentPrefix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        separator,
        quoteChar,
        escapeChar,
        lineEnding,
        maxNumberOfColumns,
        commentPrefix,
        lineSeparatorDetectionEnabled,
        sampleSize);
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

  public static Builder builderFromString(String contents) {
    return new Builder(new StringReader(contents));
  }

  public static Builder builderFromUrl(String url) throws IOException {
    return new Builder(new URL(url));
  }

  /**
   * This method may cause tablesaw to buffer the entire InputStream.
   *
   * <p>If you have a large amount of data, you can do one of the following: 1. Use the method
   * taking a File instead of a stream, or 2. Provide the array of column types as an option. If you
   * provide the columnType array, we skip type detection and can avoid reading the entire file
   */
  public static Builder builder(InputStream stream) {
    return new Builder(stream);
  }

  /**
   * This method may cause tablesaw to buffer the entire InputStream.
   *
   * <p>If you have a large amount of data, you can do one of the following: 1. Use the method
   * taking a File instead of a reader, or 2. Provide the array of column types as an option. If you
   * provide the columnType array, we skip type detection and can avoid reading the entire file
   */
  public static Builder builder(Reader reader) {
    return new Builder(reader);
  }

  /**
   * This method may cause tablesaw to buffer the entire InputStream.
   *
   * <p>If you have a large amount of data, you can do one of the following: 1. Use the method
   * taking a File instead of a reader, or 2. Provide the array of column types as an option. If you
   * provide the columnType array, we skip type detection and can avoid reading the entire file
   */
  public static Builder builder(InputStreamReader reader) {
    return new Builder(reader);
  }

  public ColumnType[] columnTypes() {
    return columnTypeReadOptions.columnTypes();
  }

  public Character separator() {
    return separator;
  }

  public Character quoteChar() {
    return quoteChar;
  }

  public Character escapeChar() {
    return escapeChar;
  }

  public String lineEnding() {
    return lineEnding;
  }

  public boolean lineSeparatorDetectionEnabled() {
    return lineSeparatorDetectionEnabled;
  }

  public Integer maxNumberOfColumns() {
    return maxNumberOfColumns;
  }

  public Character commentPrefix() {
    return commentPrefix;
  }

  public int maxCharsPerColumn() {
    return maxCharsPerColumn;
  }

  public int sampleSize() {
    return sampleSize;
  }

  public static class Builder extends ReadOptions.Builder {

    private Character separator;
    private Character quoteChar;
    private Character escapeChar;
    private String lineEnding;
    private Integer maxNumberOfColumns = 10_000;
    private Character commentPrefix;
    private boolean lineSeparatorDetectionEnabled = true;
    private int sampleSize = -1;

    protected Builder(Source source) {
      super(source);
    }

    protected Builder(URL url) throws IOException {
      super(url);
    }

    protected Builder(File file) {
      super(file);
    }

    protected Builder(InputStreamReader reader) {
      super(reader);
    }

    protected Builder(Reader reader) {
      super(reader);
    }

    protected Builder(InputStream stream) {
      super(stream);
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

    public Builder separator(Character separator) {
      this.separator = separator;
      return this;
    }

    public Builder quoteChar(Character quoteChar) {
      this.quoteChar = quoteChar;
      return this;
    }

    public Builder escapeChar(Character escapeChar) {
      this.escapeChar = escapeChar;
      return this;
    }

    public Builder lineEnding(String lineEnding) {
      this.lineEnding = lineEnding;
      this.lineSeparatorDetectionEnabled = false;
      return this;
    }

    /**
     * Defines maximal value of columns in csv file.
     *
     * @param maxNumberOfColumns - must be positive integer. Default is 10,000
     */
    public Builder maxNumberOfColumns(Integer maxNumberOfColumns) {
      this.maxNumberOfColumns = maxNumberOfColumns;
      return this;
    }

    public Builder commentPrefix(Character commentPrefix) {
      this.commentPrefix = commentPrefix;
      return this;
    }

    /**
     * Defines the maximum number of rows to be read from the file. Sampling is performed in a
     * single pass using the reservoir sampling algorithm
     * (https://en.wikipedia.org/wiki/Reservoir_sampling). Given a file with 'n' rows, if
     * 'numSamples is smaller than 'n', than exactly 'numSamples' random samples are returned; if
     * 'numSamples' is greater than 'n', then only 'n' samples are returned (no oversampling is
     * performed to increase the data to match 'numSamples').
     */
    public Builder sampleSize(int numSamples) {
      this.sampleSize = numSamples;
      return this;
    }

    @Override
    public CsvReadOptions build() {
      return new CsvReadOptions(this);
    }

    // Override super-class setters to return an instance of this class

    @Override
    public Builder header(boolean header) {
      super.header(header);
      return this;
    }

    /**
     * Enable reading of a table with duplicate column names. After the first appearance of a column
     * name, subsequent appearances will have a number appended.
     *
     * @param allow if true, duplicate names will be allowed
     */
    @Override
    public Builder allowDuplicateColumnNames(Boolean allow) {
      super.allowDuplicateColumnNames(allow);
      return this;
    }

    @Override
    public Builder columnTypesToDetect(List<ColumnType> columnTypesToDetect) {
      super.columnTypesToDetect(columnTypesToDetect);
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
    public Builder maxCharsPerColumn(int maxCharsPerColumn) {
      super.maxCharsPerColumn(maxCharsPerColumn);
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

    @Override
    public Builder ignoreZeroDecimal(boolean ignoreZeroDecimal) {
      super.ignoreZeroDecimal(ignoreZeroDecimal);
      return this;
    }

    @Override
    public Builder skipRowsWithInvalidColumnCount(boolean skipRowsWithInvalidColumnCount) {
      super.skipRowsWithInvalidColumnCount(skipRowsWithInvalidColumnCount);
      return this;
    }
  }
}
