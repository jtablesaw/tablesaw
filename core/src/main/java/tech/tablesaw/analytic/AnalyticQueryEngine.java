package tech.tablesaw.analytic;

import java.util.Optional;
import java.util.function.Supplier;
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
  private final Table table;
  private final ArgumentList argumentList;
  private final WindowFrame windowFrame;
  private final Optional<Sort> sort;
  private final Supplier<Iterable<TableSlice>> partitions;
  private final Table destination;
  private final IntComparatorChain rowComparator;

  /**
   * Creates an engine from the parts of a query it needs. The engine does not hold a reference to
   * the query object itself, so the dependency only goes from the query to the engine.
   *
   * @param table the source table
   * @param argumentList the functions to calculate and their source and destination columns
   * @param windowFrame the window frame used by aggregate functions
   * @param sort the optional order of rows within each partition
   * @param partitions supplies the partitions (slices) of the source table to process
   */
  AnalyticQueryEngine(
      Table table,
      ArgumentList argumentList,
      WindowFrame windowFrame,
      Optional<Sort> sort,
      Supplier<Iterable<TableSlice>> partitions) {
    this.table = table;
    this.argumentList = argumentList;
    this.windowFrame = windowFrame;
    this.sort = sort;
    this.partitions = partitions;
    this.destination = Table.create("Analytic ~ " + table.name());
    this.rowComparator = sort.isPresent() ? SortUtils.getChain(table, sort.get()) : null;
  }

  /**
   * Execute the given analytic Query.
   *
   * @return a table with the result of the query. Rows in the result table match the order of rows
   *     in the source table.
   */
  public Table execute() {
    addColumns();
    partitions.get().forEach(this::processSlice);
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
    for (String toColumn : argumentList.getAggregateFunctions().keySet()) {
      FunctionCall<AggregateFunctions> functionCall =
          argumentList.getAggregateFunctions().get(toColumn);

      AggregateFunctions aggregateFunction = functionCall.getFunction();
      Column<?> sourceColumn = table.column(functionCall.getSourceColumnName());
      validateColumn(aggregateFunction, sourceColumn);

      Column<?> destinationColumn = destination.column(functionCall.getDestinationColumnName());
      new WindowSlider(windowFrame, aggregateFunction, slice, sourceColumn, destinationColumn)
          .execute();
    }
  }

  /**
   * Execute all numbering functions for the given slice setting values in the appropriate
   * destination column.
   */
  @SuppressWarnings({"unchecked"})
  private void processNumberingFunctions(TableSlice slice) {
    for (String toColumn : argumentList.getNumberingFunctions().keySet()) {
      if (rowComparator == null) {
        throw new IllegalArgumentException("Cannot use Numbering Function without OrderBy");
      }
      FunctionCall<NumberingFunctions> functionCall =
          argumentList.getNumberingFunctions().get(toColumn);
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
        argumentList.createEmptyDestinationColumns(table.rowCount()).toArray(new Column<?>[0]));
  }

  /** Order the tableSlice in place. Does not modify the underlying table. */
  private void orderBy(TableSlice tableSlice) {
    sort.ifPresent(tableSlice::sortOn);
  }
}
