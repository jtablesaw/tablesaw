package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.Utils;

public class PieTrace extends AbstractTrace {

  private final double[] values;
  private final Object[] labels;

  private PieTrace(PieBuilder builder) {
    super(builder);
    this.values = builder.values;
    this.labels = builder.labels;
  }

  @Override
  public String asJavascript(int i) {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("pie_trace_template.html");
      compiledTemplate.evaluate(writer, getContext(i));
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext(int i) {

    Map<String, Object> context = super.getContext();
    context.put("variableName", "trace" + i);
    context.put("values", Utils.dataAsString(values));
    if (labels != null) {
      context.put("labels", Utils.dataAsString(labels));
    }
    return context;
  }

  public static PieBuilder builder(Object[] labels, double[] values) {
    return new PieBuilder(labels, values);
  }

  public static PieBuilder builder(Column<?> labels, NumericColumn<? extends Number> values) {
    return new PieBuilder(TraceBuilder.columnToStringArray(labels), values.asDoubleArray());
  }

  public static class PieBuilder extends TraceBuilder {

    private final String type = "pie";
    private final double[] values;
    private final Object[] labels;

    private PieBuilder(Object[] labels, double[] values) {
      this.labels = labels;
      this.values = values;
    }

    public PieTrace build() {
      return new PieTrace(this);
    }

    @Override
    protected String getType() {
      return type;
    }

    public PieTrace.PieBuilder showLegend(boolean b) {
      super.showLegend(b);
      return this;
    }
  }
}
