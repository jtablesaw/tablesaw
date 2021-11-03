package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;

/** Base class for an Analytic Function. */
interface FunctionMetaData {

  /** Returns the name of the function */
  String functionName();

  /** Returns the type of column that will hold the return values of the function */
  ColumnType returnType();

  /** Returns true if the function can be applied to data of the given {@link ColumnType} */
  boolean isCompatibleColumn(ColumnType type);
}
