package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.Instant;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredInstantColumn extends DeferredColumn
    implements InstantFilterSpec<Function<Table, Selection>> {

  public DeferredInstantColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isEqualTo(Instant value) {
    return table -> table.instantColumn(name()).isEqualTo(value);
  }

  @Override
  public Function<Table, Selection> isAfter(Instant value) {
    return table -> table.instantColumn(name()).isAfter(value);
  }

  @Override
  public Function<Table, Selection> isBefore(Instant value) {
    return table -> table.instantColumn(name()).isBefore(value);
  }
}
