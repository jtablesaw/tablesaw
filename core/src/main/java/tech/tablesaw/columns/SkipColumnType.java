package tech.tablesaw.columns;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class SkipColumnType extends AbstractColumnType {

    public static final ColumnType INSTANCE =
            new SkipColumnType(null, 0, "SKIP", "Skipped");

    private SkipColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public Column<Void> create(String name) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support column creation");
    }

    @Override
    public StringParser<?> defaultParser() {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
    }

    @Override
    public StringParser<?> customParser(CsvReadOptions options) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
    }
}
