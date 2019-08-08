package tech.tablesaw.filtering;

import java.time.Instant;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class DeferredInstantColumn extends DeferredColumn {

  public DeferredInstantColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isEqualTo(Instant value) {
    return table -> table.instantColumn(getColumnName()).isEqualTo(value);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.instantColumn(getColumnName()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.instantColumn(getColumnName()).isNotMissing();
  }

  public Function<Table, Selection> isAfter(Instant value) {
    return table -> table.instantColumn(getColumnName()).isAfter(value);
  }

  public Function<Table, Selection> isBefore(Instant value) {
    return table -> table.instantColumn(getColumnName()).isBefore(value);
  }
}
