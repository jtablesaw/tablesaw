package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.plotly.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PieTrace extends AbstractTrace {

    private Object[] x;
    private double[] y;

    private PieTrace(PieBuilder builder) {
        super(builder);
        this.x = builder.x;
        this.y = builder.y;
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
        context.put("values", Utils.dataAsString(y));
        context.put("labels", Utils.dataAsString(x));
        return context;
    }

    public static PieBuilder builder(Object[] x, double[] y) {
        return new PieBuilder(x, y);
    }

    public static PieBuilder builder(CategoricalColumn x, NumberColumn y) {
        return new PieBuilder(TraceBuilder.columnToStringArray(x), y.asDoubleArray());
    }

    public static class PieBuilder extends TraceBuilder {

        private String type = "pie";
        Object[] x;
        double[] y;

        private PieBuilder(Object[] x, double[] y) {
            this.x = x;
            this.y = y;
        }

        public PieTrace build() {
            return new PieTrace(this);
        }

        @Override
        protected String getType() {
            return type;
        }
    }
}
