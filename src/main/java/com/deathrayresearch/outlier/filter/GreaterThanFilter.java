package com.deathrayresearch.outlier.filter;

/**
 *
 */
public class GreaterThanFilter extends AbstractColumnFilter implements FloatColumnFilter {

  final float val;

  public GreaterThanFilter(int columnNumber, float value) {
    super(columnNumber);
    this.val = value;
  }

  @Override
  public boolean matches(float variable) {
    return variable > val;
  }
}
