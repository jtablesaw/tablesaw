package com.github.lwhite1.tablesaw.sorting;

/**
 *
 */
public class IntComparisonUtil {

  private static IntComparisonUtil instance = new IntComparisonUtil();

  public static IntComparisonUtil getInstance() {
    return instance;
  }

  private IntComparisonUtil() {
  }

  public int compare(int a, int b) {
    return a - b;
  }
}
