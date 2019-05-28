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

import javax.annotation.concurrent.Immutable;

import com.univocity.parsers.csv.CsvWriterSettings;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

/**
 * Class that writes tables and individual columns to CSV files
 */
@Immutable
final public class CsvWriter implements DataWriter<CsvWriteOptions> {

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
            csvWriter = new com.univocity.parsers.csv.CsvWriter(options.destination().createWriter(), settings);

            if (options.header()) {
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

    protected static CsvWriterSettings createSettings(CsvWriteOptions options) {
        CsvWriterSettings settings = new CsvWriterSettings();
        // Sets the character sequence to write for the values that are null.
        settings.setNullValue(nullValue);
        settings.getFormat().setDelimiter(options.separator());
        settings.getFormat().setQuote(options.quoteChar());
        settings.getFormat().setQuoteEscape(options.escapeChar());
        settings.getFormat().setLineSeparator(options.lineEnd());
        // writes empty lines as well.
        settings.setSkipEmptyLines(false);
        return settings;
    }

    @Override
    public void write(Table table, Destination dest) {
        write(table, CsvWriteOptions.builder(dest).build());
    }
    
}

