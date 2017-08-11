package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotting.xchart.XchartLine;

/**
 *
 */
public class Line {

    public static void show(String chart, NumericColumn x, NumericColumn y) {
        XchartLine.show(chart, x, y);
    }
}
