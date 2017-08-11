package tech.tablesaw.filtering;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.Table;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

/**
 * A boolean filtering, returns true if the filtering it wraps returns false, and vice-versa.
 */
@Immutable
public class IsFalse extends CompositeFilter {

    private final Filter filter;

    private IsFalse(Filter filter) {
        this.filter = filter;
    }

    public static IsFalse isFalse(Filter filter) {
        return new IsFalse(filter);
    }

    /**
     * Returns true if the element in the given row in my {@code column} is true
     */
    @Override
    public Selection apply(Table relation) {
        Selection selection = new BitmapBackedSelection();
        selection.addRange(0, relation.rowCount());
        selection.andNot(filter.apply(relation));
        return selection;
    }
}
