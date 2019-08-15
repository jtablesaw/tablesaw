package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;

/**
 *  Base class for an Analytic Function.
 */
 interface AnalyticFunctionMetaData {
  String functionName();
  ColumnType returnType();
  boolean isCompatibleColumn(ColumnType type);
}
