package tech.tablesaw.columns.dates;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

public class DateColumnType extends AbstractColumnType {

    public static final DateStringParser DEFAULT_PARSER = new DateStringParser(ColumnType.LOCAL_DATE);
    public static final DateColumnType INSTANCE =
            new DateColumnType(Integer.MIN_VALUE, 4, "LOCAL_DATE", "Date");

    private DateColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateColumn create(String name) {
        return DateColumn.create(name);
    }

    @Override
    public StringParser<LocalDate> defaultParser() {
        return new DateStringParser(this);
    }

    @Override
    public StringParser<LocalDate> customParser(CsvReadOptions options) {
        return new DateStringParser(this, options);
    }

    @Override
    public void copy(IntArrayList rows, Column oldColumn, Column newColumn) {
        DateColumn oldDate = (DateColumn) oldColumn;
        DateColumn newDate = (DateColumn) newColumn;
        for (int index : rows) {
            newDate.appendInternal(oldDate.getIntInternal(index));
        }
    }

    @Override
    public void copyFromRows(IntArrayList rows, Column newColumn, Row row) {
        DateColumn newDate = (DateColumn) newColumn;
        for (int index : rows) {
            row.at(index);
            PackedDate date = row.getPackedDate(newColumn.name());
            newDate.appendInternal(date.getPackedValue());
        }
    }

    @Override
    public boolean compare(int rowNumber, Column temp, Column original) {
        DateColumn tempDate = (DateColumn) temp;
        DateColumn originalDate = (DateColumn) original;
        return originalDate.getIntInternal(rowNumber) == tempDate.getIntInternal(tempDate.size() - 1);
    }
}
