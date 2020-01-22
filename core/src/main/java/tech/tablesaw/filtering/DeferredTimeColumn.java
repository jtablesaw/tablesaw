package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalTime;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredTimeColumn extends DeferredColumn
    implements TimeAndDateTimeFilterSpec<Function<Table, Selection>>,
        TimeOnlyFilterSpec<Function<Table, Selection>> {

  public DeferredTimeColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isMidnight() {
    return table -> table.timeColumn(name()).isMidnight();
  }

  @Override
  public Function<Table, Selection> isNoon() {
    return table -> table.timeColumn(name()).isNoon();
  }

  @Override
  public Function<Table, Selection> isBefore(LocalTime time) {
    return table -> table.timeColumn(name()).isBefore(time);
  }

  @Override
  public Function<Table, Selection> isAfter(LocalTime time) {
    return table -> table.timeColumn(name()).isAfter(time);
  }

  @Override
  public Function<Table, Selection> isOnOrAfter(LocalTime time) {
    return table -> table.timeColumn(name()).isOnOrAfter(time);
  }

  @Override
  public Function<Table, Selection> isOnOrBefore(LocalTime value) {
    return table -> table.timeColumn(name()).isOnOrBefore(value);
  }

  @Override
  public Function<Table, Selection> isBeforeNoon() {
    return table -> table.timeColumn(name()).isBeforeNoon();
  }

  @Override
  public Function<Table, Selection> isAfterNoon() {
    return table -> table.timeColumn(name()).isAfterNoon();
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(LocalTime value) {
    return table -> table.timeColumn(name()).isNotEqualTo(value);
  }

  @Override
  public Function<Table, Selection> isEqualTo(LocalTime value) {
    return table -> table.timeColumn(name()).isEqualTo(value);
  }

  @Override
  public Function<Table, Selection> isEqualTo(TimeColumn column) {
    return table -> table.timeColumn(name()).isEqualTo(column);
  }

  @Override
  public Function<Table, Selection> isBefore(TimeColumn column) {
    return table -> table.timeColumn(name()).isBefore(column);
  }

  @Override
  public Function<Table, Selection> isAfter(TimeColumn column) {
    return table -> table.timeColumn(name()).isAfter(column);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(TimeColumn column) {
    return table -> table.timeColumn(name()).isNotEqualTo(column);
  }
}
