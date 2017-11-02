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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.annotation.concurrent.Immutable;

import com.opencsv.CSVWriter;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Static utility class that writes tables and individual columns to CSV files
 * <p>
 * TODO(lwhite): Do something with the missing indicator param in write() method
 * TODO(lwhite): Add a missing indicator to the column write method, plus a method defining a default missing indicator
 */
@Immutable
final public class CsvWriter {

    /**
     * Private constructor to prevent instantiation
     */
    private CsvWriter() {
    }

    /**
     * Writes the given table to the given file
     *
     * @throws IOException if the write fails
     */
    public static void write(Table table, Writer writer) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] header = new String[table.columnCount()];
            for (int c = 0; c < table.columnCount(); c++) {
                header[c] = table.column(c).name();
            }
            csvWriter.writeNext(header, false);
            for (int r = 0; r < table.rowCount(); r++) {
                String[] entries = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    table.get(r, c);
                    entries[c] = table.get(r, c);
                }
                csvWriter.writeNext(entries, false);
            }
        }
    }

    /**
     * Writes the given table to a file with the given filename
     *
     * @throws IOException if the write fails
     */
    public static void write(Table table, OutputStream stream) throws IOException {
       write(table, new OutputStreamWriter(stream));    
    }

    /**
     * Writes the given table to athe given file
     *
     * @throws IOException if the write fails
     */
    public static void write(Table table, File file) throws IOException {
       write(table, new FileWriter(file));    
    }

    /**
     * Writes the given table to a file with the given filename
     *
     * @throws IOException if the write fails
     */
    public static void write(Table table, String fileName) throws IOException {
       write(table, new File(fileName));    
    }

    /**
     * Writes the given column to a file with the given fileName as a single column CSV file
     *
     * @throws IOException if the write fails
     */
    public static void write(String fileName, Column column) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {column.name()};
            writer.writeNext(header, false);

            for (int r = 0; r < column.size(); r++) {
                String[] entries = {column.getString(r)};
                writer.writeNext(entries, false);
            }
        }
    }
}
