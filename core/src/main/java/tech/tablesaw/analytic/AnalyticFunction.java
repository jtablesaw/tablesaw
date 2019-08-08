package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;

/**
 *  Base class for an Analytic Function.
 */
 interface AnalyticFunction {
  String functionName();
  ColumnType returnType(ColumnType inputColumnType);
  boolean isCompatibleColumn(ColumnType type);
}
