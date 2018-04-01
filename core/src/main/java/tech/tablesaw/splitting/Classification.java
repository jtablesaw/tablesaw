package tech.tablesaw.splitting;

import tech.tablesaw.api.Row;
import tech.tablesaw.columns.Column;

public interface Classification {

    Object cut(Row row, Column column);
}
