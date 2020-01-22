package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredColumn {

  private final String columnName;

  public DeferredColumn(String columnName) {
    this.columnName = columnName;
  }

  public String name() {
    return columnName;
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.column(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.column(name()).isNotMissing();
  }
}
