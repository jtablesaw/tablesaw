package tech.tablesaw.io.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    public static void write(Table table, File file) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            String[] header = new String[table.columnCount()];
            for (int c = 0; c < table.columnCount(); c++) {
                header[c] = table.column(c).name();
            }
            writer.writeNext(header, false);
            for (int r = 0; r < table.rowCount(); r++) {
                String[] entries = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    table.get(r, c);
                    entries[c] = table.get(r, c);
                }
                writer.writeNext(entries, false);
            }
        }
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
