package tech.tablesaw.analytic;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

/**
 * First version operates on table slices.
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
    return null;
  }

  private void addColumns() {
    this.destination.addColumns(query.getArgumentList()
      .createEmptyDestinationColumns(query.getTable().rowCount()).toArray(new Column[0]));
  }

  private TableSliceGroup partition(Table table, AnalyticQuery query) {
    if(query.getPartitionColumns().isEmpty()) {
      // TODO Change TableSliceGroup so you can get a TableSliceGroup with a single slice.
      return StandardTableSliceGroup.create(table, "");
    }
    return table.splitOn(query.getPartitionColumns().toArray(new String[0]));
  }

  private TableSliceGroup orderBy(TableSliceGroup tableSliceGroup, AnalyticQuery query) {
    if(!query.getSort().isPresent()) {
      return tableSliceGroup;
    }
    // TODO sort each table slice.
    return tableSliceGroup;
  }
}
