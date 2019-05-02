package tech.tablesaw.columns.instant;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

public class InstantColumnType extends AbstractColumnType {

    public static int BYTE_SIZE = 8;

    public static final InstantParser DEFAULT_PARSER = new InstantParser(ColumnType.LOCAL_DATE_TIME);

    private static InstantColumnType INSTANCE =
            new InstantColumnType(BYTE_SIZE, "INSTANT", "Instant");

    private InstantColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static InstantColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new InstantColumnType(BYTE_SIZE, "INSTANT", "Instant");
        }
        return INSTANCE;
    }

    @Override
    public DateTimeColumn create(String name) {
        return DateTimeColumn.create(name);
    }

    @Override
    public InstantParser customParser(ReadOptions options) {
        return new InstantParser(this);
    }

    public static long missingValueIndicator() {
        return Long.MIN_VALUE;
    }
}
