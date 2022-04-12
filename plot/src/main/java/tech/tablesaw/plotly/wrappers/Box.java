package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.traces.BoxTrace;

import static tech.tablesaw.plotly.wrappers.TraceWrapper.columnToStringArray;

public class Box {

    public static BoxTrace.BoxBuilder builder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
        return new BoxTrace.BoxBuilder(x, y);
    }

    BoxBuilder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
        this.x = columnToStringArray(x);
        this.y = y.asDoubleArray();
    }


}
