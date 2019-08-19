package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;

@Beta
public class DeferredColumn {

  private final String columnName;

  public DeferredColumn(String columnName) {
    this.columnName = columnName;
  }

  public String name() {
    return columnName;
  }
}
