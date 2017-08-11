package tech.tablesaw.filtering;

import tech.tablesaw.api.Table;
import tech.tablesaw.util.Selection;

/**
 * A predicate applied to a Relation, to return a subset of the rows in that table
 */
public abstract class Filter {

    public abstract Selection apply(Table relation);
}
