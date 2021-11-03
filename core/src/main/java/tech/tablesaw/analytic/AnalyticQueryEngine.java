package tech.tablesaw.analytic;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import tech.tablesaw.analytic.ArgumentList.FunctionCall;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.SortUtils;
import tech.tablesaw.sorting.comparators.IntComparatorChain;
import tech.tablesaw.table.TableSlice;

/**
 * Executes analytic queries.
 *
 * <p>Makes no changes to the underlying table. The order of the rows in "result" Table will match
 * the order of the rows in underlying source table.
 */
final class AnalyticQueryEngine {
  private final AnalyticQuery query;
  private final Table destination;
  private final IntComparatorChain rowComparator;

  private AnalyticQueryEngine(AnalyticQuery query) {
    this.query = query;
    this.destination = Table.create("Analytic ~ " + query.getTable().name());
    Optional<Sort> sort = query.getSort();
    this.rowComparator = sort.isPresent() ? SortUtils.getChain(query.getTable(), sort.get()) : null;
  }

  /** Returns a new AnalyticQueryEngine to execute the given query */
  public static AnalyticQueryEngine create(AnalyticQuery query) {
    return new AnalyticQueryEngine(query);
  }

  /**
   * Execute the given analytic Query.
   *
   * @return a table with the result of the query. Rows in the result table match the order of rows
   *     in the source table.
   */
  public Table execute() {
    addColumns();
    partition().forEach(this::processSlice);
    return destination;
  }

  private void processSlice(TableSlice slice) {
    orderBy(slice);
    processAggregateFunctions(slice);
    processNumberingFunctions(slice);
  }

  /**
   * Execute all aggregate functions for the given slice setting values in the appropriate
   * destination column.
   */
  private void processAggregateFunctions(TableSlice slice) {
    for (String toColumn : query.getArgumentList().getAggregateFunctions().keySet()) {
      FunctionCall<AggregateFunctions> functionCall =
          query.getArgumentList().getAggregateFunctions().get(toColumn);

      AggregateFunctions aggregateFunction = functionCall.getFunction();
      Column<?> sourceColumn = query.getTable().column(functionCall.getSourceColumnName());
      validateColumn(aggregateFunction, sourceColumn);

      Column<?> destinationColumn = destination.column(functionCall.getDestinationColumnName());
      new WindowSlider(
              query.getWindowFrame(), aggregateFunction, slice, sourceColumn, destinationColumn)
          .execute();
    }
  }

  /**
   * Execute all numbering functions for the given slice setting values in the appropriate
   * destination column.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void processNumberingFunctions(TableSlice slice) {
    for (String toColumn : query.getArgumentList().getNumberingFunctions().keySet()) {
      if (rowComparator == null) {
        throw new IllegalArgumentException("Cannot use Numbering Function without OrderBy");
      }
      FunctionCall<NumberingFunctions> functionCall =
          query.getArgumentList().getNumberingFunctions().get(toColumn);
      NumberingFunctions numberingFunctions = functionCall.getFunction();
      NumberingFunction function = numberingFunctions.getImplementation();
      Column<Integer> destinationColumn =
          (Column<Integer>) destination.column(functionCall.getDestinationColumnName());

      int prevRowNumber = -1;
      // Slice has already been ordered.
      for (Row row : slice) {
        if (row.getRowNumber() == 0) {
          function.addNextRow();
        } else {
          // Consecutive rows are equal.
          if (rowComparator.compare(
                  slice.mappedRowNumber(prevRowNumber), slice.mappedRowNumber(row.getRowNumber()))
              == 0) {
            function.addEqualRow();
          } else {
            // Consecutive rows are not equal.
            function.addNextRow();
          }
        }
        prevRowNumber = row.getRowNumber();
        // Set the row number in the destination that corresponds to the row in the view.
        destinationColumn.set(slice.mappedRowNumber(row.getRowNumber()), function.getValue());
      }
    }
  }

  /**
   * Checks to make sure the given aggregate function is compatible with the type of the source
   * column.
   */
  private void validateColumn(FunctionMetaData function, Column<?> sourceColumn) {
    if (!function.isCompatibleColumn(sourceColumn.type())) {
      throw new IllegalArgumentException(
          "Function: "
              + function.functionName()
              + " Is not compatible with column type: "
              + sourceColumn.type());
    }
  }

  /**
   * Creates empty columns that will be filled in when the analytic aggregate or numbering functions
   * are executed.
   */
  private void addColumns() {
    this.destination.addColumns(
        query
            .getArgumentList()
            .createEmptyDestinationColumns(query.getTable().rowCount())
            .toArray(new Column<?>[0]));
  }

  /**
   * Partition the source table into a series of table slices. Does not modify the underlying table.
   */
  private Iterable<TableSlice> partition() {
    if (query.getPartitionColumns().isEmpty()) {
      return ImmutableList.of(new TableSlice(query.getTable()));
    }
    return query.getTable().splitOn(query.getPartitionColumns().toArray(new String[0]));
  }

  /** Order the tableSlice in place. Does not modify the underlying table. */
  private void orderBy(TableSlice tableSlice) {
    query.getSort().ifPresent(tableSlice::sortOn);
  }
}
