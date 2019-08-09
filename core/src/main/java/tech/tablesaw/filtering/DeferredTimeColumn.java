package tech.tablesaw.filtering;

import java.time.LocalTime;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.selection.Selection;

public class DeferredTimeColumn extends DeferredColumn {

  public DeferredTimeColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isMidnight() {
    return table -> table.timeColumn(name()).isMidnight();
  }

  public Function<Table, Selection> isNoon() {
    return table -> table.timeColumn(name()).isNoon();
  }

  public Function<Table, Selection> isBefore(LocalTime time) {
    return table -> table.timeColumn(name()).isBefore(time);
  }

  public Function<Table, Selection> isAfter(LocalTime time) {
    return table -> table.timeColumn(name()).isAfter(time);
  }

  public Function<Table, Selection> isOnOrAfter(LocalTime time) {
    return table -> table.timeColumn(name()).isOnOrAfter(time);
  }

  public Function<Table, Selection> isOnOrBefore(LocalTime value) {
    return table -> table.timeColumn(name()).isOnOrBefore(value);
  }

  public Function<Table, Selection> isBeforeNoon() {
    return table -> table.timeColumn(name()).isBeforeNoon();
  }

  public Function<Table, Selection> isAfterNoon() {
    return table -> table.timeColumn(name()).isAfterNoon();
  }

  public Function<Table, Selection> isNotEqualTo(LocalTime value) {
    return table -> table.timeColumn(name()).isNotEqualTo(value);
  }

  public Function<Table, Selection> isEqualTo(LocalTime value) {
    return table -> table.timeColumn(name()).isEqualTo(value);
  }

  public Function<Table, Selection> isEqualTo(TimeColumn column) {
    return table -> table.timeColumn(name()).isEqualTo(column);
  }

  public Function<Table, Selection> isBefore(TimeColumn column) {
    return table -> table.timeColumn(name()).isBefore(column);
  }

  public Function<Table, Selection> isAfter(TimeColumn column) {
    return table -> table.timeColumn(name()).isAfter(column);
  }

  public Function<Table, Selection> isNotEqualTo(TimeColumn column) {
    return table -> table.timeColumn(name()).isNotEqualTo(column);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.timeColumn(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.timeColumn(name()).isNotMissing();
  }
}
