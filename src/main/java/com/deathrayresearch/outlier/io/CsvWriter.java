package com.deathrayresearch.outlier.io;

import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.Column;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes Tables and individual columns to CSV files
 *
 * TODO(lwhite): Do something with the missing indicator param in write() method
 * TODO(lwhite): Add a missing indicator to the column write method, plus a method defining a default missing indicator
 */
final public class CsvWriter {

  /**
   * Private constructor to prevent instantiation
   */
  private CsvWriter() {}

  /**
   * Writes the given table to a file with the given filename
   * @throws IOException
   */
  public static void write(String fileName, Relation table) throws IOException {
    write(fileName, table, null);
  }

  /**
   * Writes the given table to a file with the given filename, using the given string to represent missing data
   * @throws IOException
   */
  public static void write(String fileName, Relation table, String missing) throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(fileName));
    String[] header = new String[table.columnCount()];
    for (int c = 0; c < table.columnCount(); c++) {
      header[c] = table.column(c).name();
    }
    writer.writeNext(header);
    for (int r = 0; r < table.rowCount(); r++) {
      String[] entries = new String[table.columnCount()];
      for (int c = 0; c < table.columnCount(); c++) {
        String valueString = table.get(c, r);
        entries[c] = valueString;
      }
      writer.writeNext(entries);
    }
    writer.close();
  }

  /**
   * Writes the given column to a file with the given fileName as a single column CSV file
   * @throws IOException
   */
  public static void write(String fileName, Column column) throws IOException {
    CSVWriter writer = new CSVWriter(new FileWriter(fileName));
    String[] header = {column.name()};
    writer.writeNext(header);

    for (int r = 0; r < column.size(); r++) {
      String[] entries = {column.getString(r)};
      writer.writeNext(entries);
    }
    writer.close();
  }
}
