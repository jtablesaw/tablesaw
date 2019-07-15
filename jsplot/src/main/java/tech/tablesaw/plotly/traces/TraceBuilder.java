package tech.tablesaw.plotly.traces;

import com.google.common.base.Preconditions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.HoverLabel;

public abstract class TraceBuilder {

    protected AbstractTrace.Visibility visible = AbstractTrace.DEFAULT_VISIBILITY;

    /**
     * Determines whether or not an item corresponding to this trace is shown in
     * the legend.
     */
    protected boolean showLegend = AbstractTrace.DEFAULT_SHOW_LEGEND;

    /**
     * Sets the legend group for this trace. Traces part of the same legend
     * group hide/show at the same time when toggling legend items.
     */
    protected String legendGroup = " ";

    /**
     * Sets the opacity of the trace.
     */
    protected double opacity = AbstractTrace.DEFAULT_OPACITY; // number between or equal to 0 and 1

    /**
     * Sets the trace name. The trace name appear as the legend item and on
     * hover.
     */
    protected String name;

    /**
     * Assigns id labels to each datum. These ids for object constancy of data
     * points during animation. Should be an array of strings, not numbers or
     * any other type.
     */
    protected String[] ids;

    protected HoverLabel hoverLabel;

    /**
     * Sets a reference between this trace's x coordinates and a 2D cartesian x
     * axis. If *x* (the default value), the x coordinates refer to
     * `layout.xaxis`. If *x2*, the x coordinates refer to `layout.xaxis2`, and
     * so on.
     */
    protected String xAxis = "x";
    /**
     * Sets a reference between this trace's y coordinates and a 2D cartesian y
     * axis. If *y* (the default value), the y coordinates refer to
     * `layout.yaxis`. If *y2*, the y coordinates refer to `layout.yaxis2`, and
     * so on.
     */
    protected String yAxis = "y";

    TraceBuilder() {
    }

    protected abstract String getType();

    public TraceBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TraceBuilder opacity(double n) {
        Preconditions.checkArgument(n >= 0 && n <= 1);
        this.opacity = n;
        return this;
    }

    public TraceBuilder legendGroup(String group) {
        this.legendGroup = group;
        return this;
    }

    protected TraceBuilder showLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    protected TraceBuilder visible(AbstractTrace.Visibility visibility) {
        this.visible = visibility;
        return this;
    }

    protected TraceBuilder hoverLabel(HoverLabel hoverLabel) {
        this.hoverLabel = hoverLabel;
        return this;
    }

    protected static String[] columnToStringArray(Column<?> column) {
        String[] x = new String[column.size()];
        for (int i = 0; i < column.size(); i++) {
            x[i] = column.getString(i);
        }
        return x;
    }

    public TraceBuilder xAxis(String xAxis) {
        Preconditions.checkArgument(xAxis.matches("^x[0-9]*$"));
        this.xAxis = xAxis;
        return this;
    }

    public TraceBuilder yAxis(String yAxis) {
        Preconditions.checkArgument(yAxis.matches("^y[0-9]*$"));
        this.yAxis = yAxis;
        return this;
    }
}
