package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Layout {

    private final PebbleEngine engine = TemplateUtils.getNewEngine();

    /**
     * Determines the mode of hover interactions.
     */
    public enum HoverMode {
        X("x"),
        Y("y"),
        CLOSEST("closest"),
        FALSE("false");

        private String value;

        HoverMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Determines the display mode for bars when you have multiple bar traces. This also applies to histogram bars.
     * Group is the default.
     *
     * With "stack", the bars are stacked on top of one another.
     * With "relative", the bars are stacked on top of one another, but with negative values below the axis,
     * positive values above.
     * With "group", the bars are plotted next to one another centered around the shared location.
     * With "overlay", the bars are plotted over one another, provide an "opacity" to see through the overlaid bars.
     */
    public enum BarMode {
        STACK("stack"),
        GROUP("group"),
        OVERLAY("overlay"),
        RELATIVE("relative");

        private String value;

        BarMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Determines the mode of drag interactions.
     * "select" and "lasso" apply only to scatter traces with markers or text.
     * "orbit" and "turntable" apply only to 3D scenes.
     */
    public enum DragMode {
        ZOOM("zoom"),
        PAN("pan"),
        SELECT("select"),
        LASSO("lasso"),
        ORBIT("orbit"),
        TURNTABLE("turntable");

        private String value;

        DragMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * The global font
     */
    private Font font;

    /*
     * The plot title
     */
    private String title;

    /**
     * Sets the title font
     */
    private Font titleFont;

    /**
     * Determines whether or not a layout width or height that has been left undefined by the user
     * is initialized on each relayout. Note that, regardless of this attribute, an undefined layout width or height
     * is always initialized on the first call to plot.
     */
    private boolean autoSize;

    /**
     * The width of the plot in pixels
     */
    private int width;

    /**
     * The height of the plot in pixels
     */
    private int height;

    /**
     * Sets the margins around the plot
     */
    private Margin margin;

    /**
     * Sets the color of paper where the graph is drawn.
     */
    private String paperBgColor;

    /**
     * Sets the color of plotting area in-between x and y axes.
     */
    private String plotBgColor;

    /**
     * Sets the decimal. For example, "." puts a '.' before decimals
     */
    private String decimalSeparator;

    /**
     * Sets the separator. For example, a " " puts a space between thousands.
     */
    private String thousandsSeparator;

    /**
     * Determines whether or not a legend is drawn.
     */
    private boolean showLegend;

    /**
     * Determines the mode of hover interactions.
     */
    private HoverMode hoverMode;

    /**
     * Determines the mode of drag interactions. "select" and "lasso" apply only to scatter traces with markers or text.
     * "orbit" and "turntable" apply only to 3D scenes.
     */
    private DragMode dragMode;

    /**
     * Sets the default distance (in pixels) to look for data to add hover labels
     * (-1 means no cutoff, 0 means no looking for data). This is only a real distance
     * for hovering on point-like objects, like scatter points. For area-like objects (bars, scatter fills, etc)
     * hovering is on inside the area and off outside, but these objects will not supersede hover on point-like
     * objects in case of conflict.
     */
    private int hoverDistance;

    private Axis xAxis;

    private Axis yAxis;

    private BarMode barMode;

    private Layout(LayoutBuilder builder) {
        this.title = builder.title;
        this.autoSize = builder.autoSize;
        this.decimalSeparator = builder.decimalSeparator;
        this.thousandsSeparator = builder.thousandsSeparator;
        this.dragMode = builder.dragMode;
        this.font = builder.font;
        this.titleFont = builder.titleFont;
        this.hoverDistance = builder.hoverDistance;
        this.hoverMode = builder.hoverMode;
        this.margin = builder.margin;
        this.height = builder.height;
        this.width = builder.width;
        this.xAxis = builder.xAxis;
        this.yAxis = builder.yAxis;
        this.paperBgColor = builder.paperBgColor;
        this.plotBgColor = builder.plotBgColor;
        this.showLegend = builder.showLegend;
        this.barMode = builder.barMode;
    }

    public String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("layout_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }


    protected Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("title", title);
        context.put("titlefont", titleFont);
        context.put("width", width);
        context.put("height", height);
        context.put("font", font);
        context.put("autosize", autoSize);
        context.put("hoverdistance", hoverDistance);
        context.put("hoverMode", hoverMode);
        if (margin != null) {
            context.put("margin", margin);
        }
        context.put("dragmode", dragMode);
        context.put("showlegend", showLegend);
        context.put("plotbgcolor", plotBgColor);
        context.put("paperbgcolor", paperBgColor);

        context.put("barMode", barMode);

        if (xAxis != null) {
            context.put("xAxis", xAxis);
        }
        if (yAxis != null) {
            context.put("yAxis", yAxis);
        }
        return context;
    }

    public static LayoutBuilder builder() {
        return new LayoutBuilder();
    }

    public static class LayoutBuilder {

        /**
         * The global font
         */
        Font font = Font.builder().build();

        /*
         * The plot title
         */
        String title = "";

        /**
         * Sets the title font
         */
        Font titleFont = font;

        /**
         * Determines whether or not a layout width or height that has been left undefined by the user
         * is initialized on each relayout. Note that, regardless of this attribute, an undefined layout width or height
         * is always initialized on the first call to plot.
         */
        boolean autoSize = false;

        /**
         * The width of the plot in pixels
         */
        int width = 700;

        /**
         * The height of the plot in pixels
         */
        int height = 450;

        /**
         * Sets the margins around the plot
         */
        Margin margin;

        /**
         * Sets the color of paper where the graph is drawn.
         */
        String paperBgColor = "#fff";

        /**
         * Sets the color of plotting area in-between x and y axes.
         */
        String plotBgColor = "#fff";

        /**
         * Sets the decimal. For example, "." puts a '.' before decimals
         */
        String decimalSeparator = ".";

        /**
         * Sets the separator. For example, a " " puts a space between thousands.
         */
        String thousandsSeparator = ",";

        /**
         * Determines whether or not a legend is drawn.
         */
        boolean showLegend = false;

        /**
         * Determines the mode of hover interactions.
         */
        HoverMode hoverMode = HoverMode.FALSE;

        /**
         * Determines the mode of drag interactions. "select" and "lasso" apply only to scatter traces with markers or text.
         * "orbit" and "turntable" apply only to 3D scenes.
         */
        DragMode dragMode = DragMode.ZOOM;

        /**
         * Sets the default distance (in pixels) to look for data to add hover labels
         * (-1 means no cutoff, 0 means no looking for data). This is only a real distance
         * for hovering on point-like objects, like scatter points. For area-like objects (bars, scatter fills, etc)
         * hovering is on inside the area and off outside, but these objects will not supersede hover on point-like
         * objects in case of conflict.
         */
        int hoverDistance = 20; // greater than or equal to -1

        Axis xAxis;

        Axis yAxis;

        BarMode barMode = BarMode.GROUP;

        public Layout build() {
            return new Layout(this);
        }

        private LayoutBuilder() {}

        public LayoutBuilder title(String title) {
            this.title = title;
            return this;
        }

        public LayoutBuilder titleFont(Font titleFont) {
            this.titleFont = titleFont;
            return this;
        }

        public LayoutBuilder barMode(BarMode barMode) {
            this.barMode = barMode;
            return this;
        }

        public LayoutBuilder margin(Margin margin) {
            this.margin = margin;
            return this;
        }

        public LayoutBuilder hoverMode(HoverMode hoverMode) {
            this.hoverMode = hoverMode;
            return this;
        }

        public LayoutBuilder hoverDistance(int distance) {
            this.hoverDistance = distance;
            return this;
        }

        public LayoutBuilder showLegend(boolean showLegend) {
            this.showLegend = showLegend;
            return this;
        }

        public LayoutBuilder height(int height) {
            this.height = height;
            return this;
        }
        public LayoutBuilder width(int width) {
            this.width = width;
            return this;
        }

        public LayoutBuilder xAxis(Axis axis) {
            this.xAxis = axis;
            return this;
        }

        public LayoutBuilder yAxis(Axis axis) {
            this.yAxis = axis;
            return this;
        }

        public LayoutBuilder plotBgColor(String color) {
            this.plotBgColor = color;
            return this;
        }

        public LayoutBuilder paperBgColor(String color) {
            this.paperBgColor = color;
            return this;
        }
    }
}
