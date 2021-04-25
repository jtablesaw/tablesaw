package tech.tablesaw.plotly.traces;

import static tech.tablesaw.plotly.Utils.dataAsString;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.Utils;

public class BoxTrace extends AbstractTrace {

  private final Object[] x;
  private final double[] y;

  private BoxTrace(BoxBuilder builder) {
    super(builder);
    this.x = builder.x;
    this.y = builder.y;
  }

  public static BoxBuilder builder(Object[] x, double[] y) {
    AtomicInteger counter = new AtomicInteger(0);
    boolean[] keep = Utils.filterMissing(counter, x);
    Object[] xWithoutMissingValue = new Object[counter.get()];
    double[] yWithoutMissingValue = new double[counter.get()];
    int i = 0;
    for (int j = 0; j < keep.length; j++) {
      if (keep[j]) {
        xWithoutMissingValue[i] = x[j];
        yWithoutMissingValue[i] = y[j];
        i++;
      }
    }
    return new BoxBuilder(xWithoutMissingValue, yWithoutMissingValue);
  }

  public static BoxBuilder builder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
    Column<?>[] results = Utils.filterMissing(x, y);
    return new BoxBuilder(
        (CategoricalColumn<?>) results[0], (NumericColumn<? extends Number>) results[1]);
  }

  public static BoxBuilder builder(double[] x, double[] y) {
    Double[] xObjs = new Double[x.length];
    for (int i = 0; i < x.length; i++) {
      xObjs[i] = x[i];
    }
    return new BoxBuilder(xObjs, y);
  }

  @Override
  public String asJavascript(int i) {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("trace_template.html");
      compiledTemplate.evaluate(writer, getContext(i));
    } catch (PebbleException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return writer.toString();
  }

  private Map<String, Object> getContext(int i) {

    Map<String, Object> context = super.getContext();
    context.put("variableName", "trace" + i);
    context.put("y", dataAsString(y));
    context.put("x", dataAsString(x));
    return context;
  }

  public static class BoxBuilder extends TraceBuilder {

    private static final String type = "box";
    private final Object[] x;
    private final double[] y;

    BoxBuilder(Object[] x, double[] y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public BoxBuilder name(String name) {
      super.name(name);
      return this;
    }

    BoxBuilder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
      this.x = columnToStringArray(x);
      this.y = y.asDoubleArray();
    }

    public BoxTrace build() {
      return new BoxTrace(this);
    }

    @Override
    public BoxBuilder xAxis(String xAxis) {
      super.xAxis(xAxis);
      return this;
    }

    @Override
    public BoxBuilder yAxis(String yAxis) {
      super.yAxis(yAxis);
      return this;
    }

    @Override
    protected String getType() {
      return type;
    }
  }
}
