package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumberColumn;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static tech.tablesaw.plotly.Utils.dataAsString;

public class BarTrace extends AbstractTrace {

    private Object[] x;
    private double[] y;
    private Orientation orientation;

    private BarTrace(BarBuilder builder) {
        super(builder);
        this.orientation = builder.orientation;
        this.x = builder.x;
        this.y = builder.y;
    }

    public static BarBuilder builder(Object[] x, double[] y) {
        return new BarBuilder(x, y);
    }

    public static BarBuilder builder(CategoricalColumn x, NumberColumn y) {
        return new BarBuilder(x, y);
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
        if (orientation == Orientation.HORIZONTAL) {
            context.put("x", dataAsString(y));
            context.put("y", dataAsString(x));
        } else {
            context.put("y", dataAsString(y));
            context.put("x", dataAsString(x));
        }
        context.put("orientation", orientation.value);

        return context;
    }

    public enum Orientation {
        VERTICAL("v"),
        HORIZONTAL("h");

        String value;

        Orientation(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class BarBuilder extends TraceBuilder {

        private String type = "bar";
        Object[] x;
        double[] y;
        Orientation orientation = Orientation.VERTICAL;

        BarBuilder(Object[] x, double[] y) {
            this.x = x;
            this.y = y;
        }

        BarBuilder(CategoricalColumn x, NumberColumn y) {

            this.x = TraceBuilder.columnToStringArray(x);
            this.y = y.asDoubleArray();
        }

        public BarTrace build() {
            return new BarTrace(this);
        }

        /**
         * Sets the orientation of the bars. With "v" ("h"), the value of the each bar spans along the vertical (horizontal).
         */
        public BarBuilder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        @Override
        protected String getType() {
            return type;
        }
    }
}
