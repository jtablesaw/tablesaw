package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.Utils;
import tech.tablesaw.plotly.components.Marker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class HistogramTrace extends AbstractTrace {

    private final double[] x;
    private final double opacity;
    private final int nBinsX;
    private final int nBinsY;
    private final boolean autoBinX;
    private final boolean autoBinY;
    private final Marker marker;

    public static HistogramBuilder builder(double[] values) {
        return new HistogramBuilder(values);
    }

    public static HistogramBuilder builder(NumericColumn<? extends Number> values) {
        return new HistogramBuilder(values.asDoubleArray());
    }

    private HistogramTrace(HistogramBuilder builder) {
        super(builder);
        this.x = builder.x;
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
        context.put("x", Utils.dataAsString(x));
        context.put("opacity", opacity);
        context.put("nBinsX", nBinsX);
        context.put("nBinsY", nBinsY);
        context.put("autoBinX", autoBinX);
        context.put("autoBinY", autoBinY);
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
        private final double[] x;
        private Marker marker;

        private HistogramBuilder(double[] values) {
            this.x = values;
        }

        /**
         * Specifies the maximum number of desired bins. This value will be used
         * in an algorithm that will decide the optimal bin size such that the
         * histogram best visualizes the distribution of the data.
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
         * Determines whether or not the x axis bin attributes are picked by an
         * algorithm. Note that this should be set to False if you want to
         * manually set the number of bins using the attributes in xbins.
         *
         * Note also that this should be true (default) to use nbinsx to suggest
         * a bin count
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

        public HistogramBuilder showLegend(boolean b) {
            super.showLegend(b);
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

        public HistogramTrace build() {
            return new HistogramTrace(this);
        }

        @Override
        protected String getType() {
            return type;
        }
    }
}
