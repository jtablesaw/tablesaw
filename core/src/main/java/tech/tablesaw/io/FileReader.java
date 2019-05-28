package tech.tablesaw.io;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.univocity.parsers.common.AbstractParser;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static tech.tablesaw.api.ColumnType.SKIP;

public abstract class FileReader {

    /**
     * Constructs a CsvReader
     */
    public FileReader() {}

    protected ColumnType[] getTypes(Reader reader, ReadOptions options, int linesToSkip, AbstractParser<?> parser) {

        parser.beginParsing(reader);

        for (int i = 0; i < linesToSkip; i++) {
            parser.parseNext();
        }

        ColumnTypeDetector detector = new ColumnTypeDetector(options.columnTypesToDetect());

        return detector.detectColumnTypes(new Iterator<String[]>() {

            String[] nextRow = parser.parseNext();

            @Override
            public boolean hasNext() {
                return nextRow != null;
            }

            @Override
            public String[] next() {
                String[] tmp = nextRow;
                nextRow = parser.parseNext();
                return tmp;
            }

        }, options);
    }

    private String cleanName(String name) {
        return name.trim();
    }

    private String[] getHeaderNames(ReadOptions options, ColumnType[] types, AbstractParser<?> parser) {
        if (options.header()) {
            String[] headerNames = parser.parseNext();
            // work around issue where Univocity returns null if a column has no header.
            for (int i = 0; i < headerNames.length; i++) {
                if (headerNames[i] == null) {
                    headerNames[i] = "C" + i;
                }
            }
            return headerNames;
        } else {
            // Placeholder column names for when the file read has no header
            String[] headerNames = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                headerNames[i] = "C" + i;
            }
            return headerNames;
        }
    }

    protected Table parseRows(ReadOptions options, boolean headerOnly, Reader reader, ColumnType[] types, AbstractParser<?> parser) {
        parser.beginParsing(reader);
        Table table = Table.create(options.tableName());

        List<String> headerRow = Lists.newArrayList(getHeaderNames(options, types, parser));

        for (int x = 0; x < types.length; x++) {
            if (types[x] != SKIP) {
                String columnName = cleanName(headerRow.get(x));
                if (Strings.isNullOrEmpty(columnName)) {
                    columnName = "Column " + table.columnCount();
                }
                Column<?> newColumn = types[x].create(columnName);
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
            addRows(options, types, parser, table, columnIndexes);
        }

        return table;
    }

    private void addRows(ReadOptions options, ColumnType[] types, AbstractParser<?> reader, Table table, int[] columnIndexes) {
        String[] nextLine;

        Map<String, AbstractColumnParser<?>> parserMap = getParserMap(options, table);

        // Add the rows
        for (long rowNumber = options.header() ? 1L : 0L; (nextLine = reader.parseNext()) != null; rowNumber++) {
            // validation
            if (nextLine.length < types.length) {
                if (nextLine.length == 1 && Strings.isNullOrEmpty(nextLine[0])) {
                    System.err.println("Warning: Invalid file. Row "
                            + rowNumber
                            + " is empty. Continuing.");
                    continue;
                } else {
                    Exception e = new IndexOutOfBoundsException("Row number " + rowNumber + " contains " + nextLine.length + " columns. "
                            + types.length + " expected.");
                    throw new AddCellToColumnException(e, 0, rowNumber, table.columnNames(), nextLine);
                }
            } else if (nextLine.length > types.length) {
                throw new IllegalArgumentException("Row number " + rowNumber + " contains " + nextLine.length + " columns. "
                        + types.length + " expected.");
            }

            // append each column that we're including (not skipping)
            int cellIndex = 0;
            for (int columnIndex : columnIndexes) {
                Column<?> column = table.column(cellIndex);
                AbstractColumnParser<?> parser = parserMap.get(column.name());
                try {
                    String value = nextLine[columnIndex];
                    column.appendCell(value, parser);
                } catch (Exception e) {
                    throw new AddCellToColumnException(e, columnIndex, rowNumber, table.columnNames(), nextLine);
                }
                cellIndex++;
            }
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

    /**
     * Reads column names from header, skipping any for which the type == SKIP
     */
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
