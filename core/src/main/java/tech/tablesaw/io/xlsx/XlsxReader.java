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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

@Immutable
public class XlsxReader {

    public Table[] read(final XlsxReadOptions options) throws IOException {
        final byte[] bytes = null;
        final InputStream input = getInputStream(options, bytes);
        final Collection<Table> tables = new ArrayList<Table>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            for (final Sheet sheet : workbook) {
                final int[] tableArea = findTableArea(sheet);
                if (tableArea != null) {
                    final Table table = createTable(sheet, tableArea, options);
                    tables.add(table);
                }
            }
            return tables.toArray(new Table[tables.size()]);
        } finally {
            if (options.reader() == null) {
                // if we get a reader back from options it means the client opened it, so let the client close it
                // if it's null, we close it here.
                input.close();
            }
        }
    }

    private Boolean isBlank(final Cell cell) {
        switch (cell.getCellType()) {
        case STRING:
            if (cell.getRichStringCellValue().length() > 0) {
                return false;
            }
            break;
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() != null : cell.getNumericCellValue() != 0) {
                return false;
            }
            break;
        case BOOLEAN:
            if (cell.getBooleanCellValue()) {
                return false;
            }
            break;
        case BLANK: return true;
        default: break;
        }
        return null;
    }

    private ColumnType getColumnType(final Cell cell) {
        switch (cell.getCellType()) {
        case STRING:
            return ColumnType.STRING;
        case NUMERIC:
            return (DateUtil.isCellDateFormatted(cell) ? ColumnType.LOCAL_DATE_TIME : ColumnType.SHORT);
        case BOOLEAN:
            return ColumnType.BOOLEAN;
            //		case BLANK:
            //			return ColumnType.SKIP;
        default: break;
        }
        return null;
    }

    private int[] findTableArea(final Sheet sheet) {
        // find first row and column with contents
        int row1 = -1;
		int row2 = -1;
        int[] lastRowArea = null;
        for (final Row row : sheet) {
            final int[] rowArea = findRowArea(row);
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
            } else if (rowArea[0] < lastRowArea[0] || rowArea[1] > lastRowArea[1]) {
                lastRowArea = null;
                row2 = -1;
            } else {
                row2 = row.getRowNum();
            }
        }
        return (row1 >= 0 && lastRowArea != null ? new int[]{row1, row2, lastRowArea[0], lastRowArea[1]} : null);
    }

    private int[] findRowArea(final Row row) {
        int col1 = -1;
        int col2 = -1;
        for (final Cell cell : row) {
            final Boolean blank = isBlank(cell);
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
        return (col1 >= 0 && col2 >= col1 ? new int[]{col1, col2} : null);
    }

    private InputStream getInputStream(final XlsxReadOptions options, final byte[] bytes)
            throws FileNotFoundException {
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        if (options.inputStream() != null) {
            return options.inputStream();
        }
        return new FileInputStream(options.file());
    }

    private Table createTable(final Sheet sheet, final int[] tableArea, final XlsxReadOptions options) {
        // assume header row if all cells are of type String
        Row row = sheet.getRow(tableArea[0]);
        final List<String> headerNames = new ArrayList<>();
        for (final Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                headerNames.add(cell.getRichStringCellValue().getString());
            } else {
                break;
            }
        }
        if (headerNames.size() == tableArea[3] - tableArea[2] + 1) {
            tableArea[0]++;
        } else {
            headerNames.clear();
            for (int col = tableArea[2]; col <= tableArea[3]; col++) {
                headerNames.add("col" + col);
            }
        }
        final Table table = Table.create(options.tableName());
        final List<Column<?>> columns = new ArrayList<>();
        while (columns.size() < headerNames.size()) {
            columns.add(null);
        }
        for (int rowNum = tableArea[0]; rowNum <= tableArea[1]; rowNum++) {
            row = sheet.getRow(rowNum);
            for (int colNum = 0; colNum < headerNames.size(); colNum++) {
                final Cell cell = row.getCell(colNum + tableArea[2], MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Column<?> column = columns.get(colNum);
                if (cell != null) {
                    if (column == null) {
                        column = createColumn(headerNames.get(colNum), cell);
                        columns.set(colNum, column);
                        while (columns.size() < rowNum) {
                            column.appendMissing();
                        }
                    }
                    final Column<?> altColumn = appendValue(column, cell);
                    if (altColumn != null && altColumn != column) {
                        column = altColumn;
                        columns.set(colNum, column);
                    }
                } else if (column != null) {
                    column.appendMissing();
                }
            }
        }
        while (columns.remove(null));
        table.addColumns(columns.toArray(new Column<?>[columns.size()]));
        return table;
    }

    private Column<?> appendValue(final Column<?> column, final Cell cell) {
        switch (cell.getCellType()) {
        case STRING:
            column.appendCell(cell.getRichStringCellValue().getString());
            return null;
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                final Date date = cell.getDateCellValue();
                final LocalDateTime localDate = LocalDateTime.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
                column.appendCell(localDate.toString());
                return null;
            } else {
                final double num = cell.getNumericCellValue();
                if (column.type() == ColumnType.SHORT) {
                    final Column<Short> shortColumn = (Column<Short>) column;
                    if ((short) num == num) {
                        shortColumn.append((short) num);
                        return null;
                    } else if ((int) num == num) {
                        Column<Integer> altColumn = ColumnType.INTEGER.create(column.name());
                        altColumn = shortColumn.mapInto(s -> (int) s, altColumn);
                        altColumn.append((int) num);
                        return altColumn;
                    } else if ((long) num == num) {
                        Column<Long> altColumn = ColumnType.LONG.create(column.name());
                        altColumn = shortColumn.mapInto(s -> (long) s, altColumn);
                        altColumn.append((long) num);
                        return altColumn;
                    } else {
                        Column<Double> altColumn = ColumnType.DOUBLE.create(column.name());
                        altColumn = shortColumn.mapInto(s -> (double) s, altColumn);
                        altColumn.append(num);
                        return altColumn;
                    }
                } else if (column.type() == ColumnType.INTEGER) {
                    final Column<Integer> intColumn = (Column<Integer>) column;
                    if ((int) num == num) {
                        intColumn.append((int) num);
                        return null;
                    } else if ((long) num == num) {
                        Column<Long> altColumn = ColumnType.LONG.create(column.name());
                        altColumn = intColumn.mapInto(s -> (long) s, altColumn);
                        altColumn.append((long) num);
                        return altColumn;
                    } else {
                        Column<Double> altColumn = ColumnType.DOUBLE.create(column.name());
                        altColumn = intColumn.mapInto(s -> (double) s, altColumn);
                        altColumn.append(num);
                        return altColumn;
                    }
                } else if (column.type() == ColumnType.LONG) {
                    final Column<Long> longColumn = (Column<Long>) column;
                    if ((long) num == num) {
                        longColumn.append((long) num);
                        return null;
                    } else {
                        Column<Double> altColumn = ColumnType.DOUBLE.create(column.name());
                        altColumn = longColumn.mapInto(s -> (double) s, altColumn);
                        altColumn.append(num);
                        return altColumn;
                    }
                } else if (column.type() == ColumnType.DOUBLE) {
                    final Column<Double> doubleColumn = (Column<Double>) column;
                    doubleColumn.append(num);
                    return null;
                }
            }
            break;
        case BOOLEAN:
            if (column.type() == ColumnType.BOOLEAN) {
                final Column<Boolean> booleanColumn = (Column<Boolean>) column;
                booleanColumn.append(cell.getBooleanCellValue());
                return null;
            }
        default: break;
        }
        return null;
    }

    public Column<?> createColumn(final String name, final Cell cell) {
        Column<?> column;
        ColumnType columnType = getColumnType(cell);
        if (columnType == null) {
            columnType = ColumnType.STRING;
        }
        column = columnType.create(name);
        return column;
    }
}
