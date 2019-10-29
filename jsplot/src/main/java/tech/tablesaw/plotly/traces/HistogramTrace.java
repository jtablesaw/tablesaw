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
import tech.tablesaw.plotly.components.Marker;

public class HistogramTrace extends AbstractTrace {

  private final Object[] x;
  private final Object[] y;
  private final double opacity;
  private final int nBinsX;
  private final int nBinsY;
  private final boolean autoBinX;
  private final boolean autoBinY;
  private final Marker marker;
  private final HistNorm histNorm;
  private final HistFunc histFunc;

  public enum HistNorm {
    NONE(""),
    PERCENT("percent"),
    PROBABILITY("probability"),
    DENSITY("density"),
    PROBABILITY_DENSITY("probability density");

    private final String value;

    HistNorm(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public enum HistFunc {
    COUNT("count"),
    SUM("sum"),
    AVG("avg"),
    MIN("min"),
    MAX("max");

    private final String value;

    HistFunc(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public static HistogramBuilder builder(double[] values) {
    return new HistogramBuilder(values);
  }

  public static HistogramBuilder builder(NumericColumn<? extends Number> values) {
    return new HistogramBuilder(values.asDoubleArray());
  }

  public static HistogramBuilder builder(Column<?> xValues, NumericColumn<? extends Number> values) {
    return new HistogramBuilder(xValues.asObjectArray(), values.asDoubleArray());
  }

  private HistogramTrace(HistogramBuilder builder) {
    super(builder);
    if (builder.horizontal) {
      this.x = builder.y;
      this.y = builder.x;
    } else {
      this.x = builder.x;
      this.y = builder.y;
    }
    this.histNorm = builder.histNorm;
    this.histFunc = builder.histFunc;
    this.nBinsX = builder.nBinsX;
    this.nBinsY = builder.nBinsY;
    this.autoBinX = builder.autoBinX;
    this.autoBinY = builder.autoBinY;
    this.opacity = builder.opacity;
    this.marker = builder.marker;
  }

  @Override
  public String asJavascript(int i) {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("trace_template.html");
      compiledTemplate.evaluate(writer, getContext(i));
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext(int i) {

    Map<String, Object> context = super.getContext();
    context.put("variableName", "trace" + i);
    if (x != null) {
      context.put("x", Utils.dataAsString(x));
    }
    if (y != null) {
      context.put("y", Utils.dataAsString(y));
    }
    context.put("opacity", opacity);
    context.put("nBinsX", nBinsX);
    context.put("nBinsY", nBinsY);
    context.put("autoBinX", autoBinX);
    context.put("autoBinY", autoBinY);
    context.put("histnorm", histNorm);
    context.put("histfunc", histFunc);
    if (marker != null) {
      context.put("marker", marker);
    }

    return context;
  }

  public static class HistogramBuilder extends TraceBuilder {

    private final String type = "histogram";
    private int nBinsX;
    private int nBinsY;
    private boolean autoBinX;
    private boolean autoBinY;
    private boolean horizontal = false;
    private Object[] x;
    private Object[] y;
    private Marker marker;
    private HistNorm histNorm = HistNorm.NONE;
    private HistFunc histFunc = HistFunc.COUNT;

    private HistogramBuilder(double[] values) {
      this.x = doublesToObjects(values);
    }

    private HistogramBuilder(Object[] xValues, double[] yValues) {
      this.x = xValues;
      this.y = doublesToObjects(yValues);
    }

    /**
     * Specifies the maximum number of desired bins. This value will be used in an algorithm that
     * will decide the optimal bin size such that the histogram best visualizes the distribution of
     * the data.
     */
    public HistogramBuilder nBinsX(int bins) {
      this.nBinsX = bins;
      return this;
    }

    public HistogramBuilder nBinsY(int bins) {
      this.nBinsY = bins;
      return this;
    }

    /**
     * Determines whether or not the x axis bin attributes are picked by an algorithm. Note that
     * this should be set to False if you want to manually set the number of bins using the
     * attributes in xbins.
     *
     * <p>Note also that this should be true (default) to use nbinsx to suggest a bin count
     */
    public HistogramBuilder autoBinX(boolean autoBinX) {
      this.autoBinX = autoBinX;
      return this;
    }

    public HistogramBuilder autoBinY(boolean autoBinY) {
      this.autoBinY = autoBinY;
      return this;
    }

    public HistogramBuilder marker(Marker marker) {
      this.marker = marker;
      return this;
    }

    public HistogramBuilder opacity(double opacity) {
      super.opacity(opacity);
      return this;
    }

    public HistogramBuilder horizontal(boolean horizontal) {
      this.horizontal = horizontal;
      return this;
    }

    public HistogramBuilder showLegend(boolean b) {
      super.showLegend(b);
      return this;
    }

    /**
     * Specifies the type of normalization used for this histogram trace. If "", the span of each
     * bar corresponds to the number of occurrences (i.e. the number of data points lying inside the
     * bins). If "percent" / "probability", the span of each bar corresponds to the percentage /
     * fraction of occurrences with respect to the total number of sample points (here, the sum of
     * all bin HEIGHTS equals 100% / 1). If "density", the span of each bar corresponds to the
     * number of occurrences in a bin divided by the size of the bin interval (here, the sum of all
     * bin AREAS equals the total number of sample points). If "probability density", the area of
     * each bar corresponds to the probability that an event will fall into the corresponding bin
     * (here, the sum of all bin AREAS equals 1).
     *
     * @param histNorm  The normalization type for the histogram
     * @return          This HistogramBuilder
     */
    public HistogramBuilder histNorm(HistNorm histNorm) {
      this.histNorm = histNorm;
      return this;
    }

    /**
     * Specifies the binning function used for this histogram trace. If "count", the histogram
     * values are computed by counting the number of values lying inside each bin. If "sum", "avg",
     * "min", "max", the histogram values are computed using the sum, the average, the minimum or
     * the maximum of the values lying inside each bin respectively.
     *
     * @param histFunc  The function type
     * @return          This HistogramBuilder
     */
    public HistogramBuilder histFunc(HistFunc histFunc) {
      this.histFunc = histFunc;
      return this;
    }

    public HistogramBuilder name(String name) {
      super.name(name);
      return this;
    }

    public HistogramBuilder xAxis(String xAxis) {
      super.xAxis(xAxis);
      return this;
    }

    public HistogramBuilder yAxis(String yAxis) {
      super.yAxis(yAxis);
      return this;
    }

    public HistogramBuilder y(double[] y) {
      this.y = doublesToObjects(y);
      return this;
    }

    public HistogramBuilder y(NumericColumn<? extends Number> values) {
      this.y = values.asObjectArray();
      return this;
    }

    public HistogramTrace build() {
      return new HistogramTrace(this);
    }

    @Override
    protected String getType() {
      return type;
    }
  }

  private static Object[] doublesToObjects(double[] doubles) {
    Object[] objects = new Object[doubles.length];
    for (int i = 0; i < doubles.length; i++) {
      objects[i] = doubles[i];
    }
    return objects;
  }
}
