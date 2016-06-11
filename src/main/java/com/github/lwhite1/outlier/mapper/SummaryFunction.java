package com.github.lwhite1.outlier.mapper;

import com.github.lwhite1.outlier.Table;

/**
 *
 */
public class SummaryFunction {

  private final Table original;
  private final String summarizedColumnName;

  public SummaryFunction(Table original, String summarizedColumnName) {
    this.original = original;
    this.summarizedColumnName = summarizedColumnName;
  }

  protected String summarizedColumnName() {
    return summarizedColumnName;
  }

  protected Table original() {
    return original;
  }
}
