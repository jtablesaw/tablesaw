package com.deathrayresearch.outlier.io;

import au.com.bytecode.opencsv.CSVReader;
import com.deathrayresearch.outlier.*;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds complex ColumnTables from Comma Separated Value (CSV) files.
 */
final public class CsvReader {

  /**
   * Private constructor to prevent instantiation
   */
  private CsvReader() {
  }

  /**
   * Constructs a {@link com.deathrayresearch.outlier.Table} from a CSV file. This constructor
   * assumes that the file has a one-row header file, which is used to populate the column names.
   */
  public static Table read(String fileName, ColumnType types[]) throws IOException {
    return read(fileName, types, ',', true);
  }

  /**
   * Constructs a {@link com.deathrayresearch.outlier.Table} from a CSV file. This constructor
   * assumes that the file has a one-row header file, which is used to populate the column names.
   */
  public static Table read(String fileName, ColumnType types[], char delimiter) throws IOException {
    return read(fileName, types, delimiter, true);
  }

  /**
   * Constructs a Table from a CSV File.
   *
   * @param fileName The fully specified file name
   * @param types    An array of the types of columns in the file, in the order they appear
   * @param header   Is the first row in the file a header?
   * @return A Table containing the data in the csv file.
   * @throws IOException
   */
  public static Table read(String fileName, ColumnType types[], boolean header)
      throws IOException {
     return read(fileName, types, ',', header);
  }

  public static Table read(String fileName,
                           ColumnType types[],
                           int[] wanted,
                           char columnSeparator,
                           boolean header)
      throws IOException {

    CSVReader reader = new CSVReader(new FileReader(fileName));

    // Add the rows
    String[] nextLine;
    nextLine = reader.readNext();
    reader.close();

    ColumnType[] newTypes = new ColumnType[nextLine.length];
    for (int i = 0; i < nextLine.length; i++) {
      newTypes[i] = ColumnType.SKIP;
    }

    for (int j = 0; j < wanted.length; j++) {
      int columnNumber = wanted[j];
      newTypes[columnNumber] = types[j];
    }

    return read(fileName, newTypes, columnSeparator, header);
  }

    /**
     * Constructs a Table from a CSV File.
     *
     * @param fileName        The fully specified file name
     * @param types           An array of the types of columns in the file, in the order they appear
     * @param columnSeparator the delimiter
     * @param header          Is the first row in the file a header?
     * @return A Table containing the data in the csv file.
     * @throws IOException
     */
  public static Table read(String fileName,
                           ColumnType types[],
                           char columnSeparator,
                           boolean header)
      throws IOException {

    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.builder().setColumnSeparator(columnSeparator).build();
    mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    File csvFile = new File(fileName);
    MappingIterator<String[]> it = mapper.reader(String[].class).with(schema).readValues(csvFile);

    String[] columnNames;
    List<String> headerRow;
    if (header) {
      headerRow = Lists.newArrayList(it.next());
      columnNames = selectColumnNames(headerRow, types);
    } else {
      columnNames = makeColumnNames(types);
      headerRow = Lists.newArrayList(columnNames);
    }
    Table table = new Table(fileName);
    for (int x = 0; x < types.length; x++) {
      if (types[x] != ColumnType.SKIP) {
        Column newColumn = TypeUtils.newColumn(headerRow.get(x), types[x]);
        table.addColumn(newColumn);
      }
    }
    // Add the rows
    String[] nextLine;
    while (it.hasNext()) {
      nextLine = it.next();
      // for each column that we're including (not skipping)
      int cellIndex = 0;
      for (String columnName : columnNames) {
        // get the index in the original table, which includes skipped fields
        int columnIndex = headerRow.indexOf(columnName);
        Column column = table.column(cellIndex);
        if (column.type() == ColumnType.FLOAT) {
          FloatColumn fc = (FloatColumn) column;
          fc.addCell(nextLine[columnIndex]);
        } else if (column.type() == ColumnType.INTEGER) {
          IntColumn ic = (IntColumn) column;
          ic.addCell(nextLine[columnIndex]);
        } else if (column.type() == ColumnType.CAT) {
          CategoryColumn cc = (CategoryColumn) column;
          cc.addCell(nextLine[columnIndex]);
        } else if (column.type() == ColumnType.LOCAL_TIME) {
          LocalTimeColumn bc = (LocalTimeColumn) column;
          bc.addCell(nextLine[columnIndex]);
        } else if (column.type() == ColumnType.LOCAL_DATE) {
          LocalDateColumn bc = (LocalDateColumn) column;
          bc.addCell(nextLine[columnIndex]);
        } else if (column.type() == ColumnType.TEXT) {
          TextColumn tc = (TextColumn) column;
          tc.addCell(nextLine[columnIndex]);
        }else if (column.type() == ColumnType.LOCAL_DATE_TIME) {
          LocalDateTimeColumn dtc = (LocalDateTimeColumn) column;
          dtc.addCell(nextLine[columnIndex]);
        } else if (column.type() == ColumnType.BOOLEAN) {
          BooleanColumn bc = (BooleanColumn) column;
          bc.addCell(nextLine[columnIndex]);
        }
        cellIndex++;
      }
    }
    it.close();
    return table;
  }

  public static void read(String inputFileName,
                               List<Integer> columns,
                               boolean header)
      throws IOException {

    CSVReader reader = new CSVReader(new FileReader(inputFileName));

    // Add the rows
    String[] nextLine;
    String[] newLine = new String[columns.size()];
    while ((nextLine = reader.readNext()) != null) {
      for (int i = 0; i < columns.size(); i++) {
        newLine[i] = nextLine[columns.get(i)];
      }
    }
    reader.close();
  }

  /**
   * Provides placeholder column names for when the file read has no header
   */
  private static String[] makeColumnNames(ColumnType types[]) {
    String[] header = new String[types.length];
    for (int i = 0; i < types.length; i++) {
      header[i] = "C" + i;
    }
    return header;
  }

  /**
   * Reads column names from header, skipping any for which the type == SKIP
   */
  private static String[] selectColumnNames(List<String> names, ColumnType types[]) {
    List<String> header = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      if (types[i] != ColumnType.SKIP) {
        header.add(names.get(i));
      }
    }
    String[] result = new String[header.size()];
    return header.toArray(result);
  }
}
