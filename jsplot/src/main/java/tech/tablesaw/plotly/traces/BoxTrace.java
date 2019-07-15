package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumericColumn;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static tech.tablesaw.plotly.Utils.dataAsString;

public class BoxTrace extends AbstractTrace {

    private final Object[] x;
    private final double[] y;

    private BoxTrace(BoxBuilder builder) {
        super(builder);
        this.x = builder.x;
        this.y = builder.y;
    }

    public static BoxBuilder builder(Object[] x, double[] y) {
        return new BoxBuilder(x, y);
    }

    public static BoxBuilder builder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
        return new BoxBuilder(x, y);
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
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
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
