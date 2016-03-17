package com.deathrayresearch.outlier.io;

import au.com.bytecode.opencsv.CSVReader;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnType;
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
 * Builds Tables from Comma Separated Value (CSV) files.
 *
 * TODO(lwhite): Change header param from a boolean to an int, representing the number of lines for the header
 * TODO(lwhite): Add multi-file read methods that take header, separator, and maybe, wanted as params
 */
final public class CsvReader {

  /**
   * Private constructor to prevent instantiation
   */
  private CsvReader() {}

  /**
   * Constructs and returns a table from one or more CSV files, all containing the same column types
   * <p>
   * This constructor assumes the files have a one-line header, which is used to populate the column names,
   * and that they use a comma to separate between columns.
   * @throws IOException If there is an issue reading any of the files
   */
  public static Table read(ColumnType types[], String ... fileNames) throws IOException {
    if (fileNames.length == 1) {
      return read(types, true, ',', fileNames[0]);
    } else {
      Table table = read(types, true, ',', fileNames[0]);
      for (int i = 1; i < fileNames.length; i++) {
        String fileName = fileNames[i];
        table.append(read(types, true, ',', fileName));
      }
      return table;
    }
  }

  /**
   * Constructs a {@link com.deathrayresearch.outlier.Table} from a CSV file. This constructor
   * assumes that the file has a one-line header, which is used to populate the column names.
   */
  public static Table read(ColumnType types[], char delimiter, String fileName) throws IOException {
    return read(types, true, delimiter, fileName);
  }

  /**
   * Returns a Table constructed from a CSV File.
   *
   * @param types    An array of the types of columns in the file, in the order they appear
   * @param header   Is the first row in the file a header?
   * @param fileName The fully specified file name
   * @return A Table containing the data in the csv file.
   * @throws IOException
   */
  public static Table read(ColumnType types[], boolean header, String fileName)
      throws IOException {
    return read(types, header, ',', fileName);
  }

  /**
   * Returns a Table constructed from a CSV File.
   *
   * @param types    An array of the types of columns in the file, in the order they appear
   * @param header   Is the first row in the file a header?
   * @param columnSeparator The character used to separate the columns in the input file
   * @param fileName The fully specified file name
   * @return A Table containing the data in the csv file.
   * @throws IOException
   */
  public static Table read(ColumnType types[], boolean header, int[] wanted, char columnSeparator, String fileName)
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

    return read(newTypes, header, columnSeparator, fileName);
  }

  /**
   * Constructs a Table from a CSV File.
   *
   * @param types           An array of the types of columns in the file, in the order they appear
   * @param header          Is the first row in the file a header?
   * @param columnSeparator the delimiter
   * @param fileName        The fully specified file name
   * @return A Table containing the data in the csv file.
   * @throws IOException
   */
  public static Table read(ColumnType types[], boolean header, char columnSeparator, String fileName)
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
    int[] columnIndexes = new int[columnNames.length];
    for (int i = 0; i < columnIndexes.length; i++) {
      // get the index in the original table, which includes skipped fields
      columnIndexes[i] = headerRow.indexOf(columnNames[i]);
    }
    // Add the rows
    String[] nextLine;
    while (it.hasNext()) {
      nextLine = it.next();
      // for each column that we're including (not skipping)
      int cellIndex = 0;
      for (int columnIndex : columnIndexes) {
        Column column = table.column(cellIndex);
        column.addCell(nextLine[columnIndex]);
        cellIndex++;
      }
    }
    it.close();
    return table;
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
