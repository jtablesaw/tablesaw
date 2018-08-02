package tech.tablesaw.columns.datetimes;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDateTime;

public class DateTimeColumnType extends AbstractColumnType {

    public static final DateTimeStringParser DEFAULT_PARSER = new DateTimeStringParser(ColumnType.LOCAL_DATE_TIME);
    public static final DateTimeColumnType INSTANCE =
            new DateTimeColumnType(Long.MIN_VALUE, 8, "LOCAL_DATE_TIME", "DateTime");

    private DateTimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateTimeColumn create(String name) {
        return DateTimeColumn.create(name);
    }

    @Override
    public StringParser<LocalDateTime> defaultParser() {
        return new DateTimeStringParser(this);
    }

    @Override
    public DateTimeStringParser customParser(CsvReadOptions options) {
        return new DateTimeStringParser(this, options);
    }

    @Override
    public void copy(IntArrayList rows, Column oldColumn, Column newColumn) {
        DateTimeColumn oldDateTime = (DateTimeColumn) oldColumn;
        DateTimeColumn newDateTime = (DateTimeColumn) newColumn;
        for (int index : rows) {
            newDateTime.appendInternal(oldDateTime.getLongInternal(index));
        }
    }

    @Override
    public boolean compare(int rowNumber, Column temp, Column original) {
        DateTimeColumn tempDateTime = (DateTimeColumn) temp;
        DateTimeColumn originalDateTime = (DateTimeColumn) original;
        return originalDateTime.getLongInternal(rowNumber) == tempDateTime.getLongInternal(tempDateTime.size() - 1);
    }
}
