package tech.tablesaw.io;

import static tech.tablesaw.api.ColumnType.*;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.univocity.parsers.common.AbstractParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;

public abstract class FileReader {

  private static Logger logger = LoggerFactory.getLogger(FileReader.class);
  private static final int UNLIMITED_SAMPLE_SIZE = -1;

  /**
   * Returns an array containing the inferred columnTypes for the file being read, as calculated by
   * the ColumnType inference logic. These types may not be correct.
   */
  public ColumnType[] getColumnTypes(
      Reader reader,
      ReadOptions options,
      int linesToSkip,
      AbstractParser<?> parser,
      String[] columnNames) {

    if (parser.getContext() == null) parser.beginParsing(reader);

    for (int i = 0; i < linesToSkip; i++) {
      parser.parseNext();
    }

    ColumnTypeDetector detector = new ColumnTypeDetector(options.columnTypesToDetect());

    ColumnType[] columnTypes =
        detector.detectColumnTypes(
            new Iterator<String[]>() {

              String[] nextRow = parser.parseNext();

              @Override
              public boolean hasNext() {
                return nextRow != null;
              }

              @Override
              public String[] next() {
                if (!hasNext()) {
                  throw new NoSuchElementException();
                }
                String[] tmp = nextRow;
                nextRow = parser.parseNext();
                return tmp;
              }
            },
            options);

    // If there are columnTypes configured by the user use them
    for (int i = 0; i < columnTypes.length; i++) {
      boolean hasColumnName = columnNames != null && i < columnNames.length;
      Optional<ColumnType> configuredColumnType =
          options.columnTypeReadOptions().columnType(i, hasColumnName ? columnNames[i] : null);
      if (configuredColumnType.isPresent()) {
        columnTypes[i] = configuredColumnType.get();
      }
    }

    return columnTypes;
  }

  private String cleanName(String name) {
    return name.trim();
  }

  /** Returns the column names for each column in the source. */
  public String[] getColumnNames(
      ReadOptions options,
      ReadOptions.ColumnTypeReadOptions columnTypeReadOptions,
      AbstractParser<?> parser) {

    if (options.header()) {

      String[] headerNames = parser.parseNext();

      // work around issue where Univocity returns null if a column has no header.
      for (int i = 0; i < headerNames.length; i++) {
        if (headerNames[i] == null) {
          headerNames[i] = "C" + i;
        } else {
          headerNames[i] = headerNames[i].trim();
        }
      }
      if (options.allowDuplicateColumnNames()) {
        renameDuplicateColumnHeaders(headerNames);
      }
      return headerNames;
    } else {
      // Placeholder column names for when the file read has no header
      int columnCount =
          columnTypeReadOptions.columnTypes() != null
              ? columnTypeReadOptions.columnTypes().length
              : 0;
      String[] headerNames = new String[columnCount];
      for (int i = 0; i < columnCount; i++) {
        headerNames[i] = "C" + i;
      }
      return headerNames;
    }
  }

  /**
   * Renames any column header that appears more than once. Subsequent appearances have "-[count]"
   * appended; For example, the first (or only) appearance of "foo" is named "foo", the second
   * appearance is named "foo-2" The header array is modified in place.
   *
   * @param headerNames The header names to be potentially adjusted.
   */
  private void renameDuplicateColumnHeaders(String[] headerNames) {
    Map<String, Integer> nameCounter = new HashMap<>();
    for (int i = 0; i < headerNames.length; i++) {
      String name = headerNames[i];
      Integer count = nameCounter.get(name.toLowerCase());
      if (count == null) {
        nameCounter.put(name.toLowerCase(), 1);
      } else {
        count++;
        nameCounter.put(name.toLowerCase(), count);
        headerNames[i] = name + "-" + count;
      }
    }
  }

  protected Table parseRows(
      ReadOptions options,
      boolean headerOnly,
      Reader reader,
      ReadOptions.ColumnTypeReadOptions columnTypeReadOptions,
      AbstractParser<?> parser) {
    return parseRows(
        options, headerOnly, reader, columnTypeReadOptions, parser, UNLIMITED_SAMPLE_SIZE);
  }

  protected Table parseRows(
      ReadOptions options,
      boolean headerOnly,
      Reader reader,
      ReadOptions.ColumnTypeReadOptions columnTypeReadOptions,
      AbstractParser<?> parser,
      int sampleSize) {
    parser.beginParsing(reader);
    Table table = Table.create(options.tableName());

    List<String> headerRow =
        Lists.newArrayList(getColumnNames(options, columnTypeReadOptions, parser));

    @SuppressWarnings({"UnstableApiUsage", "OptionalGetWithoutIsPresent"})
    ColumnType[] types =
        Streams.mapWithIndex(
                headerRow.stream(),
                (columnName, idx) -> columnTypeReadOptions.columnType((int) idx, columnName))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(ColumnType[]::new);

    for (int x = 0; x < types.length; x++) {
      if (types[x] != SKIP) {
        String columnName = cleanName(headerRow.get(x));
        if (Strings.isNullOrEmpty(columnName)) {
          columnName = "Column " + table.columnCount();
        }
        ColumnType type = types[x];
        Column<?> newColumn;
        newColumn = type.create(columnName);
        table.addColumns(newColumn);
      }
    }

    if (!headerOnly) {
      String[] columnNames = selectColumnNames(headerRow, types);
      int[] columnIndexes = new int[columnNames.length];
      for (int i = 0; i < columnIndexes.length; i++) {
        // get the index in the original table, which includes skipped fields
        columnIndexes[i] = headerRow.indexOf(columnNames[i]);
      }
      addRows(options, types, parser, table, columnIndexes, sampleSize);
    }

    return table;
  }

  private void addRows(
      ReadOptions options,
      ColumnType[] types,
      AbstractParser<?> reader,
      Table table,
      int[] columnIndexes,
      int sampleSize) {

    String[] nextLine;
    Map<String, AbstractColumnParser<?>> parserMap = getParserMap(options, table);

    Random random = new Random(0);
    // Add the rows
    for (int rowNumber = options.header() ? 1 : 0;
        (nextLine = reader.parseNext()) != null;
        rowNumber++) {
      // validation
      if (options.skipRowsWithInvalidColumnCount()
          && options.header()
          && nextLine.length != types.length) {
        continue;
      }
      if (nextLine.length < types.length) {
        if (nextLine.length == 1 && Strings.isNullOrEmpty(nextLine[0])) {
          logger.error("Warning: Invalid file. Row " + rowNumber + " is empty. Continuing.");
          continue;
        } else {
          Exception e =
              new IndexOutOfBoundsException(
                  "Row number "
                      + rowNumber
                      + " contains "
                      + nextLine.length
                      + " columns. "
                      + types.length
                      + " expected.");
          throw new AddCellToColumnException(e, 0, rowNumber, table.columnNames(), nextLine);
        }
      } else if (nextLine.length > types.length) {
        throw new IllegalArgumentException(
            "Row number "
                + rowNumber
                + " contains "
                + nextLine.length
                + " columns. "
                + types.length
                + " expected.");
      }

      int samplesCount = table.rowCount();
      if (sampleSize < 0 || samplesCount < sampleSize) {
        addValuesToColumns(table, columnIndexes, nextLine, parserMap, rowNumber, -1);
      } else {
        // find a row index to replace
        int randomIndex = random.nextInt(samplesCount + 1);
        // replace index if it is smaller than numSamples, otherwise ignore it.
        if (randomIndex < sampleSize) {
          addValuesToColumns(table, columnIndexes, nextLine, parserMap, rowNumber, randomIndex);
        }
      }
    }
  }

  private void addValuesToColumns(
      Table table,
      int[] columnIndexes,
      String[] nextLine,
      Map<String, AbstractColumnParser<?>> parserMap,
      int rowNumber,
      int rowIndex) {
    // append each column that we're including (not skipping)
    int cellIndex = 0;
    for (int columnIndex : columnIndexes) {
      Column<?> column = table.column(cellIndex);
      AbstractColumnParser<?> parser = parserMap.get(column.name());
      try {
        String value = nextLine[columnIndex];
        if (rowIndex >= 0) {
          column.set(rowIndex, value, parser);
        } else {
          column.appendCell(value, parser);
        }
      } catch (Exception e) {
        throw new AddCellToColumnException(
            e, columnIndex, rowNumber, table.columnNames(), nextLine);
      }
      cellIndex++;
    }
  }

  private Map<String, AbstractColumnParser<?>> getParserMap(ReadOptions options, Table table) {
    Map<String, AbstractColumnParser<?>> parserMap = new HashMap<>();
    for (Column<?> column : table.columns()) {
      AbstractColumnParser<?> parser = column.type().customParser(options);
      parserMap.put(column.name(), parser);
    }
    return parserMap;
  }

  /** Reads column names from header, skipping any for which the type == SKIP */
  private String[] selectColumnNames(List<String> names, ColumnType[] types) {
    List<String> header = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      if (types[i] != SKIP) {
        String name = names.get(i);
        name = name.trim();
        header.add(name);
      }
    }
    String[] result = new String[header.size()];
    return header.toArray(result);
  }

  protected String getTypeString(Table structure) {
    StringBuilder buf = new StringBuilder();
    buf.append("ColumnType[] columnTypes = {");
    buf.append(System.lineSeparator());

    Column<?> typeCol = structure.column("Column Type");
    Column<?> indxCol = structure.column("Index");
    Column<?> nameCol = structure.column("Column Name");

    // add the column headers
    int typeColIndex = structure.columnIndex(typeCol);
    int indxColIndex = structure.columnIndex(indxCol);
    int nameColIndex = structure.columnIndex(nameCol);

    int typeColWidth = typeCol.columnWidth();
    int indxColWidth = indxCol.columnWidth();
    int nameColWidth = nameCol.columnWidth();

    final char padChar = ' ';
    for (int r = 0; r < structure.rowCount(); r++) {
      String cell = Strings.padEnd(structure.get(r, typeColIndex) + ",", typeColWidth, padChar);
      buf.append(cell);
      buf.append(" // ");

      cell = Strings.padEnd(structure.getUnformatted(r, indxColIndex), indxColWidth, padChar);
      buf.append(cell);
      buf.append(' ');

      cell = Strings.padEnd(structure.getUnformatted(r, nameColIndex), nameColWidth, padChar);
      buf.append(cell);
      buf.append(' ');

      buf.append(System.lineSeparator());
    }
    buf.append("}");
    buf.append(System.lineSeparator());
    return buf.toString();
  }
}
