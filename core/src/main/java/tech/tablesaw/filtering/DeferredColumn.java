package tech.tablesaw.filtering;

public class DeferredColumn {

  private final String columnName;

  public DeferredColumn(String columnName) {
    this.columnName = columnName;
  }

  public String getColumnName() {
    return columnName;
  }
}
