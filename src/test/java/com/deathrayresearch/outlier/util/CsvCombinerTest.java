package com.deathrayresearch.outlier.util;

import com.deathrayresearch.outlier.io.CsvCombiner;

/**
 *
 */
public class CsvCombinerTest {

  public static void main(String[] args) throws Exception {
    CsvCombiner.combineAll("/Users/larrywhite/Downloads/2015_flight-delays_from_raw_data",
        "bigdata/foobar.csv", ',', true);
  }
}