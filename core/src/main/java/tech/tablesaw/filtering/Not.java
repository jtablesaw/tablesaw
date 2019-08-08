package tech.tablesaw.filtering;

import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class Not implements Function<Table, Selection> {

  private Function<Table, Selection> argument;

  public Not(Function<Table, Selection> argument) {
    this.argument = argument;
  }

  @Override
  public Selection apply(Table table) {
    return argument.apply(table).flip(0, table.rowCount());
  }
}
