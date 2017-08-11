package tech.tablesaw.util;

import tech.tablesaw.io.csv.CsvCombiner;

/**
 *
 */
public class CsvCombinerTest {

    public static void main(String[] args) throws Exception {
        CsvCombiner.combineAll("/Users/larrywhite/Downloads/2015_flight-delays_from_raw_data",
                "bigdata/foobar.csv", ',', true);
    }
}