package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.util.function.Function;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredBooleanColumn extends DeferredColumn
    implements BooleanFilterSpec<Function<Table, Selection>> {

  public DeferredBooleanColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isFalse() {
    return table -> table.booleanColumn(name()).isFalse();
  }

  @Override
  public Function<Table, Selection> isTrue() {
    return table -> table.booleanColumn(name()).isTrue();
  }

  @Override
  public Function<Table, Selection> isEqualTo(BooleanColumn other) {
    return table -> table.booleanColumn(name()).isEqualTo(other);
  }
}
