package tech.tablesaw.plotly.components;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Represents an entire html page that contains a figure
 */
public class Page extends Component {

    private final Figure figure;
    private final String divName;

    private final String plotlyJsLocation;

    private Page(PageBuilder builder) {
        this.figure = builder.figure;
        this.divName = builder.divName;
        this.plotlyJsLocation = builder.plotlyJsLocation;
    }

    @Override
    public String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("page_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("figureScript", figure.asJavascript(divName));
        context.put("targetDiv", figure.divString(divName));
        context.put("figureTitle", figure.getLayout() != null? figure.getLayout().getTitle() : null);
        context.put("plotlyJsLocation", plotlyJsLocation);
        return context;
    }

    public static PageBuilder pageBuilder(Figure figure, String divName) {
        return new PageBuilder(figure, divName);
    }

    public static class PageBuilder {

        private final Figure figure;
        private final String divName;

        private String plotlyJsLocation = null;

        public PageBuilder(Figure figure, String divName) {
            this.figure = figure;
            this.divName = divName;
        }

        public Page build() {
            return new Page(this);
        }
        
        public PageBuilder plotlyJsLocation(String location) {
            this.plotlyJsLocation = location;
            return this;
        }
    }
}
