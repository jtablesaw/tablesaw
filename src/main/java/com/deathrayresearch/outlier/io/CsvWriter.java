package com.deathrayresearch.outlier.io;

import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.Column;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Builds complex ColumnTables form other data sources. *
 */
final public class CsvWriter {

  /**
   * Private constructor to prevent instantiation
   */
  private CsvWriter() {
  }

  public static void write(String fileName, Relation table) throws IOException {
    write(fileName, table, null);
  }

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
        Object cell = table.get(c, r);
        String valueString = String.valueOf(cell);
        entries[c] = valueString;
      }
      writer.writeNext(entries);
    }
    writer.close();
  }

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
