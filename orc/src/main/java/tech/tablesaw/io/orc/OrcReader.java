package tech.tablesaw.io.orc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.*;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.RecordReader;
import org.apache.orc.TypeDescription;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.Source;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrcReader implements DataReader<OrcReadOptions> {
    private static final OrcReader INSTANCE = new OrcReader();
    private static final List<String> supportedTypes = Arrays.asList("boolean",
            "date",
            "decimal",
            "double",
            "float",
            "int",
            "timestamp",
            "tinyint",
            "smallint",
            "bigint",
            "string",
            "varchar",
            "char");

    static {
        register(Table.defaultReaderRegistry);
    }

    public static void register(ReaderRegistry registry) {
        registry.registerExtension("orc", INSTANCE);
        registry.registerMimeType("application/apache.hadoop.orc", INSTANCE);
        registry.registerOptions(OrcReadOptions.class, INSTANCE);
    }

    @Override
    public Table read(OrcReadOptions options) throws IOException {
        Configuration configuration = new Configuration();
        OrcFile.ReaderOptions readerOptions = OrcFile.readerOptions(configuration);
        if (options.orcReaderOptions() != null) {
            readerOptions = options.orcReaderOptions();
        }
        try (Reader reader = OrcFile.createReader(new Path(options.source().file().getAbsolutePath()),
                readerOptions)) {
            return readFromOrcFile(reader, options);
        }
    }

    private Table readFromOrcFile(Reader reader, OrcReadOptions options) throws IOException {
        List<String> columnName = reader.getSchema().getFieldNames();
        List<String> columnTypes = reader.getSchema().getChildren()
                .stream()
                .map(TypeDescription::toString)
                .map(value -> value.replaceAll("\\(.*\\)", ""))//transform all types from char(5) to char
                .collect(Collectors.toList());
        List<String> unsupportedTypes = getUnsupportedTypes(columnTypes);
        if (!unsupportedTypes.isEmpty()) {
            throw new IllegalArgumentException("Unknown types present: " + unsupportedTypes);
        }
        RecordReader rows = reader.rows();
        VectorizedRowBatch readBatch = reader.getSchema().createRowBatch();

        Table table = Table.create(options.tableName());

        List<Column<?>> columns = new ArrayList<>(Collections.nCopies(columnName.size(), null));
        while (rows.nextBatch(readBatch)) {
            for (int r = 0; r < readBatch.size; ++r) {
                for (int i = 0; i < columnName.size(); i++) {
                    Column<?> column = columns.get(i);
                    if (column == null) {
                        column = createColumn(columnName.get(i), columnTypes.get(i));
                        columns.set(i, column);
                    }
                    appendValue(column, readBatch, r, i, columnTypes.get(i));
                }
            }
        }
        table.addColumns(columns.toArray(new Column<?>[0]));
        rows.close();
        return table;
    }

    private List<String> getUnsupportedTypes(List<String> columnTypes) {
        return columnTypes.stream().filter(str -> !supportedTypes.contains(str)).collect(Collectors.toList());
    }

    private void appendValue(final Column<?> column,final VectorizedRowBatch readBatch,final int rowIndex,final int columnIndex
            ,final String type) {
        int currentRowIndex = rowIndex;
        if (readBatch.cols[columnIndex].isRepeating) {
            currentRowIndex = 0;
        }
        if (!readBatch.cols[columnIndex].isNull[currentRowIndex] || readBatch.cols[columnIndex].noNulls) {
            switch (type) {
                case "boolean":
                    LongColumnVector boolColumnVector = (LongColumnVector) readBatch.cols[columnIndex];
                    boolean value = boolColumnVector.vector[currentRowIndex] == 1;
                    ((BooleanColumn) column).append(value);
                    break;
                case "date":
                    LongColumnVector dateVector = (LongColumnVector) readBatch.cols[columnIndex];
                    long dateValue = dateVector.vector[currentRowIndex];
                    ((DateColumn) column).append(LocalDate.ofEpochDay(dateValue));
                    break;
                case "decimal":
                    //Decimal can hold values of many types such as float, int, byte, short etc for simplicity
                    //we will store values in double
                    DecimalColumnVector decimalColumnVector = (DecimalColumnVector) readBatch.cols[columnIndex];
                    double decimalValue = decimalColumnVector.vector[currentRowIndex].doubleValue();
                    ((DoubleColumn) column).append(decimalValue);
                    break;
                case "double":
                    DoubleColumnVector doubleColumnVector = (DoubleColumnVector) readBatch.cols[columnIndex];
                    double doubleValue = doubleColumnVector.vector[currentRowIndex];
                    ((DoubleColumn) column).append(doubleValue);
                    break;
                case "float":
                    DoubleColumnVector floatColumnVector = (DoubleColumnVector) readBatch.cols[columnIndex];
                    float floatValue = (float) floatColumnVector.vector[currentRowIndex];
                    ((FloatColumn) column).append(floatValue);
                    break;
                case "int":
                    LongColumnVector intColumnVector = (LongColumnVector) readBatch.cols[columnIndex];
                    int intValue = (int) intColumnVector.vector[currentRowIndex];
                    ((IntColumn) column).append(intValue);
                    break;
                case "timestamp":
                    TimestampColumnVector k1 = (TimestampColumnVector) readBatch.cols[columnIndex];
                    Timestamp timestamp = new Timestamp(0);
                    timestamp.setTime(k1.time[currentRowIndex]);
                    timestamp.setNanos(k1.nanos[currentRowIndex]);
                    ((DateTimeColumn) column).append(timestamp.toLocalDateTime());
                    break;
                case "tinyint":
                    LongColumnVector shortColumnVector = (LongColumnVector) readBatch.cols[columnIndex];
                    short shortValue = (short) shortColumnVector.vector[currentRowIndex];
                    ((ShortColumn) column).append(shortValue);
                    break;
                case "smallint":
                    shortColumnVector = (LongColumnVector) readBatch.cols[columnIndex];
                    shortValue = (short) shortColumnVector.vector[currentRowIndex];
                    ((ShortColumn) column).append(shortValue);
                    break;
                case "bigint":
                    LongColumnVector longColumnVector = (LongColumnVector) readBatch.cols[columnIndex];
                    long longValue = longColumnVector.vector[currentRowIndex];
                    ((LongColumn) column).append(longValue);
                    break;
                default:
                    BytesColumnVector strCol = (BytesColumnVector) readBatch.cols[columnIndex];
                    String str = new String(strCol.vector[currentRowIndex], strCol.start[currentRowIndex], strCol.length[currentRowIndex]);
                    column.appendCell(str);
                    break;
            }
        } else {
            column.appendCell(null);
        }
    }

    private Column<?> createColumn(final String name,final String type) {
        ColumnType columnType = null;
        switch (type.toLowerCase()) {
            case "boolean":
                columnType = ColumnType.BOOLEAN;
                break;
            case "date":
                columnType = ColumnType.LOCAL_DATE;
                break;
            case "decimal":
            case "double":
                columnType = ColumnType.DOUBLE;
                break;
            case "float":
                columnType = ColumnType.FLOAT;
                break;
            case "int":
                columnType = ColumnType.INTEGER;
                break;
            case "timestamp":
                columnType = ColumnType.LOCAL_DATE_TIME;
                break;
            case "tinyint":
            case "smallint":
                columnType = ColumnType.SHORT;
                break;
            case "bigint":
                columnType = ColumnType.LONG;
                break;
            default:
                columnType = ColumnType.STRING;
                break;
        }
        return columnType.create(name);
    }

    @Override
    public Table read(Source source) throws IOException {
        return read(OrcReadOptions.builder(source).build());
    }
}
