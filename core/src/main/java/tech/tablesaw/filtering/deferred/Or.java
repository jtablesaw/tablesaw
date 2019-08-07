package tech.tablesaw.filtering.deferred;

import com.google.common.base.Preconditions;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.function.Function;

public class Or implements Function<Table, Selection> {

    private Function<Table, Selection>[] arguments;

    public Or(Function<Table, Selection>... arguments) {
        Preconditions.checkArgument(
                arguments != null && arguments.length > 0,
                "The arguments to Or must be a non-null array of length 1 or greater");
        this.arguments = arguments;
    }

    @Override
    public Selection apply(Table table) {
        Selection result = arguments[0].apply(table);
        if (arguments.length > 1) {
            for (int i = 1; i < arguments.length; i++) {
                result.or(arguments[i].apply(table));
            }
        }
        return result;
    }
}
