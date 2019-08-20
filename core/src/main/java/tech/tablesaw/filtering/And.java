package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class And implements Function<Table, Selection> {

  private Function<Table, Selection>[] arguments;

  @SafeVarargs
  public And(Function<Table, Selection>... arguments) {
    Preconditions.checkNotNull(arguments, "The arguments to And must be non-null");
    Preconditions.checkArgument(
        arguments.length > 0, "The arguments to And must be an array of length 1 or greater");
    this.arguments = arguments;
  }

  @Override
  public Selection apply(Table table) {
    Selection result = arguments[0].apply(table);
    for (int i = 1; i < arguments.length; i++) {
      result.and(arguments[i].apply(table));
    }
    return result;
  }
}
