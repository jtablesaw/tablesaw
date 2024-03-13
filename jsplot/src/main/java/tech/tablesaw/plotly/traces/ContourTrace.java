package tech.tablesaw.plotly.traces;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

import static tech.tablesaw.plotly.Utils.dataAsString;

public class ContourTrace extends AbstractTrace {

  private final Object[] x;
  private final Object[] y;
  private final double[][] z;
  private final String type;

  public ContourTrace(ContourBuilder builder) {
    super(builder);
    this.x = builder.x;
    this.y = builder.y;
    this.z = builder.z;
    this.type = builder.getType();
  }

  @Override
  public String asJavascript(int i) {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("trace_template.html");
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return writer.toString();
  }

  @Override
  protected Map<String, Object> getContext() {

    Map<String, Object> context = super.getContext();
    context.put("variableName", "trace0");
    context.put("x", dataAsString(x));
    context.put("y", dataAsString(y));
    context.put("z", dataAsString(z));
    context.put("type", type);
    return context;
  }

  public static ContourTrace.ContourBuilder builder(Object[] x, Object[] y, double[][] z) {
    return new ContourTrace.ContourBuilder(x, y, z);
  }

  public static class ContourBuilder extends TraceBuilder {

    private static final String type = "contour";
    private final Object[] x;
    private final Object[] y;
    private final double[][] z;

    ContourBuilder(Object[] x, Object[] y, double[][] z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public ContourTrace.ContourBuilder xAxis(String xAxis) {
      super.xAxis(xAxis);
      return this;
    }

    @Override
    public ContourTrace.ContourBuilder yAxis(String yAxis) {
      super.yAxis(yAxis);
      return this;
    }

    public ContourTrace build() {
      return new ContourTrace(this);
    }

    @Override
    protected String getType() {
      return type;
    }
  }
}
