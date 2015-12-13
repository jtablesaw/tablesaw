package com.deathrayresearch.outlier.io;

import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.Table;

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

  public static void write(String fileName, Table table) throws IOException {
    write(fileName, table, null);
  }

  public static void write(String fileName, Table table, String missing) throws IOException {
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
        if (cell == null || valueString.equals("null")) {
          cell = missing;
        }
        entries[c] = valueString;
      }
      writer.writeNext(entries);
    }
    writer.close();
  }
}
