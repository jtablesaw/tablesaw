package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.traces.Scatter3DTrace;

public class Scatter3D {

    public static Scatter3DTrace.Scatter3DBuilder builder(
            NumericColumn<? extends Number> x,
            NumericColumn<? extends Number> y,
            NumericColumn<? extends Number> z) {
        return new Scatter3DTrace.Scatter3DBuilder(x, y, z);
    }

    private Scatter3DBuilder(
            NumericColumn<? extends Number> x,
            NumericColumn<? extends Number> y,
            NumericColumn<? extends Number> z) {
        this.x = x.asDoubleArray();
        this.y = y.asDoubleArray();
        this.z = z.asDoubleArray();
    }
}
