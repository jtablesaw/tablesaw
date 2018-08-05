package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PieTrace extends AbstractTrace {

    private double[] y;
    private Object[] labels;

    private PieTrace(PieBuilder builder) {
        super(builder);
        this.y = builder.y;
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
        context.put("values", Utils.dataAsString(y));
        if (labels != null) {
            context.put("labels", Utils.dataAsString(labels));
        }
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
        double[] y;
        Object[] labels;

        private PieBuilder(Object[] x, double[] y) {
            this.labels = x;
            this.y = y;
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

        /**
         * Sets the sector labels. If `labels` entries are duplicated, we sum associated `values` or simply count occurrences if `values` is not provided. For other array attributes (including color) we use the first non-empty entry among all occurrences of the label.
         */
        public PieTrace.PieBuilder labels(Column labels) {
            this.labels = labels.asObjectArray();
            return this;
        }

    }
}
