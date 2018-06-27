package tech.tablesaw.plotly.traces;

import com.google.common.base.Preconditions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.HoverLabel;

public abstract class TraceBuilder {

    AbstractTrace.Visibility visible = AbstractTrace.Visibility.TRUE;

    /**
     * Determines whether or not an item corresponding to this trace is shown in the legend.
     */
    boolean showLegend = false;

    /**
     * Sets the legend group for this trace. Traces part of the same legend group hide/show at the same time
     * when toggling legend items.
     */
    String legendGroup = " ";

    /**
     * Sets the opacity of the trace.
     */
    double opacity = 1; // number between or equal to 0 and 1

    /**
     * Sets the trace name. The trace name appear as the legend item and on hover.
     */
    String name;

    /**
     * Assigns id labels to each datum. These ids for object constancy of data points during animation.
     * Should be an array of strings, not numbers or any other type.
     */
    String[] ids;

    HoverLabel hoverLabel;

    protected TraceBuilder() {}

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

    public HoverLabel hoverLabel() {
         return hoverLabel;
    }

    public boolean showLegend() {
        return showLegend;
    }

    static String[] columnToStringArray(Column numberColumn) {
        String[] x = new String[numberColumn.size()];
        for (int i = 0; i < numberColumn.size(); i++) {
            x[i] = numberColumn.getString(i);
        }
        return x;
    }
}
