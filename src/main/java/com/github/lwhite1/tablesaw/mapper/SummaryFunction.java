package com.github.lwhite1.tablesaw.mapper;

import com.github.lwhite1.tablesaw.Table;

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
