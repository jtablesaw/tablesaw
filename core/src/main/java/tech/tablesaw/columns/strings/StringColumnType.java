package tech.tablesaw.columns.strings;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class StringColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;
    public static final StringParser DEFAULT_PARSER = new StringParser(ColumnType.STRING);

    public static final StringColumnType INSTANCE =
            new StringColumnType(BYTE_SIZE,
                    "STRING",
                    "String");

    private StringColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public StringColumn create(String name) {
        return StringColumn.create(name);
    }

    @Override
    public StringParser customParser(CsvReadOptions options) {
        return new StringParser(this, options);
    }

    public static String missingValueIndicator() {
        return "";
    }

}
