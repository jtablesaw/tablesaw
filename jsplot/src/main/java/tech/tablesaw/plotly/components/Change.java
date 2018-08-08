package tech.tablesaw.plotly.components;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Change extends Component {

    private ChangeLine changeLine;
    private String fillColor;

    @Override
    public String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("change_template.html");

            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    Change(ChangeBuilder builder) {
        this.changeLine = builder.changeLine;
        this.fillColor = builder.fillColor;
    }

    private Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("changeLine", changeLine);
        context.put("fillColor", fillColor);
        return context;
    }

    public static class ChangeBuilder {

        String fillColor;
        ChangeLine changeLine;

        public ChangeBuilder fillColor(String color) {
            this.fillColor = color;
            return this;
        }

        public ChangeBuilder changeLine(ChangeLine line) {
            this.changeLine = line;
            return this;
        }

        public Change build() {
            return new Change(this);
        }
    }


    static class ChangeLine extends Component {

        final String color;
        final int width;

        private ChangeLine(LineBuilder lineBuilder) {

            color = lineBuilder.color;
            width = lineBuilder.width;
        }

        @Override
        public String asJavascript() {
            return null;
        }
    }

    public static class LineBuilder {

        String color = "#3D9970";
        int width = 2;

        /**
         * Sets the color of line bounding the box(es).
         */
        public LineBuilder color(String color) {
            this.color = color;
            return this;
        }

        /**
         * Sets the width (in px) of line bounding the box(es).
         *
         * @param width greater than or equal to 0
         */
        public LineBuilder width(int width) {
            Preconditions.checkArgument(width >= 0);
            this.width = width;
            return this;
        }

        public ChangeLine build() {
            return new ChangeLine(this);
        }
    }
}
