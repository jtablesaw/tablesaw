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
import tech.tablesaw.io.ReadOptions.ColumnTypeReadOptions;

public class ReadOptions {

  public static final boolean DEFAULT_IGNORE_ZERO_DECIMAL = true;
  public static final boolean DEFAULT_SKIP_ROWS_WITH_INVALID_COLUMN_COUNT = false;

  private static final List<ColumnType> DEFAULT_TYPES =
      Lists.newArrayList(
          LOCAL_DATE_TIME, LOCAL_TIME, LOCAL_DATE, BOOLEAN, INTEGER, LONG, DOUBLE, STRING // , TEXT
          );

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
          STRING);

  protected final Source source;
  protected final String tableName;
  protected final List<ColumnType> columnTypesToDetect;
  protected final boolean sample;
  protected final String dateFormat;
  protected final String dateTimeFormat;
  protected final String timeFormat;
  protected final Locale locale;
  protected final String[] missingValueIndicators;
  protected final boolean minimizeColumnSizes;
  protected final int maxCharsPerColumn;
  protected final boolean ignoreZeroDecimal;
  protected final boolean allowDuplicateColumnNames;
  protected final boolean skipRowsWithInvalidColumnCount;

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
    missingValueIndicators = builder.missingValueIndicators;
    minimizeColumnSizes = builder.minimizeColumnSizes;
    header = builder.header;
    maxCharsPerColumn = builder.maxCharsPerColumn;
    ignoreZeroDecimal = builder.ignoreZeroDecimal;
    skipRowsWithInvalidColumnCount = builder.skipRowsWithInvalidColumnCount;

    dateFormatter = builder.dateFormatter;
    timeFormatter = builder.timeFormatter;
    dateTimeFormatter = builder.dateTimeFormatter;
    allowDuplicateColumnNames = builder.allowDuplicateColumnNames;
    locale = builder.locale;

    if (builder.columnTypes != null)
      columnTypeReadOptions = new ByIdxColumnTypeReadOptions(builder.columnTypes);
    else if (!builder.columnTypeMap.isEmpty())
      columnTypeReadOptions = new ByNameMapColumnTypeReadOptions(builder.columnTypeMap);
    else if (builder.completeColumnTypeFunction != null)
      columnTypeReadOptions =
          new CompleteFunctionColumnTypeReadOptions(builder.completeColumnTypeFunction);
    else if (builder.columnTypeFunction != null)
      columnTypeReadOptions = new PartialFunctionColumnTypeReadOptions(builder.columnTypeFunction);
    else columnTypeReadOptions = ColumnTypeReadOptions.EMPTY;
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

  public String[] missingValueIndicators() {
    return missingValueIndicators;
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

  public boolean skipRowsWithInvalidColumnCount() {
    return skipRowsWithInvalidColumnCount;
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
    protected Locale locale = Locale.getDefault();
    protected String[] missingValueIndicators = new String[0];
    protected boolean minimizeColumnSizes = false;
    protected boolean header = true;
    protected int maxCharsPerColumn = 4096;
    protected boolean ignoreZeroDecimal = DEFAULT_IGNORE_ZERO_DECIMAL;
    protected boolean skipRowsWithInvalidColumnCount = DEFAULT_SKIP_ROWS_WITH_INVALID_COLUMN_COUNT;
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

    public Builder dateFormat(DateTimeFormatter dateFormat) {
      this.dateFormatter = dateFormat;
      return this;
    }

    public Builder allowDuplicateColumnNames(Boolean allow) {
      this.allowDuplicateColumnNames = allow;
      return this;
    }

    public Builder timeFormat(DateTimeFormatter dateFormat) {
      this.timeFormatter = dateFormat;
      return this;
    }

    public Builder dateTimeFormat(DateTimeFormatter dateFormat) {
      this.dateTimeFormatter = dateFormat;
      return this;
    }

    public Builder missingValueIndicator(String... missingValueIndicators) {
      this.missingValueIndicators = missingValueIndicators;
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

    /** Skip the rows with invalid column count in data values. Defaluts to {@code false}. */
    public Builder skipRowsWithInvalidColumnCount(boolean skipRowsWithInvalidColumnCount) {
      this.skipRowsWithInvalidColumnCount = skipRowsWithInvalidColumnCount;
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
     * Provide column types for all columns skipping autodetect column type logic. The array must
     * contain a ColumnType for each column in the table. An error will be thrown if they don't
     * match up
     */
    public Builder columnTypes(ColumnType[] columnTypes) {
      if (columnTypeOptionsAlreadySet()) {
        throw new IllegalStateException("columnTypes already set");
      }
      this.columnTypes = columnTypes;
      return this;
    }

    /**
     * Provide a function that determines ColumnType for all column names. To provide only for some
     * use {@link #columnTypesPartial(Function)}
     *
     * <p>This method is generally more efficient because it skips column type detection
     */
    public Builder columnTypes(Function<String, ColumnType> columnTypeFunction) {
      if (columnTypeOptionsAlreadySet()) {
        throw new IllegalStateException("columnTypes already set");
      }
      this.completeColumnTypeFunction = columnTypeFunction;
      return this;
    }

    /**
     * Provide a function that determines ColumnType for some column names. To provide for all
     * column names use {@link #columnTypes(Function)} that generally is more efficient because it
     * skips column type detection
     */
    public Builder columnTypesPartial(Function<String, Optional<ColumnType>> columnTypeFunction) {
      if (columnTypeOptionsAlreadySet()) {
        throw new IllegalStateException("columnTypes already set");
      }
      this.columnTypeFunction = columnTypeFunction;
      return this;
    }

    /**
     * Provide a map that determines ColumnType for given column names. Types for not present column
     * names will be autodetected. To provide type for all column names use {@link
     * #columnTypes(Function)} that generally is more efficient because it skips column type
     * detection
     */
    public Builder columnTypesPartial(Map<String, ColumnType> columnTypeByName) {
      if (columnTypeOptionsAlreadySet()) {
        throw new IllegalStateException("columnTypes already set");
      }
      if (columnTypeByName != null) {
        this.columnTypeMap = columnTypeByName;
      }
      return this;
    }

    private boolean columnTypeOptionsAlreadySet() {
      return columnTypes != null
          || columnTypeFunction != null
          || completeColumnTypeFunction != null
          || !columnTypeMap.isEmpty();
    }

    public ReadOptions build() {
      return new ReadOptions(this);
    }
  }

  /**
   * Allows user to set column types. It can work in three ways:
   *
   * <ul>
   *   <li>If no information is provided column types are autodetected
   *   <li>A complete list of columns can be provided using {@link
   *       ReadOptions.Builder#columnTypes(ColumnType[])} or {@link
   *       ReadOptions.Builder#columnTypes(Function)}. This skips column type detection.
   *   <li>Provide values for some column names using {@link
   *       ReadOptions.Builder#columnTypesPartial(Map)} or {@link
   *       ReadOptions.Builder#columnTypesPartial(Function)} (String, ColumnType)}. In this case
   *       provided columnTypes are used and the others are autodetected
   * </ul>
   */
  public interface ColumnTypeReadOptions {
    Optional<ColumnType> columnType(int columnNumber, String columnName);

    default boolean hasColumnTypeForAllColumnsIfHavingColumnNames() {
      return false;
    }

    default boolean hasColumnTypeForAllColumns() {
      return false;
    }

    default ColumnType[] columnTypes() {
      return null;
    }

    ColumnTypeReadOptions EMPTY = (columnNumber, columnName) -> Optional.empty();

    static ColumnTypeReadOptions of(ColumnType[] allColumnTypes) {
      return new ByIdxColumnTypeReadOptions(allColumnTypes);
    }
  }
}

class ByIdxColumnTypeReadOptions implements ColumnTypeReadOptions {
  final ColumnType[] columnTypesByIdx;

  public ByIdxColumnTypeReadOptions(ColumnType[] columnTypesByIdx) {
    this.columnTypesByIdx = columnTypesByIdx;
  }

  @Override
  public Optional<ColumnType> columnType(int columnNumber, String columnName) {
    return Optional.of(columnTypesByIdx[columnNumber]);
  }

  @Override
  public ColumnType[] columnTypes() {
    return columnTypesByIdx;
  }

  @Override
  public boolean hasColumnTypeForAllColumnsIfHavingColumnNames() {
    return true;
  }

  @Override
  public boolean hasColumnTypeForAllColumns() {
    return true;
  }
}

class ByNameMapColumnTypeReadOptions implements ColumnTypeReadOptions {
  private final Map<String, ColumnType> columnTypesByNameMap;

  public ByNameMapColumnTypeReadOptions(Map<String, ColumnType> columnTypesByNameMap) {
    this.columnTypesByNameMap = columnTypesByNameMap;
  }

  @Override
  public Optional<ColumnType> columnType(int columnNumber, String columnName) {
    return Optional.ofNullable(columnTypesByNameMap.get(columnName));
  }
}

class CompleteFunctionColumnTypeReadOptions implements ColumnTypeReadOptions {
  private final Function<String, ColumnType> function;

  public CompleteFunctionColumnTypeReadOptions(Function<String, ColumnType> function) {
    this.function = function;
  }

  @Override
  public Optional<ColumnType> columnType(int columnNumber, String columnName) {
    return Optional.of(function.apply(columnName));
  }

  @Override
  public boolean hasColumnTypeForAllColumnsIfHavingColumnNames() {
    return true;
  }
}

class PartialFunctionColumnTypeReadOptions implements ColumnTypeReadOptions {
  final Function<String, Optional<ColumnType>> function;

  public PartialFunctionColumnTypeReadOptions(Function<String, Optional<ColumnType>> function) {
    this.function = function;
  }

  @Override
  public Optional<ColumnType> columnType(int columnNumber, String columnName) {
    return function.apply(columnName);
  }
}
