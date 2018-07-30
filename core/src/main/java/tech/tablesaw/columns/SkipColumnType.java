package tech.tablesaw.columns;

public class SkipColumnType extends AbstractColumnType {

    public SkipColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public Column create(String name) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support column creation");
    }

    @Override
    public StringParser defaultParser() {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
    }
}
