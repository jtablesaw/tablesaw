package tech.tablesaw.filtering;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.Table;
import tech.tablesaw.util.Selection;

/**
 * A boolean filtering. For symmetry with IsFalse
 */
@Immutable
public class IsTrue extends CompositeFilter {

    private final Filter filter;

    private IsTrue(Filter filter) {
        this.filter = filter;
    }

    public static IsTrue isTrue(Filter filter) {
        return new IsTrue(filter);
    }

    /**
     * Returns true if the element in the given row in my {@code column} is true
     *
     * @param relation
     */
    @Override
    public Selection apply(Table relation) {
        return filter.apply(relation);
    }
}
