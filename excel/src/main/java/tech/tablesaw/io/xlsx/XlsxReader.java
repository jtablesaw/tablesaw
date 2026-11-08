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

package tech.tablesaw.io.xlsx;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import com.google.common.collect.Iterables;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.format.CellGeneralFormatter;
import org.apache.poi.ss.format.CellNumberFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.RuntimeIOException;
import tech.tablesaw.io.Source;

@Immutable
public class XlsxReader implements DataReader<XlsxReadOptions> {
  private static final Logger logger = LoggerFactory.getLogger(XlsxReader.class);
  private static final XlsxReader INSTANCE = new XlsxReader();

  static {
    register(Table.defaultReaderRegistry);
  }

  public static void register(ReaderRegistry registry) {
    registry.registerExtension("xlsx", INSTANCE);
    registry.registerMimeType(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", INSTANCE);
    registry.registerOptions(XlsxReadOptions.class, INSTANCE);
  }

  @Override
  public Table read(XlsxReadOptions options) {
    List<Table> tables = null;
    try {
      tables = readMultiple(options, true);
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    if (options.sheetIndex() != null) {
      int index = options.sheetIndex();
      if (index < 0 || index >= tables.size()) {
        throw new IndexOutOfBoundsException(
            String.format("Sheet index %d outside bounds. %d sheets found.", index, tables.size()));
      }

      Table table = tables.get(index);
      if (table == null) {
        throw new IllegalArgumentException(
            String.format("No table found at sheet index %d.", index));
      }
      return table;
    }
    // since no specific sheetIndex asked, return first table
    return tables.stream()
        .filter(t -> t != null)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No tables found."));
  }

  public List<Table> readMultiple(XlsxReadOptions options) throws IOException {
    return readMultiple(options, false);
  }

  /**
   * Read at most a table from every sheet.
   *
   * @param includeNulls include nulls for sheets without a table
   * @return a list of tables, at most one for every sheet
   */
  protected List<Table> readMultiple(XlsxReadOptions options, boolean includeNulls)
      throws IOException {
    byte[] bytes = null;
    InputStream input = getInputStream(options, bytes);
    List<Table> tables = new ArrayList<>();
    try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {
      for (Sheet sheet : workbook) {
        TableRange tableArea = findTableArea(sheet);
        if (tableArea != null) {
          Table table = createTable(sheet, tableArea, options);
          tables.add(table);
        } else if (includeNulls) {
          tables.add(null);
        }
      }
      return tables;
    } finally {
      if (options.source().reader() == null) {
        // if we get a reader back from options it means the client opened it, so let
        // the client close it
        // if it's null, we close it here.
        input.close();
      }
    }
  }

  private Boolean isBlank(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        if (cell.getRichStringCellValue().length() > 0) {
          return false;
        }
        break;
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)
            ? cell.getDateCellValue() != null
            : cell.getNumericCellValue() != 0) {
          return false;
        }
        break;
      case BOOLEAN:
        if (cell.getBooleanCellValue()) {
          return false;
        }
        break;
      case BLANK:
        return true;
      default:
        break;
    }
    return null;
  }

  private static class TableRange {
    private int startRow, endRow, startColumn, endColumn;

    TableRange(int startRow, int endRow, int startColumn, int endColumn) {
      this.startRow = startRow;
      this.endRow = endRow;
      this.startColumn = startColumn;
      this.endColumn = endColumn;
    }

    public int getColumnCount() {
      return endColumn - startColumn + 1;
    }
  }

  private TableRange findTableArea(Sheet sheet) {
    // find first row and column with contents
    int row1 = -1;
    int row2 = -1;
    TableRange lastRowArea = null;
    for (Row row : sheet) {
      TableRange rowArea = findRowArea(row);
      if (lastRowArea == null && rowArea != null) {
        if (row1 < 0) {
          lastRowArea = rowArea;
          row1 = row.getRowNum();
          row2 = row1;
        }
      } else if (lastRowArea != null && rowArea == null) {
        if (row2 > row1) {
          break;
        } else {
          row1 = -1;
        }
      } else if (lastRowArea == null && rowArea == null) {
        row1 = -1;
      } else if (rowArea.startColumn < lastRowArea.startColumn
          || rowArea.endColumn > lastRowArea.endColumn) {
        lastRowArea = null;
        row2 = -1;
      } else {
        row2 = row.getRowNum();
      }
    }
    return row1 >= 0 && lastRowArea != null
        ? new TableRange(row1, row2, lastRowArea.startColumn, lastRowArea.endColumn)
        : null;
  }

  private TableRange findRowArea(Row row) {
    int col1 = -1;
    int col2 = -1;
    for (Cell cell : row) {
      Boolean blank = isBlank(cell);
      if (col1 < 0 && Boolean.FALSE.equals(blank)) {
        col1 = cell.getColumnIndex();
        col2 = col1;
      } else if (col1 >= 0 && col2 >= col1) {
        if (Boolean.FALSE.equals(blank)) {
          col2 = cell.getColumnIndex();
        } else if (Boolean.TRUE.equals(blank)) {
          break;
        }
      }
    }
    return col1 >= 0 && col2 >= col1 ? new TableRange(0, 0, col1, col2) : null;
  }

  private InputStream getInputStream(XlsxReadOptions options, byte[] bytes)
      throws FileNotFoundException {
    if (bytes != null) {
      return new ByteArrayInputStream(bytes);
    }
    if (options.source().inputStream() != null) {
      return options.source().inputStream();
    }
    return new FileInputStream(options.source().file());
  }

  private Table createTable(Sheet sheet, TableRange tableArea, XlsxReadOptions options) {
    Optional<List<String>> optHeaderNames = getHeaderNames(sheet, tableArea);
    optHeaderNames.ifPresent(h -> tableArea.startRow++);
    List<String> headerNames = optHeaderNames.orElse(calculateDefaultColumnNames(tableArea));

    Table table = Table.create(options.tableName() + "#" + sheet.getSheetName());
    List<Column<?>> columns = new ArrayList<>(Collections.nCopies(headerNames.size(), null));
    for (int rowNum = tableArea.startRow; rowNum <= tableArea.endRow; rowNum++) {
      Row row = sheet.getRow(rowNum);
      for (int colNum = 0; colNum < headerNames.size(); colNum++) {
        int excelColNum = colNum + tableArea.startColumn;
        Cell cell = row.getCell(excelColNum, MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Column<?> column = columns.get(colNum);
        String columnName = headerNames.get(colNum);
        if (cell != null) {
          if (column == null) {
            column = createColumn(colNum, columnName, sheet, excelColNum, tableArea, options);
            columns.set(colNum, column);
            while (column.size() < rowNum - tableArea.startRow) {
              column.appendMissing();
            }
          }
          Column<?> altColumn = appendValue(column, cell);
          if (altColumn != null && altColumn != column) {
            column = altColumn;
            columns.set(colNum, column);
          }
        } else {
          boolean hasCustomizedType =
              options.columnTypeReadOptions().columnType(colNum, columnName).isPresent();
          if (column == null && hasCustomizedType) {
            ColumnType columnType =
                options.columnTypeReadOptions().columnType(colNum, columnName).get();
            column = columnType.create(columnName).appendMissing();
            columns.set(colNum, column);
          } else if (hasCustomizedType) {
            column.appendMissing();
          }
        }
        if (column != null) {
          while (column.size() <= rowNum - tableArea.startRow) {
            column.appendMissing();
          }
        }
      }
    }
    columns.removeAll(Collections.singleton(null));
    table.addColumns(columns.toArray(new Column<?>[columns.size()]));
    return table;
  }

  private Optional<List<String>> getHeaderNames(Sheet sheet, TableRange tableArea) {
    // assume header row if all cells are of type String
    Row row = sheet.getRow(tableArea.startRow);
    List<String> headerNames =
        IntStream.range(tableArea.startColumn, tableArea.endColumn + 1)
            .mapToObj(row::getCell)
            .filter(cell -> cell.getCellType() == STRING)
            .map(cell -> cell.getRichStringCellValue().getString())
            .collect(Collectors.toList());
    return headerNames.size() == tableArea.getColumnCount()
        ? Optional.of(headerNames)
        : Optional.empty();
  }

  private List<String> calculateDefaultColumnNames(TableRange tableArea) {
    return IntStream.range(tableArea.startColumn, tableArea.endColumn + 1)
        .mapToObj(i -> "col" + i)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Column<?> appendValue(Column<?> column, Cell cell) {
    CellType cellType =
        cell.getCellType() == FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
    switch (cellType) {
      case STRING:
        column.appendCell(cell.getRichStringCellValue().getString());
        return null;
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          Date date = cell.getDateCellValue();
          // This will return inconsistent results across time zones, but that matches Excel's
          // behavior
          LocalDateTime localDate =
              date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
          if (column.type() == ColumnType.STRING) {
            // If column has String type try to honor it and leave the value as an string as similar
            // as posible as seen in Excel
            String dataFormatStyle = cell.getCellStyle().getDataFormatString();
            String val;
            if ("general".equalsIgnoreCase(dataFormatStyle)) {
              val = new CellGeneralFormatter().format(cell.getNumericCellValue());
            } else {
              val = new CellDateFormatter(dataFormatStyle).format(cell.getDateCellValue());
            }
            column.appendCell(val);
          } else {
            column.appendCell(localDate.toString());
          }
          return null;
        } else {
          double num = cell.getNumericCellValue();
          if (column.type() == ColumnType.INTEGER) {
            Column<Integer> intColumn = (Column<Integer>) column;
            if ((int) num == num) {
              intColumn.append((int) num);
              return null;
            } else if ((long) num == num) {
              Column<Long> altColumn = LongColumn.create(column.name(), column.size());
              altColumn = intColumn.mapInto(s -> (long) s, altColumn);
              altColumn.append((long) num);
              return altColumn;
            } else {
              Column<Double> altColumn = DoubleColumn.create(column.name(), column.size());
              altColumn = intColumn.mapInto(s -> (double) s, altColumn);
              altColumn.append(num);
              return altColumn;
            }
          } else if (column.type() == ColumnType.LONG) {
            Column<Long> longColumn = (Column<Long>) column;
            if ((long) num == num) {
              longColumn.append((long) num);
              return null;
            } else {
              Column<Double> altColumn = DoubleColumn.create(column.name(), column.size());
              altColumn = longColumn.mapInto(s -> (double) s, altColumn);
              altColumn.append(num);
              return altColumn;
            }
          } else if (column.type() == ColumnType.DOUBLE) {
            Column<Double> doubleColumn = (Column<Double>) column;
            doubleColumn.append(num);
            return null;
          } else if (column.type() == ColumnType.STRING) {
            // If column has String type try to honor it and leave the value as an string as similar
            // as posible as seen in Excel
            Column<String> stringColumn = (Column<String>) column;
            String dataFormatStyle = cell.getCellStyle().getDataFormatString();
            String val;
            try {
              if ("general".equalsIgnoreCase(dataFormatStyle)) {
                val = new CellGeneralFormatter().format(cell.getNumericCellValue());
              } else {
                val = new CellNumberFormatter(dataFormatStyle).format(cell.getNumericCellValue());
              }
            } catch (IllegalFormatException e) {
              logger.warn(
                  "Error formatting cell value {} to string: {}. Ignoring it's value",
                  cell.getNumericCellValue(),
                  e.getMessage());
              val = null;
            }
            stringColumn.append(val);
          }
        }
        break;
      case BOOLEAN:
        if (column.type() == ColumnType.BOOLEAN) {
          Column<Boolean> booleanColumn = (Column<Boolean>) column;
          booleanColumn.append(cell.getBooleanCellValue());
          return null;
        } else if (column.type() == ColumnType.STRING) {
          // If column has String type try to honor it and leave the value as an string as similar
          // as posible as seen in Excel
          Column<String> stringColumn = (Column<String>) column;
          try {
            String val = new CellGeneralFormatter().format(cell.getBooleanCellValue());
            stringColumn.append(val);
          } catch (IllegalFormatException e) {
            logger.warn(
                "Error formatting cell value {} at ({},{}) to string: {}. Ignoring it's value",
                cell.getNumericCellValue(),
                cell.getColumnIndex(),
                cell.getRowIndex(),
                e.getMessage());
            stringColumn.append((String) null);
          }
        }
      default:
        break;
    }
    return null;
  }

  private Column<?> createColumn(
      int colNum,
      String name,
      Sheet sheet,
      int excelColNum,
      TableRange tableRange,
      XlsxReadOptions options) {
    Column<?> column;

    ColumnType columnType =
        options
            .columnTypeReadOptions()
            .columnType(colNum, name)
            .orElse(
                calculateColumnTypeForColumn(sheet, excelColNum, tableRange)
                    .orElse(ColumnType.STRING));

    column = columnType.create(name);
    return column;
  }

  @Override
  public Table read(Source source) {
    return read(XlsxReadOptions.builder(source).build());
  }

  private Optional<ColumnType> calculateColumnTypeForColumn(
      Sheet sheet, int col, TableRange tableRange) {
    Set<CellType> cellTypes = getCellTypes(sheet, col, tableRange);

    if (cellTypes.size() != 1) {
      return Optional.empty();
    }

    CellType cellType = Iterables.get(cellTypes, 0);
    switch (cellType) {
      case STRING:
        return Optional.of(ColumnType.STRING);
      case NUMERIC:
        return allNumericFieldsDateFormatted(sheet, col, tableRange)
            ? Optional.of(ColumnType.LOCAL_DATE_TIME)
            : Optional.of(ColumnType.INTEGER);
      case BOOLEAN:
        return Optional.of(ColumnType.BOOLEAN);
      default:
        return Optional.empty();
    }
  }

  private Set<CellType> getCellTypes(Sheet sheet, int col, TableRange tableRange) {
    return IntStream.range(tableRange.startRow, tableRange.endRow + 1)
        .mapToObj(sheet::getRow)
        .filter(Objects::nonNull)
        .map(row -> row.getCell(col))
        .filter(Objects::nonNull)
        .filter(cell -> !Optional.ofNullable(isBlank(cell)).orElse(false))
        .map(
            cell ->
                cell.getCellType() == FORMULA
                    ? cell.getCachedFormulaResultType()
                    : cell.getCellType())
        .collect(Collectors.toSet());
  }

  private boolean allNumericFieldsDateFormatted(Sheet sheet, int col, TableRange tableRange) {
    return IntStream.range(tableRange.startRow, tableRange.endRow + 1)
        .mapToObj(sheet::getRow)
        .filter(Objects::nonNull)
        .map(row -> row.getCell(col))
        .filter(Objects::nonNull)
        .filter(
            cell ->
                cell.getCellType() == NUMERIC
                    || (cell.getCellType() == FORMULA
                        && cell.getCachedFormulaResultType() == NUMERIC))
        .allMatch(DateUtil::isCellDateFormatted);
  }
}
