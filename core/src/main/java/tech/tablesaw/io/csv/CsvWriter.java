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
import tech.tablesaw.api.Table;

import javax.annotation.concurrent.Immutable;
import java.io.Writer;

/**
 * Class that writes tables and individual columns to CSV files
 */
@Immutable
final public class CsvWriter {

    private static final String nullValue = "";

    private final Table table;
    private final boolean header;
    private final Writer writer;
    private final CsvWriterSettings settings;

    /**
     * Private constructor to prevent instantiation
     */
    public CsvWriter(Table table, CsvWriteOptions options) {
        this.table = table;
        this.header = options.header();
        this.writer = options.writer();

        this.settings = new CsvWriterSettings();
        // Sets the character sequence to write for the values that are null.
        settings.setNullValue(nullValue);
        settings.getFormat().setDelimiter(options.separator());
        settings.getFormat().setQuote(options.quoteChar());
        settings.getFormat().setQuoteEscape(options.escapeChar());
        settings.getFormat().setLineSeparator(options.lineEnd());
        // writes empty lines as well.
        settings.setSkipEmptyLines(false);

    }

    public void write() {

        com.univocity.parsers.csv.CsvWriter csvWriter = null;
        // Creates a writer with the above settings;
        try {

            csvWriter = new com.univocity.parsers.csv.CsvWriter(writer, settings);
            if (header) {
                String[] header = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    header[c] = table.column(c).name();
                }
                csvWriter.writeHeaders(header);
            }
            for (int r = 0; r < table.rowCount(); r++) {
                String[] entries = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    table.get(r, c);
                    entries[c] = table.getUnformatted(r, c);
                }
                csvWriter.writeRow(entries);
            }
        } finally {
            if (csvWriter != null) {
                csvWriter.flush();
                csvWriter.close();
            }
        }
    }

    public String getNullValue() {
        return settings.getNullValue();
    }

    public Table getTable() {
        return table;
    }

    public char getQuoteCharacter() {
        return settings.getFormat().getQuote();
    }

    public boolean getHeader() {
        return header;
    }

    public char getEscapeChar() {
        return settings.getFormat().getQuoteEscape();
    }

    public char getSeparator() {
        return settings.getFormat().getDelimiter();
    }

    public String getLineEnd() {
        return new String(settings.getFormat().getLineSeparator());
    }
}

