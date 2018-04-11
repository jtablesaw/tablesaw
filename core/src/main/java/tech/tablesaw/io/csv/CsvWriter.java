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

import com.opencsv.CSVWriter;
import tech.tablesaw.api.Table;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;

/**
 * Static utility class that writes tables and individual columns to CSV files
 */
@Immutable
final public class CsvWriter {

    /**
     * Private constructor to prevent instantiation
     */
    private CsvWriter() {}

    /**
     * Writes the given table to a file
     *
     * @throws IOException if the write fails
     */
    public static void write(Table table, CsvWriteOptions options) throws IOException {

        try (CSVWriter csvWriter = new CSVWriter(options.writer(),
                        options.separator(),
                        options.quoteChar(),
                        options.escapeChar(),
                        options.lineEnd())) {

            if (options.header()) {
                String[] header = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    header[c] = table.column(c).name();
                }
                csvWriter.writeNext(header);
            }
            for (int r = 0; r < table.rowCount(); r++) {
                String[] entries = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    table.get(r, c);
                    entries[c] = table.get(r, c);
                }
                csvWriter.writeNext(entries);
            }
        }
    }
}
