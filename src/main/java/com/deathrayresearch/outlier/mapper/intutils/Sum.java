package com.deathrayresearch.outlier.mapper.intutils;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.TableGroup;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.mapper.SummaryFunction;
import org.apache.commons.math3.stat.StatUtils;

import java.util.function.ToIntFunction;

/**
 *
 */
public class Sum extends SummaryFunction {

  public static final String FUNCTION_NAME = "Sum";

  private final ToIntFunction<IntColumn> fun = IntColumn::sum;

  public Sum(Table original, String summarizedColumnName) {
    super(original, summarizedColumnName);
  }

  public Table by(String... columnNames) {
    TableGroup group = new TableGroup(original(), columnNames);
    return group.apply(fun, summarizedColumnName(), resultColumnName());
  }

  private String resultColumnName() {
    return FUNCTION_NAME + " " + summarizedColumnName();
  }
}
