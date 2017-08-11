package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntIterable;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.filtering.BooleanPredicate;

/**
 *
 */
public interface BooleanColumnUtils extends Column, IntIterable {

    BooleanPredicate isMissing = i -> i == Byte.MIN_VALUE;

    BooleanPredicate isNotMissing = i -> i != BooleanColumn.MISSING_VALUE;
}
