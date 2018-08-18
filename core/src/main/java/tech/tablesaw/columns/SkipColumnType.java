package tech.tablesaw.columns;

import tech.tablesaw.io.csv.CsvReadOptions;

public class SkipColumnType extends AbstractColumnType {

    public static final SkipColumnType INSTANCE =
            new SkipColumnType(0, "SKIP", "Skipped");

    private SkipColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public Column<Void> create(String name) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support column creation");
    }

    @Override
    public Comparable<?> getMissingValueIndicator() {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support missing values");
    }

    @Override
    public StringParser<?> defaultParser() {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
    }

    @Override
    public StringParser<?> customParser(CsvReadOptions options) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
    }

    public static Object missingValueIndicator() {
        throw new UnsupportedOperationException("Column type " + SKIP + " doesn't support missing values");
    }
}
