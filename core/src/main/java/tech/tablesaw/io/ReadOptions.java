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

package tech.tablesaw.io;

import static tech.tablesaw.api.ColumnType.BOOLEAN;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE_TIME;
import static tech.tablesaw.api.ColumnType.LOCAL_TIME;
import static tech.tablesaw.api.ColumnType.LONG;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.api.ColumnType.TEXT;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import tech.tablesaw.api.ColumnType;

public class ReadOptions {

  public static final boolean DEFAULT_IGNORE_ZERO_DECIMAL = true;

  private static final List<ColumnType> DEFAULT_TYPES =
      Lists.newArrayList(
          LOCAL_DATE_TIME, LOCAL_TIME, LOCAL_DATE, BOOLEAN, INTEGER, LONG, DOUBLE, STRING, TEXT);

  /**
   * An extended list of types that are used if minimizeColumnSizes is true. By including extra
   * types like Short the resulting table size is reduced at the cost of some additional complexity
   * for the programmer if, for example, they will subsequently modify the data in a way that
   * exceeds the range of the type.
   */
  protected static final List<ColumnType> EXTENDED_TYPES =
      Lists.newArrayList(
          LOCAL_DATE_TIME,
          LOCAL_TIME,
          LOCAL_DATE,
          BOOLEAN,
          SHORT,
          INTEGER,
          LONG,
          FLOAT,
          DOUBLE,
          STRING,
          TEXT);

  protected final Source source;
  protected final String tableName;
  protected final List<ColumnType> columnTypesToDetect;
  protected final boolean sample;
  protected final String dateFormat;
  protected final String dateTimeFormat;
  protected final String timeFormat;
  protected final Locale locale;
  protected final String missingValueIndicator;
  protected final boolean minimizeColumnSizes;
  protected final int maxCharsPerColumn;
  protected final boolean ignoreZeroDecimal;
  protected final boolean allowDuplicateColumnNames;

  protected final DateTimeFormatter dateFormatter;
  protected final DateTimeFormatter dateTimeFormatter;
  protected final DateTimeFormatter timeFormatter;

  protected final ColumnTypeReadOptions columnTypeReadOptions;

  protected final boolean header;

  protected ReadOptions(ReadOptions.Builder builder) {
    source = builder.source;
    tableName = builder.tableName;
    columnTypesToDetect = builder.columnTypesToDetect;
    sample = builder.sample;
    dateFormat = builder.dateFormat;
    timeFormat = builder.timeFormat;
    dateTimeFormat = builder.dateTimeFormat;
    missingValueIndicator = builder.missingValueIndicator;
    minimizeColumnSizes = builder.minimizeColumnSizes;
    header = builder.header;
    maxCharsPerColumn = builder.maxCharsPerColumn;
    ignoreZeroDecimal = builder.ignoreZeroDecimal;

    dateFormatter = builder.dateFormatter;
    timeFormatter = builder.timeFormatter;
    dateTimeFormatter = builder.dateTimeFormatter;

    allowDuplicateColumnNames = builder.allowDuplicateColumnNames;

    columnTypeReadOptions =
        new ColumnTypeReadOptions(
            builder.columnTypes,
            builder.columnTypeMap,
            builder.completeColumnTypeFunction,
            builder.columnTypeFunction);

    if (builder.locale == null) {
      locale = Locale.getDefault();
    } else {
      locale = builder.locale;
    }
  }

  public Source source() {
    return source;
  }

  public String tableName() {
    return tableName;
  }

  public boolean allowDuplicateColumnNames() {
    return allowDuplicateColumnNames;
  }

  public List<ColumnType> columnTypesToDetect() {
    return columnTypesToDetect;
  }

  public boolean sample() {
    return sample;
  }

  public boolean minimizeColumnSizes() {
    return minimizeColumnSizes;
  }

  public String missingValueIndicator() {
    return missingValueIndicator;
  }

  public Locale locale() {
    return locale;
  }

  public boolean header() {
    return header;
  }

  public boolean ignoreZeroDecimal() {
    return ignoreZeroDecimal;
  }

  public DateTimeFormatter dateTimeFormatter() {
    if (dateTimeFormatter != null) {
      return dateTimeFormatter;
    }

    if (Strings.isNullOrEmpty(dateTimeFormat)) {
      return null;
    }
    return DateTimeFormatter.ofPattern(dateTimeFormat, locale);
  }

  public DateTimeFormatter timeFormatter() {
    if (timeFormatter != null) {
      return timeFormatter;
    }
    if (Strings.isNullOrEmpty(timeFormat)) {
      return null;
    }
    return DateTimeFormatter.ofPattern(timeFormat, locale);
  }

  public DateTimeFormatter dateFormatter() {
    if (dateFormatter != null) {
      return dateFormatter;
    }
    if (Strings.isNullOrEmpty(dateFormat)) {
      return null;
    }
    return DateTimeFormatter.ofPattern(dateFormat, locale);
  }

  public ColumnTypeReadOptions columnTypeReadOptions() {
    return columnTypeReadOptions;
  }

  protected static class Builder {

    protected final Source source;
    protected String tableName = "";
    protected List<ColumnType> columnTypesToDetect = DEFAULT_TYPES;
    protected boolean sample = true;
    protected String dateFormat;
    protected DateTimeFormatter dateFormatter;
    protected String timeFormat;
    protected DateTimeFormatter timeFormatter;
    protected String dateTimeFormat;
    protected DateTimeFormatter dateTimeFormatter;
    protected Locale locale;
    protected String missingValueIndicator;
    protected boolean minimizeColumnSizes = false;
    protected boolean header = true;
    protected int maxCharsPerColumn = 4096;
    protected boolean ignoreZeroDecimal = DEFAULT_IGNORE_ZERO_DECIMAL;
    private boolean allowDuplicateColumnNames = false;
    protected ColumnType[] columnTypes;
    protected Map<String, ColumnType> columnTypeMap = new HashMap<>();
    protected Function<String, Optional<ColumnType>> columnTypeFunction;
    protected Function<String, ColumnType> completeColumnTypeFunction;

    protected Builder() {
      source = null;
    }

    protected Builder(Source source) {
      this.source = source;
    }

    protected Builder(File file) {
      this.source = new Source(file);
      this.tableName = file.getName();
    }

    protected Builder(URL url) throws IOException {
      this.source = new Source(url.openStream());
      this.tableName = url.toString();
    }

    protected Builder(InputStream stream) {
      this.source = new Source(stream);
    }

    protected Builder(InputStreamReader reader) {
      this.source = new Source(reader);
    }

    protected Builder(Reader reader) {
      this.source = new Source(reader);
    }

    public Builder tableName(String tableName) {
      this.tableName = tableName;
      return this;
    }

    public Builder header(boolean hasHeader) {
      this.header = hasHeader;
      return this;
    }

    /** Deprecated. Use dateFormat(DateTimeFormatter dateFormat) instead */
    @Deprecated
    public Builder dateFormat(String dateFormat) {
      this.dateFormat = dateFormat;
      return this;
    }

    public Builder dateFormat(DateTimeFormatter dateFormat) {
      this.dateFormatter = dateFormat;
      return this;
    }

    public Builder allowDuplicateColumnNames(Boolean allow) {
      this.allowDuplicateColumnNames = allow;
      return this;
    }

    /** Deprecated. Use timeFormat(DateTimeFormatter dateFormat) instead */
    @Deprecated
    public Builder timeFormat(String timeFormat) {
      this.timeFormat = timeFormat;
      return this;
    }

    public Builder timeFormat(DateTimeFormatter dateFormat) {
      this.timeFormatter = dateFormat;
      return this;
    }

    /** Deprecated. Use dateTimeFormat(DateTimeFormatter dateFormat) instead */
    @Deprecated
    public Builder dateTimeFormat(String dateTimeFormat) {
      this.dateTimeFormat = dateTimeFormat;
      return this;
    }

    public Builder dateTimeFormat(DateTimeFormatter dateFormat) {
      this.dateTimeFormatter = dateFormat;
      return this;
    }

    public Builder missingValueIndicator(String missingValueIndicator) {
      this.missingValueIndicator = missingValueIndicator;
      return this;
    }

    public Builder maxCharsPerColumn(int maxCharsPerColumn) {
      this.maxCharsPerColumn = maxCharsPerColumn;
      return this;
    }

    /** Ignore zero value decimals in data values. Defaults to {@code true}. */
    public Builder ignoreZeroDecimal(boolean ignoreZeroDecimal) {
      this.ignoreZeroDecimal = ignoreZeroDecimal;
      return this;
    }

    public Builder sample(boolean sample) {
      this.sample = sample;
      return this;
    }

    public Builder locale(Locale locale) {
      this.locale = locale;
      return this;
    }

    /** @see ColumnTypeDetector */
    public Builder columnTypesToDetect(List<ColumnType> columnTypesToDetect) {
      // Types need to be in certain order as more general types like string come last
      // Otherwise everything will be parsed as a string
      List<ColumnType> orderedTypes = new ArrayList<>();
      for (ColumnType t : EXTENDED_TYPES) {
        if (columnTypesToDetect.contains(t)) {
          orderedTypes.add(t);
        }
      }
      this.columnTypesToDetect = orderedTypes;
      return this;
    }

    /**
     * Allow the {@link ColumnTypeDetector} to choose shorter column types such as float instead of
     * double when the data will fit in a smaller type
     */
    public Builder minimizeColumnSizes() {
      this.columnTypesToDetect = EXTENDED_TYPES;
      return this;
    }

    /**
     * Provide column types for all columns preventing autodetect column type logic. It's expected
     * that the array contains all columns
     */
    public Builder columnTypes(ColumnType[] columnTypes) {
      this.columnTypes = columnTypes;
      return this;
    }

    public Builder columnType(String columnName, ColumnType columnType) {
      this.columnTypeMap.put(columnName, columnType);
      return this;
    }

    /**
     * Provide a function that determines ColumnType for some column names. To provide for all
     * column names use {@link #completeColumnTypeByNameFunction(Function)} because it prevents
     * running unnecessary autodetect column type logic that can be expensive in some situations
     */
    public Builder columnTypeByNameFunction(
        Function<String, Optional<ColumnType>> columnTypeFunction) {
      this.columnTypeFunction = columnTypeFunction;
      return this;
    }

    /**
     * Provide a function that determines ColumnType for all column names. To provide only for some
     * use {@link #columnTypeByNameFunction(Function)}
     *
     * <p>Providing that function prevents running autodetect column type logic
     */
    public Builder completeColumnTypeByNameFunction(
        Function<String, ColumnType> columnTypeFunction) {
      this.completeColumnTypeFunction = columnTypeFunction;
      return this;
    }

    public Builder columnTypes(Map<String, ColumnType> columnTypeByName) {
      if (columnTypeByName != null) this.columnTypeMap = columnTypeByName;

      return this;
    }

    public ReadOptions build() {
      return new ReadOptions(this);
    }
  }

  /**
   * Allow to customize read column types. It can work in three ways:
   *
   * <ul>
   *   <li>If no information is provided column types are autodetected
   *   <li>A complete list of columns can be provided using {@link
   *       ReadOptions.Builder#columnTypes(ColumnType[])} or {@link
   *       ReadOptions.Builder#completeColumnTypeFunction} and they are used preventing autodetect
   *   <li>Provide values for some column names using {@link
   *       ReadOptions.Builder#columnType(String,ColumnType)} or {@link
   *       ReadOptions.Builder#columnTypeByNameFunction(Function)} (String, ColumnType)}. In this
   *       case provided columnTypes are used and the others are autodetected
   * </ul>
   */
  public static class ColumnTypeReadOptions {
    final ColumnType[] columnTypesByIdx;
    final Map<String, ColumnType> columnTypesByNameMap;
    final Function<String, Optional<ColumnType>> columnTypesByNameFunction;
    final Function<String, ColumnType> completeColumnTypesByNameFunction;

    public static ColumnTypeReadOptions of(ColumnType[] allColumnTypes) {
      return new ColumnTypeReadOptions(allColumnTypes, null, null, null);
    }

    ColumnTypeReadOptions(
        ColumnType[] columnTypesByIdx,
        Map<String, ColumnType> columnTypesByNameMap,
        Function<String, ColumnType> completeColumnTypesByNameFunction,
        Function<String, Optional<ColumnType>> columnTypesByNameFunction) {
      this.columnTypesByIdx = columnTypesByIdx;
      this.columnTypesByNameMap = columnTypesByNameMap;
      this.columnTypesByNameFunction = columnTypesByNameFunction;
      this.completeColumnTypesByNameFunction = completeColumnTypesByNameFunction;
    }

    public Optional<ColumnType> columnType(int columnNumber, String columnName) {
      Optional<ColumnType> columnType = Optional.empty();
      if (columnTypesByIdx != null && columnNumber < columnTypesByIdx.length)
        columnType = Optional.ofNullable(columnTypesByIdx[columnNumber]);
      if (!columnType.isPresent() && columnTypesByNameMap != null)
        columnType = Optional.ofNullable(columnTypesByNameMap.get(columnName));
      if (!columnType.isPresent() && completeColumnTypesByNameFunction != null)
        columnType = Optional.of(completeColumnTypesByNameFunction.apply(columnName));
      if (!columnType.isPresent() && columnTypesByNameFunction != null)
        columnType = columnTypesByNameFunction.apply(columnName);
      return columnType;
    }

    public ColumnType[] columnTypes() {
      return columnTypesByIdx;
    }

    public boolean canCalculateColumnTypeForAllColumns() {
      return hasColumnTypeForAllColumns() || completeColumnTypesByNameFunction != null;
    }

    public boolean hasColumnTypeForAllColumns() {
      return columnTypesByIdx != null && columnTypesByIdx.length > 0;
    }
  }
}
