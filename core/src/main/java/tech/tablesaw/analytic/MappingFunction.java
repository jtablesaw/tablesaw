package tech.tablesaw.analytic;

import tech.tablesaw.columns.Column;

/**
 * An Analytic Aggregate functions accepts a window of rows and returns a single value which is used to set
 * a single row in the output column. In contrast an Analytic Mapping function accepts a window and
 * returns a window of rows (of the same size) which is used to a set a window of rows in the output column.
 *
 * Rank denseRank and rowNumber are examples of analytic mapping functions.
 *
 * Analytic mapping functions are called exactly once per window.
 *
 * TODO be explicit about the use case. I am not sure what else they are used for outside of * numbering functions.
 *
 * @param <T> the output Column type type. E.G. DoubleColumn.
 * @param <R> the output Column type type. E.G. DoubleColumn.
 */
public interface MappingFunction<T extends Column<?>, R extends Column<?>> {
  R apply(T inputWindow);
}
