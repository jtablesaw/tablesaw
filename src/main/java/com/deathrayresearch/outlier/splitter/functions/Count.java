package com.deathrayresearch.outlier.splitter.functions;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.TableGroup;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.mapper.SummaryFunction;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 *
 */
public class Count extends SummaryFunction {

  public static final String FUNCTION_NAME = "Count";


 // private final ToIntFunction<Column> fun = this::count;

  public Count(Table original, String summarizedColumnName) {
    super(original, summarizedColumnName);
  }
/*
  public Table by(String... columnNames) {
    TableGroup group = new TableGroup(original(), columnNames);
    return group.apply(fun, summarizedColumnName(), resultColumnName());
  }

  public Table by(Column... columns) {
    TableGroup group = new TableGroup(original(), columns);
    return group.apply(fun, summarizedColumnName(), resultColumnName());
  }

  public Table by(ToIntFunction fun, LocalDateColumn column) {
    TableGroup group = new TableGroup(original(), column);
    return group.apply(fun, summarizedColumnName(), resultColumnName());
  }

  private String resultColumnName() {
    return FUNCTION_NAME + " " + summarizedColumnName();
  }

  private int count(Column column) {
    return column.size();
  }
*/
}
