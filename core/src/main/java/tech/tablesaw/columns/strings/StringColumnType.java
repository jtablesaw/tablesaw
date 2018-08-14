package tech.tablesaw.columns.strings;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
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

}
