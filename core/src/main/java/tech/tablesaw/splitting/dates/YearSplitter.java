package tech.tablesaw.splitting.dates;

import com.google.common.base.Preconditions;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.columns.Column;
import tech.tablesaw.splitting.Classification;

public class YearSplitter implements Classification {

    @Override
    public Object cut(Row row, Column column) {

        ColumnType type = column.type();
        Preconditions.checkArgument(type.equals(ColumnType.LOCAL_DATE)
                || type.equals(ColumnType.LOCAL_DATE_TIME));
        return row.getPackedDate(column.name()).getYear();
    }
}
