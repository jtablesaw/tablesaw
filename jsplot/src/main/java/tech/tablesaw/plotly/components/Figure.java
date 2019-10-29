package tech.tablesaw.plotly.components;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.tablesaw.plotly.event.EventHandler;
import tech.tablesaw.plotly.traces.Trace;

/**
 * Plotly's graph description places attributes into two categories: traces (objects that describe a
 * single series of data in a graph like Scatter or Heatmap) and layout attributes that apply to the
 * rest of the chart, like the title, xaxis, or annotations).
 *
 * <p>Figure combines the two parts, associating one or more traces with a layout. If the layout is
 * null a default layout is provided.
 */
public class Figure {

  private final Trace[] data;
  private final Layout layout;
  private final Config config;
  private final EventHandler[] eventHandlers;

  private final Map<String, Object> context = new HashMap<>();

  private final PebbleEngine engine = TemplateUtils.getNewEngine();

  public Figure(FigureBuilder builder) {
    this.data = builder.traces();
    this.layout = builder.layout;
    this.config = builder.config;
    this.eventHandlers = builder.eventHandlers();
  }

  public Figure(Trace... traces) {
    this((Layout) null, traces);
  }

  public Figure(Layout layout, Trace... traces) {
    this(layout, (Config) null, traces);
  }

  public Figure(Layout layout, Config config, Trace... traces) {
    this.data = traces;
    this.layout = layout;
    this.config = config;
    this.eventHandlers = null;
  }

  /** @deprecated Use the FigureBuilder instead */
  @Deprecated
  public Figure(Layout layout, EventHandler eventHandler, Trace... traces) {
    this(layout, new EventHandler[] {eventHandler}, traces);
  }

  /** @deprecated Use the FigureBuilder instead */
  @Deprecated
  public Figure(Layout layout, EventHandler[] eventHandlers, Trace... traces) {
    this.data = traces;
    this.layout = layout;
    this.config = null;
    this.eventHandlers = eventHandlers;
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
    if (config != null) {
      builder.append(config.asJavascript());
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
      builder.append("trace").append(i);
      if (i < data.length - 1) {
        builder.append(", ");
      }
    }
    builder.append("];").append(System.lineSeparator());

    builder.append("Plotly.newPlot(").append(divName).append(", ").append("data");

    if (layout != null) {
      builder.append(", ");
      builder.append("layout");
    }
    if (config != null) {
      builder.append(", ");
      builder.append("config");
    }

    builder.append(");");

    return builder.toString();
  }

  private String eventHandlerFunction(String targetName, String divName) {
    StringBuilder builder = new StringBuilder();

    if (eventHandlers != null) {
      builder.append(System.lineSeparator());
      for (EventHandler eventHandler : eventHandlers) {
        builder.append(eventHandler.asJavascript(targetName, divName));
      }
      builder.append(System.lineSeparator());
    }

    return builder.toString();
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public static FigureBuilder builder() {
    return new FigureBuilder();
  }

  public static class FigureBuilder {

    private Layout layout;
    private Config config;
    private List<Trace> traces = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();

    public FigureBuilder layout(Layout layout) {
      this.layout = layout;
      return this;
    }

    public FigureBuilder config(Config config) {
      this.config = config;
      return this;
    }

    public FigureBuilder addTraces(Trace... traces) {
      this.traces.addAll(Arrays.asList(traces));
      return this;
    }

    public FigureBuilder addEventHandlers(EventHandler... handlers) {
      this.eventHandlers.addAll(Arrays.asList(handlers));
      return this;
    }

    public Figure build() {
      Preconditions.checkState(!traces.isEmpty(), "A figure must have at least one trace");
      return new Figure(this);
    }

    private EventHandler[] eventHandlers() {
      return eventHandlers.toArray(new EventHandler[0]);
    }

    private Trace[] traces() {
      return traces.toArray(new Trace[0]);
    }
  }
}
