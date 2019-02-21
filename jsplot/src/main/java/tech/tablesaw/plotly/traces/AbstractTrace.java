package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.plotly.components.HoverLabel;
import tech.tablesaw.plotly.components.TemplateUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTrace implements Trace {

    protected static final double DEFAULT_OPACITY = 1.0;
    protected static final Visibility DEFAULT_VISIBILITY = Visibility.TRUE;
    protected static final boolean DEFAULT_SHOW_LEGEND = false;

    protected final PebbleEngine engine = TemplateUtils.getNewEngine();

    public enum Visibility {
        TRUE("True"),
        FALSE("False"),
        LEGEND_ONLY("legendonly");

        private final String value;

        Visibility(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        }

    protected final String type;

    private final Visibility visible;

    /**
     * Determines whether or not an item corresponding to this trace is shown in the legend.
     */
    private final boolean showLegend;

    /**
     * Sets the legend group for this trace. Traces part of the same legend group hide/show at the same time
     * when toggling legend items.
     */
    private final String legendGroup;

    /**
     * Sets the opacity of the trace.
     */
    private final double opacity; // number between or equal to 0 and 1

    /**
     * Sets the trace name. The trace name appear as the legend item and on hover.
     */
    private final String name;

    /**
     * Assigns id labels to each datum. These ids for object constancy of data points during animation.
     * Should be an array of strings, not numbers or any other type.
     */
    private final String[] ids;

    private final HoverLabel hoverLabel;

    public AbstractTrace(TraceBuilder builder) {
        this.type = builder.getType();
        this.name = builder.name;
        this.showLegend = builder.showLegend;
        this.legendGroup = builder.legendGroup;
        this.visible = builder.visible;
        this.ids = builder.ids;
        this.hoverLabel = builder.hoverLabel;
        this.opacity = builder.opacity;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("trace_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    protected Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("type", type);
        context.put("name", name);
        if(showLegend != DEFAULT_SHOW_LEGEND) context.put("showLegend", showLegend);
        context.put("legendGroup", legendGroup);
        if(!visible.equals(DEFAULT_VISIBILITY)) context.put("visible", visible);
        context.put("ids", ids);
        context.put("hoverLable", hoverLabel);
        if(opacity != DEFAULT_OPACITY) context.put("opacity", opacity);
        return context;
    }

    public HoverLabel hoverLabel() {
        return hoverLabel;
    }

    public boolean showLegend() {
        return showLegend;
    }
}
