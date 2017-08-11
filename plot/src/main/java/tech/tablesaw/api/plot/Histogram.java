package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotting.smile.SmileHistogram;

/**
 *
 */
public class Histogram {


    public static void show(NumericColumn x) {
        SmileHistogram.show(x);
    }

    public static void show(double[] x) {
        SmileHistogram.show(x);
    }

    public static void show(String title, NumericColumn x) {
        SmileHistogram.show(title, x);
    }
}
