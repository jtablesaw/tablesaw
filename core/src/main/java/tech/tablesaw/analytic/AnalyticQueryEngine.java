package tech.tablesaw.analytic;

import com.google.common.collect.ImmutableList;
import tech.tablesaw.analytic.ArgumentList.FunctionCall;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSlice;

/**
 * Executes AnalyticQueries.
 */
public class AnalyticQueryEngine {
  private final AnalyticQuery query;
  private final Table destination;

  private AnalyticQueryEngine(AnalyticQuery query) {
    this.query = query;
    this.destination = Table.create("Analytic ~ " + query.getTable().name());
  }

  public static AnalyticQueryEngine create(AnalyticQuery query) {
    return new AnalyticQueryEngine(query);
  }

  public Table execute() {
    addColumns();
    partition().forEach(this::processSlice);
    return destination;
  }

  private void processSlice(TableSlice slice) {
    System.out.println(slice.first(10));
    orderBy(slice);
    for(String toColumn : query.getArgumentList().getAggregateFunctions().keySet()) {
      FunctionCall<AnalyticAggregateFunctions> functionCall = query.getArgumentList()
        .getAggregateFunctions().get(toColumn);

      AnalyticAggregateFunctions aggregateFunction = functionCall.getFunction();
      Column<?> sourceColumn = query.getTable().column(functionCall.getSourceColumnName());
      validateColumn(aggregateFunction, sourceColumn);

      Column<?> destinationColumn = destination.column(functionCall.getDestinationColumnName());
      new WindowSlider(query.getWindowFrame(), aggregateFunction, slice, sourceColumn, destinationColumn)
        .process();
    }
  }

  private void validateColumn(AnalyticFunctionMetaData function, Column<?> sourceColumn) {
    if(!function.isCompatibleColumn(sourceColumn.type())) {
      throw new IllegalArgumentException("Function: " + function.functionName()
        + " Is not compatible with column type: " + sourceColumn.type());
    }
  }

  private void addColumns() {
    this.destination.addColumns(query.getArgumentList()
      .createEmptyDestinationColumns(query.getTable().rowCount()).toArray(new Column[0]));
  }

  private Iterable<TableSlice> partition() {
    if(query.getPartitionColumns().isEmpty()) {
      return ImmutableList.of(new TableSlice(query.getTable()));
    }
    return query.getTable().splitOn(query.getPartitionColumns().toArray(new String[0]));
  }

  private void orderBy(TableSlice tableSlice) {
    if(query.getSort().isPresent()) {
      tableSlice.sortOn(query.getSort().get());
    }
  }
}
