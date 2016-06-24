package com.github.lwhite1.tablesaw.io;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Static utility class that Builds Tables from Comma Separated Value (CSV) files.
 * <p>
 * TODO(lwhite): Change header param from a boolean to an int, representing the number of lines for the header
 * TODO(lwhite): Add multi-file read methods that take header, separator, and maybe, wanted as params
 */
@Immutable
final public class CsvReader {

  /**
   * Private constructor to prevent instantiation
   */
  private CsvReader() {
  }

  /**
   * Constructs and returns a table from one or more CSV files, all containing the same column types
   * <p>
   * This constructor assumes the files have a one-line header, which is used to populate the column names,
   * and that they use a comma to separate between columns.
   *
   * @throws IOException If there is an issue reading any of the files
   */
  public static Table read(ColumnType types[], String... fileNames) throws IOException {
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
   * Constructs a {@link Table} from a CSV file. This constructor
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
   * @param types           An array of the types of columns in the file, in the order they appear
   * @param header          Is the first row in the file a header?
   * @param columnSeparator The character used to separate the columns in the input file
   * @param fileName        The fully specified file name
   * @return A Table containing the data in the csv file.
   * @throws IOException
   */
  public static Table read(ColumnType types[], boolean header, int[] wanted, char columnSeparator, String fileName)
      throws IOException {

    String[] nextLine;
    ColumnType[] newTypes;
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      // Add the rows
      nextLine = reader.readNext();
    }

    newTypes = new ColumnType[nextLine.length];
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
   * Returns a Table constructed from a CSV File with the given file name
   *
   * The @code{fileName} is used as the initial table name for the new table
   *
   * @param types           An array of the types of columns in the file, in the order they appear
   * @param header          Is the first row in the file a header?
   * @param columnSeparator the delimiter
   * @param fileName        The fully specified file name. It is used to provide a default name for the table
   * @return A Table containing the data in the csv file.
   * @throws IOException
   */
  public static Table read(ColumnType types[], boolean header, char columnSeparator, String fileName)
      throws IOException {

    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.builder().setColumnSeparator(columnSeparator).build();
    mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    File csvFile = new File(fileName);
    Table table;
    try (MappingIterator<String[]> it = mapper.reader(String[].class).with(schema).readValues(csvFile)) {

      String[] columnNames;
      List<String> headerRow;
      if (header) {
        headerRow = Lists.newArrayList(it.next());
        columnNames = selectColumnNames(headerRow, types);
      } else {
        columnNames = makeColumnNames(types);
        headerRow = Lists.newArrayList(columnNames);
      }

      table = new Table(nameMaker(fileName));
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
    }
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

  private static String nameMaker(String path) {
    Path p = Paths.get(path);
    return p.getFileName().toString();
  }

  public static Table read(String fileName) throws IOException {
    ColumnType[] columnTypes = detectColumnTypes(fileName, true, ',');
    return read(columnTypes, true, fileName);
  }

  @VisibleForTesting
  static ColumnType[] detectColumnTypes(String file, boolean header, char delimiter)
      throws IOException {

    int linesToSkip = header ? 1 : 2;
    final int maxRows = 100;
    // Read the first 100 rows and guess
    // TODO(lwhite): Could we read the last 100 rows to double check?

    // to hold the results
    List<ColumnType> columnTypes = new ArrayList<>();

    // to hold the data read from the file
    List<List<String>> columnData = new ArrayList<>();

    int rowCount = 0; // make sure we don't go over maxRows
    try (CSVReader reader = new CSVReader(new FileReader(file), delimiter, '"', linesToSkip)) {
      String[] nextLine;
      while ((nextLine = reader.readNext()) != null && rowCount < maxRows) {
        int columnNumber = 0;
        for (String field : nextLine) {
          if (rowCount == 0) {
            columnData.add(new ArrayList<>());
            //continue; // TODO(lwhite): Better way to handle header
          }
          columnData.get(columnNumber).add(field);
          columnNumber ++;
        }
        rowCount++;
      }
    }

    // now detect
    for (List<String> valuesList : columnData) {
      ColumnType detectedType = detectType(valuesList);
      columnTypes.add(detectedType);
    }

    return columnTypes.toArray(new ColumnType[columnTypes.size()]);
  }

  private static ColumnType detectType(List<String> valuesList) {

    ColumnType[] types = ColumnType.values();
    for (String s : valuesList) {
      if (isLocalDateTime.test(s)) {
        return ColumnType.LOCAL_DATE_TIME;
      }
      if (isLocalTime.test(s)) {
        return ColumnType.LOCAL_TIME;
      }
      if (isLocalDate.test(s)) {
        return ColumnType.LOCAL_DATE;
      }
      if (isBoolean.test(s)) {
        return ColumnType.BOOLEAN;
      }
      if (isShort.test(s)){
        return ColumnType.SHORT_INT;
      }
      if (isInteger.test(s)) {
        return ColumnType.INTEGER;
      }
      if (isLong.test(s)) {
        return ColumnType.LONG_INT;
      }
      if (isFloat.test(s)) {
        return ColumnType.FLOAT;
      }
    }
    return ColumnType.CATEGORY;
  }

  private static java.util.function.Predicate<String> isBoolean = s ->
      TypeUtils.TRUE_STRINGS.contains(s) || TypeUtils.FALSE_STRINGS.contains(s);

  private static Predicate<String> isLong = new Predicate<String>() {

    @Override
    public boolean test(@Nullable String s) {
      try {
        Long.parseLong(s);
        return true;
      } catch (NumberFormatException e) {
        // it's all part of the plan
        return false;
      }
    }
  };

  private static Predicate<String> isInteger = s -> {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  };

  private static Predicate<String> isFloat = s -> {
    try {
      Float.parseFloat(s);
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  };

  private static Predicate<String> isShort = s -> {
    try {
      Short.parseShort(s);
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  };

  private static Predicate<String> isLocalDate = s -> {
    try {
      LocalDate.parse(s, TypeUtils.DATE_FORMATTER);
      return true;
    } catch (DateTimeParseException e) {
      // it's all part of the plan
      return false;
    }
  };

  private static Predicate<String> isLocalTime = s -> {
    try {
      LocalTime.parse(s, TypeUtils.TIME_FORMATTER);
      return true;
    } catch (DateTimeParseException e) {
      // it's all part of the plan
      return false;
    }
  };

  private static Predicate<String> isLocalDateTime = s -> {
    try {
      LocalDateTime.parse(s, TypeUtils.DATE_TIME_FORMATTER);
      return true;
    } catch (DateTimeParseException e) {
      // it's all part of the plan
      return false;
    }
  };
}
