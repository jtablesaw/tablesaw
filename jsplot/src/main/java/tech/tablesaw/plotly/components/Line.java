package tech.tablesaw.plotly.components;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Line extends Component {

    private final String color;
    private final double width;
    private final double smoothing;
    private final Shape shape;
    private final String dash;
    private final boolean simplify;

    private Line(LineBuilder builder) {
        this.color = builder.color;
        this.shape = builder.shape;
        this.smoothing = builder.smoothing;
        this.dash = builder.dash;
        this.simplify = builder.simplify;
        this.width = builder.width;
    }

    public Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("color", color);
        context.put("width", width);
        context.put("shape", shape);
        context.put("smoothing", smoothing);
        context.put("dash", dash);
        context.put("simplify", simplify);
        return context;
    }

    @Override
    public String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("line_template.html");

            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public enum Shape {
        LINEAR("linear"),
        SPLINE("spline"),
        HV("hv"),
        VH("vh"),
        HVH("hvh"),
        VHV("vhv");

        private final String value;

        Shape(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public String getColor() {
        return color;
    }

    public double getWidth() {
        return width;
    }

    public double getSmoothing() {
        return smoothing;
    }

    public Shape getShape() {
        return shape;
    }

    public String getDash() {
        return dash;
    }

    public boolean isSimplify() {
        return simplify;
    }

    public static LineBuilder builder() {
        return new LineBuilder();
    }

    public static class LineBuilder {
        private String color = "gray";
        private double width = 2;
        private double smoothing = 1;
        private Shape shape = Shape.LINEAR;
        private String dash = "solid";
        private boolean simplify = true;

        public LineBuilder color(String color) {
            this.color = color;
            return this;
        }
        public LineBuilder width(double width) {
            Preconditions.checkArgument(width >= 0);
            this.width = width;
            return this;
        }
        public LineBuilder smoothing(double smoothing) {
            Preconditions.checkArgument(smoothing >= 0 && smoothing <= 1.3);
            this.smoothing = smoothing;
            return this;
        }
        public LineBuilder dash(String dash) {
            this.dash = dash;
            return this;
        }
        public LineBuilder simplify(boolean b) {
            this.simplify = b;
            return this;
        }
        public LineBuilder shape(Shape shape) {
            this.shape = shape;
            return this;
        }
        public Line build() {
            return new Line(this);
        }
    }
}
