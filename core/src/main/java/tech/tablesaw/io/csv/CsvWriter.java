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

import com.univocity.parsers.csv.CsvWriterSettings;
import java.time.format.DateTimeFormatter;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

/** Class that writes tables and individual columns to CSV files */
@Immutable
public final class CsvWriter implements DataWriter<CsvWriteOptions> {

  private static final CsvWriter INSTANCE = new CsvWriter();
  private static final String nullValue = "";

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerExtension("csv", INSTANCE);
    registry.registerOptions(CsvWriteOptions.class, INSTANCE);
  }

  public void write(Table table, CsvWriteOptions options) {
    CsvWriterSettings settings = createSettings(options);

    com.univocity.parsers.csv.CsvWriter csvWriter = null;
    // Creates a writer with the above settings;
    try {
      csvWriter =
          new com.univocity.parsers.csv.CsvWriter(options.destination().createWriter(), settings);

      writeHeader(table, options, csvWriter);
      for (int r = 0; r < table.rowCount(); r++) {
        String[] entries = new String[table.columnCount()];
        for (int c = 0; c < table.columnCount(); c++) {
          writeValues(table, options, r, entries, c);
        }
        csvWriter.writeRow(entries);
      }
    } finally {
      if (csvWriter != null) {
        csvWriter.flush();
        if (options.autoClose()) csvWriter.close();
      }
    }
  }

  private void writeValues(Table table, CsvWriteOptions options, int r, String[] entries, int c) {
    DateTimeFormatter dateFormatter = options.dateFormatter();
    DateTimeFormatter dateTimeFormatter = options.dateTimeFormatter();
    ColumnType columnType = table.column(c).type();
    if (dateFormatter != null && columnType.equals(ColumnType.LOCAL_DATE)) {
      DateColumn dc = (DateColumn) table.column(c);
      entries[c] = options.dateFormatter().format(dc.get(r));
    } else if (dateTimeFormatter != null && columnType.equals(ColumnType.LOCAL_DATE_TIME)) {
      DateTimeColumn dc = (DateTimeColumn) table.column(c);
      entries[c] = options.dateTimeFormatter().format(dc.get(r));
    } else {
      if (options.usePrintFormatters()) {
        entries[c] = table.getString(r, c);
      } else {
        entries[c] = table.getUnformatted(r, c);
      }
    }
  }

  private void writeHeader(
      Table table, CsvWriteOptions options, com.univocity.parsers.csv.CsvWriter csvWriter) {
    if (options.header()) {
      String[] header = new String[table.columnCount()];
      for (int c = 0; c < table.columnCount(); c++) {
        String name = table.column(c).name();
        header[c] = options.columnNameMap().getOrDefault(name, name);
      }
      csvWriter.writeHeaders(header);
    }
  }

  protected static CsvWriterSettings createSettings(CsvWriteOptions options) {
    CsvWriterSettings settings = new CsvWriterSettings();
    // Sets the character sequence to write for the values that are null.
    settings.setNullValue(nullValue);
    if (options.separator() != null) {
      settings.getFormat().setDelimiter(options.separator());
    }
    if (options.quoteChar() != null) {
      settings.getFormat().setQuote(options.quoteChar());
    }
    if (options.escapeChar() != null) {
      settings.getFormat().setQuoteEscape(options.escapeChar());
    }
    if (options.lineEnd() != null) {
      settings.getFormat().setLineSeparator(options.lineEnd());
    }
    settings.setIgnoreLeadingWhitespaces(options.ignoreLeadingWhitespaces());
    settings.setIgnoreTrailingWhitespaces(options.ignoreTrailingWhitespaces());
    // writes empty lines as well.
    settings.setSkipEmptyLines(false);
    settings.setQuoteAllFields(options.quoteAllFields());
    return settings;
  }

  @Override
  public void write(Table table, Destination dest) {
    write(table, CsvWriteOptions.builder(dest).build());
  }
}
