package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.io.csv.CsvReadOptions;

public class SkipColumnType extends AbstractColumnType {

    public static final ColumnType INSTANCE =
            new SkipColumnType(null, 0, "SKIP", "Skipped");

    private SkipColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
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

    @Override
    public StringParser customParser(CsvReadOptions options) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
    }

    @Override
    public void copy(IntArrayList rows, Column oldColumn, Column newColumn) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support copying");
    }

    @Override
    public void copyFromRows(IntArrayList rows, Column newColumn, Row row) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support copying");
    }

    @Override
    public boolean compare(int rowNumber, Column temp, Column original) {
        throw new UnsupportedOperationException("Column type " + name() + " doesn't support comparison");
    }
}
