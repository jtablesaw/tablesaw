package tech.tablesaw.util;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

public final class ColumnUtil {

    private ColumnUtil() {}

    public static void append(Column<?> sourceCol, Column<?> destCol, int r) {
        if (sourceCol instanceof NumberColumn) {
            ((NumberColumn) destCol).append(((NumberColumn) sourceCol).getDouble(r));
        } else {
            destCol.appendObj(sourceCol.get(r));
        }
    }

}
