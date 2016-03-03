package com.deathrayresearch.outlier.util;

/**
 *
 */
public class CsvCombinerTest {

  public static void main(String[] args) throws Exception {
    CsvCombiner.readAll("/Users/larrywhite/Downloads/2015_flight-delays_from_raw_data",
        "bigdata/foobar.csv", ',', true);
  }
}