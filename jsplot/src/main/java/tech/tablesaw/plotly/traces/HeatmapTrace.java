package tech.tablesaw.plotly.traces;

import static tech.tablesaw.plotly.Utils.dataAsString;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import tech.tablesaw.plotly.Utils;

public class HeatmapTrace extends AbstractTrace {

  private final Object[] x;
  private final Object[] y;
  private final double[][] z;
  private final String type;

  private HeatmapTrace(HeatmapBuilder builder) {
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

  public static HeatmapBuilder builder(Object[] x, Object[] y, double[][] z) {
    AtomicInteger counterX = new AtomicInteger(0);
    AtomicInteger counterY = new AtomicInteger(0);
    boolean[] keepX = Utils.filterMissing(counterX, x);
    boolean[] keepY = Utils.filterMissing(counterY, y);

    Object[] xWithoutMissingValue = new Object[counterX.get()];
    Object[] yWithoutMissingValue = new Object[counterY.get()];
    double[][] zWithoutMissingValue = new double[counterY.get()][counterX.get()];

    int xPos = 0;
    for (int xOriginalIdx = 0; xOriginalIdx < keepX.length; xOriginalIdx++) {
      if (!keepX[xOriginalIdx]) {
        continue;
      }
      int yPos = 0;
      for (int yOriginalIdx = 0; yOriginalIdx < keepY.length; yOriginalIdx++) {
        if (keepY[yOriginalIdx]) {
          yWithoutMissingValue[yPos] = y[yOriginalIdx];
          zWithoutMissingValue[yPos][xPos] = z[yOriginalIdx][xOriginalIdx];
          yPos++;
        }
      }
      xWithoutMissingValue[xPos] = x[xOriginalIdx];
      xPos++;
    }
    System.out.println(Arrays.toString(xWithoutMissingValue));
    return new HeatmapBuilder(xWithoutMissingValue, yWithoutMissingValue, zWithoutMissingValue);
  }

  public static class HeatmapBuilder extends TraceBuilder {

    private static final String type = "heatmap";
    private final Object[] x;
    private final Object[] y;
    private final double[][] z;

    HeatmapBuilder(Object[] x, Object[] y, double[][] z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public HeatmapBuilder xAxis(String xAxis) {
      super.xAxis(xAxis);
      return this;
    }

    @Override
    public HeatmapBuilder yAxis(String yAxis) {
      super.yAxis(yAxis);
      return this;
    }

    public HeatmapTrace build() {
      return new HeatmapTrace(this);
    }

    @Override
    protected String getType() {
      return type;
    }
  }
}
