package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.plotly.traces.Trace;
import tech.tablesaw.plotly.event.EventHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Plotly's graph description places attributes into two categories:
 * traces (objects that describe a single series of data in a graph like Scatter or Heatmap)
 * and layout attributes that apply to the rest of the chart, like the title, xaxis, or annotations).
 * <p>
 * Figure combines the two parts, associating one or more traces with a layout. If the layout is null a default layout
 * is provided.
 */
public class Figure {

    private final Trace[] data;
    private final Layout layout;
    private final EventHandler[] eventHandlers;

    private final Map<String, Object> context = new HashMap<>();

    private final PebbleEngine engine = TemplateUtils.getNewEngine();

    public Figure(Layout layout, Trace... traces) {
        this.data = traces;
        this.layout = layout;
        this.eventHandlers = null;
    }

    public Figure(Layout layout, EventHandler eventHandler, Trace... traces) {
        this.data = traces;
        this.layout = layout;
        this.eventHandlers = new EventHandler[] {eventHandler};
    }

    public Figure(Layout layout, EventHandler[] eventHandlers, Trace... traces) {
        this.data = traces;
        this.layout = layout;
        this.eventHandlers = eventHandlers;
    }

    public Figure(Trace... traces) {
        this.data = traces;
        this.layout = null;
        this.eventHandlers = null;
    }

    public String divString(String divName) {
        return String.format("<div id='%s' ></div>" + System.lineSeparator(), divName);
    }

    public Layout getLayout() {
        return layout;
    }

    public String asJavascript(String divName) {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        buildContext(divName);

        try {
            compiledTemplate = engine.getTemplate("figure_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private void buildContext(String divName) {

        String targetName = "target_" + divName;
        context.put("divName", divName);
        context.put("targetName", targetName);

        StringBuilder builder = new StringBuilder();

        if (layout != null) {
            builder.append(layout.asJavascript());
        }
        builder.append(System.lineSeparator());
        for (int i = 0; i < data.length; i++) {
            Trace trace = data[i];
            builder.append(trace.asJavascript(i));
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        String figure = builder.toString();
        context.put("figure", figure);
        context.put("plotFunction", plotFunction(targetName));
        context.put("eventHandlerFunction", eventHandlerFunction(targetName, divName));
    }

    private String plotFunction(String divName) {
        StringBuilder builder = new StringBuilder();

        builder.append("var data = [ ");
        for (int i = 0; i < data.length; i++) {
            builder.append("trace")
                    .append(i);
            if (i < data.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("];")
                .append(System.lineSeparator());

        builder.append("Plotly.newPlot(")
                .append(divName)
                .append(", ")
                .append("data");


        if (layout != null) {
            builder.append(", ");
            builder.append("layout");
        }

        builder.append(");");
        
        return builder.toString();
    }

    private String eventHandlerFunction(String targetName, String divName) {
        StringBuilder builder = new StringBuilder();
        
        if (eventHandlers != null) {
            builder.append(System.lineSeparator());
            for (int i=0; i < eventHandlers.length; i++) {
                builder.append(eventHandlers[i].asJavascript(targetName, divName));
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
