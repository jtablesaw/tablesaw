package tech.tablesaw.plotly.traces;

import tech.tablesaw.plotly.components.HoverLabel;

public interface Trace {

    /**
     * Returns a string of Javascript code that can be used to display the trace
     * in a browser
     *
     * @param i A unique number for this trace in the enclosing figure
     * @return A string that can be rendered in javascript
     */
    String asJavascript(int i);

    String name();

    HoverLabel hoverLabel();

    boolean showLegend();

}
