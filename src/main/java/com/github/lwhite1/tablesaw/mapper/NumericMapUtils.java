package com.github.lwhite1.tablesaw.mapper;

import org.apache.commons.math3.stat.StatUtils;

/**
 *
 */
public class NumericMapUtils {


  public double[] normalize(double[] data) {
    return StatUtils.normalize(data);
  }


}
