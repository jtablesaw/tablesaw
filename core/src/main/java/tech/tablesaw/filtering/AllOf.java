package tech.tablesaw.filtering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.util.Selection;

/**
 * A composite filtering that only returns {@code true} if all component filters return true
 */
public class AllOf extends CompositeFilter {

    private final List<Filter> filterList = new ArrayList<>();

    private AllOf(Collection<Filter> filters) {
        this.filterList.addAll(filters);
    }

    public static AllOf allOf(Filter... filters) {
        List<Filter> filterList = new ArrayList<>();
        Collections.addAll(filterList, filters);
        return new AllOf(filterList);
    }

    public static AllOf allOf(Collection<Filter> filters) {
        return new AllOf(filters);
    }

    public Selection apply(Table relation) {
        Selection selection = null;
        for (Filter filter : filterList) {
            if (selection == null) {
                selection = filter.apply(relation);
            } else {
                selection.and(filter.apply(relation));
            }
        }
        return selection;
    }
}
