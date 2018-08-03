package tech.tablesaw.columns.strings;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class StringColumnType extends AbstractColumnType {

    public static final StringStringParser DEFAULT_PARSER = new StringStringParser(ColumnType.STRING);
    public static final StringColumnType INSTANCE =
            new StringColumnType("",
                    4,
                    "STRING",
                    "String");

    private StringColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public StringColumn create(String name) {
        return StringColumn.create(name);
    }

    @Override
    public StringStringParser defaultParser() {
        return new StringStringParser(this);
    }

    @Override
    public StringStringParser customParser(CsvReadOptions options) {
        return new StringStringParser(this, options);
    }

    @Override
    public void copy(IntArrayList rows, Column oldColumn, Column newColumn) {
        StringColumn oldString = (StringColumn) oldColumn;
        StringColumn newString = (StringColumn) newColumn;
        for (int index : rows) {
            newString.append(oldString.get(index));
        }
    }

    @Override
    public void copyFromRows(IntArrayList rows, Column newColumn, Row row) {
        StringColumn newTime = (StringColumn) newColumn;
        for (int index : rows) {
            row.at(index);
            newTime.append(row.getString(newColumn.name()));
        }
    }

    @Override
    public boolean compare(int rowNumber, Column temp, Column original) {
        StringColumn tempString = (StringColumn) temp;
        StringColumn originalString = (StringColumn) original;
        return originalString.get(rowNumber).equals(tempString.get(tempString.size() - 1));
    }
}
