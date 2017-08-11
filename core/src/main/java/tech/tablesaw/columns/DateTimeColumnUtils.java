package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.filtering.LongPredicate;

/**
 *
 */
public interface DateTimeColumnUtils extends Column {

    LongPredicate isMissing = i -> i == DateTimeColumn.MISSING_VALUE;
    LongPredicate isNotMissing = i -> i != DateTimeColumn.MISSING_VALUE;

    LongArrayList data();
}
