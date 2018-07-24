package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Scene extends Component {

    private final Axis xAxis;

    private final Axis yAxis;

    private final Axis zAxis;

    private Scene(SceneBuilder builder) {
        this.xAxis = builder.xAxis;
        this.yAxis = builder.yAxis;
        this.zAxis = builder.zAxis;
    }

    protected Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        if (xAxis != null) {
            context.put("xAxis", xAxis);
        }
        if (yAxis != null) {
            context.put("yAxis", yAxis);
        }
        if (zAxis != null) {
            context.put("zAxis", zAxis);
        }
        return context;
    }

    public static Scene.SceneBuilder sceneBuilder() {
        return new Scene.SceneBuilder();
    }

    @Override
    String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;
        try {
            compiledTemplate = engine.getTemplate("scene_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();

    }

    public static class SceneBuilder {

        Axis xAxis;

        Axis yAxis;

        Axis zAxis;

        public SceneBuilder xAxis(Axis axis) {
            this.xAxis = axis;
            return this;
        }

        public SceneBuilder yAxis(Axis axis) {
            this.yAxis = axis;
            return this;
        }

        public SceneBuilder zAxis(Axis axis) {
            this.zAxis = axis;
            return this;
        }

        public Scene build() {
            return new Scene(this);
        }

    }
}
