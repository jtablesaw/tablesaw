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

package tech.tablesaw.io.fixed;

import com.google.common.io.CharStreams;
import com.univocity.parsers.common.AbstractParser;
import com.univocity.parsers.common.NormalizedString;
import com.univocity.parsers.fixed.FixedWidthFormat;
import com.univocity.parsers.fixed.FixedWidthParser;
import com.univocity.parsers.fixed.FixedWidthParserSettings;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.concurrent.Immutable;
import org.apache.commons.math3.util.Pair;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.*;

@Immutable
public class FixedWidthReader extends FileReader implements DataReader<FixedWidthReadOptions> {

  private static final FixedWidthReader INSTANCE = new FixedWidthReader();

  static {
    register(Table.defaultReaderRegistry);
  }

  public static void register(ReaderRegistry registry) {
    registry.registerOptions(FixedWidthReadOptions.class, INSTANCE);
  }

  /** Constructs a FixedWidthReader */
  public FixedWidthReader() {
    super();
  }

  /**
   * Determines column types if not provided by the user Reads all input into memory unless File was
   * provided
   */
  private Pair<Reader, ReadOptions.ColumnTypeReadOptions> getReaderAndColumnTypes(
      FixedWidthReadOptions options) {
    ReadOptions.ColumnTypeReadOptions columnTypeReadOptions = options.columnTypeReadOptions();
    byte[] bytesCache = null;

    boolean hasColumnNames =
        options.columnSpecs() != null
            && options.columnSpecs().getFieldNames() != null
            && options.columnSpecs().getFieldNames().length > 0;
    try {
      if (!options.columnTypeReadOptions().hasColumnTypeForAllColumns()
          && (!options.columnTypeReadOptions().hasColumnTypeForAllColumnsIfHavingColumnNames()
              || !hasColumnNames)) {
        Reader reader = options.source().createReader(bytesCache);
        if (options.source().file() == null) {

          bytesCache = CharStreams.toString(reader).getBytes();

          // create a new reader since we just exhausted the existing one
          reader = options.source().createReader(bytesCache);
        }
        columnTypeReadOptions =
            ReadOptions.ColumnTypeReadOptions.of(detectColumnTypes(reader, options));
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }

    try {
      return Pair.create(options.source().createReader(bytesCache), columnTypeReadOptions);
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  public Table read(FixedWidthReadOptions options) {
    return read(options, false);
  }

  private Table read(FixedWidthReadOptions options, boolean headerOnly) {
    Pair<Reader, ReadOptions.ColumnTypeReadOptions> pair = getReaderAndColumnTypes(options);
    Reader reader = pair.getKey();
    ReadOptions.ColumnTypeReadOptions columnTypeReadOptions = pair.getValue();

    FixedWidthParser parser = fixedWidthParser(options);

    try {
      return parseRows(options, headerOnly, reader, columnTypeReadOptions, parser);
    } finally {
      if (options.source().reader() == null) {
        // if we get a reader back from options it means the client opened it, so let the client
        // close it
        // if it's null, we close it here.
        parser.stopParsing();
        try {
          reader.close();
        } catch (IOException e) {
          throw new RuntimeIOException(e);
        }
      }
    }
  }

  /**
   * Returns a string representation of the column types in file {@code fixed widthFilename}, as
   * determined by the type-detection algorithm
   *
   * <p>This method is intended to help analysts quickly fix any erroneous types, by printing out
   * the types in a format such that they can be edited to correct any mistakes, and used in an
   * array literal
   *
   * <p>For example:
   *
   * <p>LOCAL_DATE, // 0 date SHORT, // 1 approval STRING, // 2 who
   *
   * <p>Note that the types are array separated, and that the index position and the column name are
   * printed such that they would be interpreted as comments if you paste the output into an array:
   */
  public String printColumnTypes(FixedWidthReadOptions options) {

    Table structure = read(options, true).structure();
    return getTypeString(structure);
  }

  /**
   * Estimates and returns the type for each column in the delimited text file {@code file}
   *
   * <p>The type is determined by checking a sample of the data in the file. Because only a sample
   * of the data is checked, the types may be incorrect. If that is the case a Parse Exception will
   * be thrown.
   *
   * <p>The method {@code printColumnTypes()} can be used to print a list of the detected columns
   * that can be corrected and used to explicitly specify the correct column types.
   */
  public ColumnType[] detectColumnTypes(Reader reader, FixedWidthReadOptions options) {

    boolean header = options.header();
    int linesToSkip = header ? 1 : 0;

    AbstractParser<?> parser = fixedWidthParser(options);

    try {
      String[] columnNames =
          Optional.ofNullable(options.columnSpecs())
              .flatMap(specs -> Optional.ofNullable(specs.getFieldNames()))
              .map(
                  fieldNames ->
                      Arrays.stream(fieldNames)
                          .map(NormalizedString::toString)
                          .toArray(String[]::new))
              .orElse(null);
      return getColumnTypes(reader, options, linesToSkip, parser, columnNames);
    } finally {
      parser.stopParsing();
      // we don't close the reader since we didn't create it
    }
  }

  private FixedWidthParser fixedWidthParser(FixedWidthReadOptions options) {
    FixedWidthParserSettings settings = new FixedWidthParserSettings();

    if (options.columnSpecs() != null) {
      settings = new FixedWidthParserSettings(options.columnSpecs());
    }
    settings.setFormat(fixedWidthFormat(options));
    settings.setMaxCharsPerColumn(options.maxNumberOfColumns());
    if (options.skipTrailingCharsUntilNewline()) {
      settings.setSkipTrailingCharsUntilNewline(options.skipTrailingCharsUntilNewline());
    }
    if (options.maxNumberOfColumns() != null) {
      settings.setMaxColumns(options.maxNumberOfColumns());
    }
    if (options.recordEndsOnNewline()) {
      settings.setRecordEndsOnNewline(true);
    }
    return new FixedWidthParser(settings);
  }

  private FixedWidthFormat fixedWidthFormat(FixedWidthReadOptions options) {
    FixedWidthFormat format = new FixedWidthFormat();
    if (options.padding() != ' ') {
      format.setPadding(options.padding());
    }
    if (options.lookupWildcard() != '?') {
      format.setLookupWildcard(options.lookupWildcard());
    }
    if (options.lineEnding() != null) {
      format.setLineSeparator(options.lineEnding());
    }
    return format;
  }

  @Override
  public Table read(Source source) {
    return read(FixedWidthReadOptions.builder(source).build());
  }
}
