package tech.tablesaw.plotly.components;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;

public class Annotation extends Component {

  /**
   * This is to supply annotation in Plot.ly reference to
   * https://plotly.com/javascript/reference/layout/annotations/. Support most of
   * apis there.
   */

  /**
   * The horizontal alignment of the 'text' within the box.
   */
  public enum Align {
    LEFT("left"), CENTER("center"), RIGHT("right");

    private final String value;

    Align(String value) {
      this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  /*
   * The vertical alignment of the 'text' within the box.
   */
  public enum Valign {
    TOP("top"), MIDDLE("middle"), BOTTOM("bottom");

    private final String value;

    Valign(String value) {
      this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  public enum Xanchor {
    AUTO("auto"), LEFT("left"), CENTER("center"), RIGHT("right");

    private final String value;

    Xanchor(String value) {
      this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  public enum Yanchor {
    AUTO("auto"), TOP("top"), MIDDLE("center"), BOTTOM("right");

    private final String value;

    Yanchor(String value) {
      this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  public enum ClicktoShow {
    FALSE(), ONOFF("onoff"), ONOUT("onout");

    private final String value;

    ClicktoShow(String value) {
      this.value = value;
    }

    ClicktoShow() {
      this.value = "false";
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  private static final boolean DEFAULT_VISIBLE = true;
  private static final Font DEFAULT_FONT = Font.builder().build();
  private static final double DEFAULT_OPACITY = 1;
  private static final Align DEFAULT_ALIGN = Align.CENTER;
  private static final Valign DEFAULT_VALIGN = Valign.MIDDLE;
  private static final String DEFAULT_BGCOLOR = "rgba(0,0,0,0)";
  private static final String DEFAULT_BORDERCOLOR = "rgba(0,0,0,0)";
  private static final double DEFAULT_BORDERPAD = 1;
  private static final double DEFAULT_BORDERWIDTH = 1;
  private static final boolean DEFAULT_SHOWARROW = true;
  private static final int DEFAULT_ARROWHEAD = 1;
  private static final int DEFAULT_STARTARROWHEAD = 1;
  private static final String DEFAULT_ARROWSIDE = "END";
  private static final double DEFAULT_ARROWSIZE = 1;
  private static final double DEFAULT_STARTARROWSIZE = 1;
  private static final double DEFAULT_ARROWWIDTH = 1;
  private static final double DEFAULT_STANDOFF = 0;
  private static final double DEFAULT_STARTSTANDOFF = 0;
  private static final String DEFAULT_AXREF = "paper";
  private static final String DEFAULT_AYREF = "paper";
  private static final String DEFAULT_XREF = "paper";
  private static final Xanchor DEFAULT_XANCHOR = Xanchor.AUTO;
  private static final double DEFAULT_XSHIFT = 0;
  private static final String DEFAULT_YREF = "paper";
  private static final Yanchor DEFAULT_YANCHOR = Yanchor.AUTO;
  private static final double DEFAULT_YSHIFT = 0;
  private static final ClicktoShow DEFAULT_CLICKTOSHOW = ClicktoShow.FALSE;
  private static final boolean DEFAULT_CAPTUREEVENTS = true;

  private final boolean visible;
  private final String text;
  private final Font font;
  private final Double width;
  private final Double height;
  private final double opacity;
  private final Align align;
  private final Valign valign;
  private final String bgcolor;
  private final String bordercolor;
  private final double borderpad;
  private final double borderwidth;
  private final boolean showarrow;
  private final String arrowcolor;
  private final int arrowhead;
  private final int startarrowhead;
  private final String arrowside;
  private final double arrowsize;
  private final double startarrowsize;
  private final double arrowwidth;
  private final double standoff;
  private final double startstandoff;
  private final Double ax;
  private final Double ay;
  private final String axref;
  private final String ayref;
  private final String xref;
  private final Double x;
  private final Xanchor xanchor;
  private final double xshift;
  private final String yref;
  private final Double y;
  private final Yanchor yanchor;
  private final double yshift;
  private final ClicktoShow clicktoshow;
  private final Double xclick;
  private final Double yclick;
  private final String hovertext;
  private final HoverLabel hoverlabel;
  private final boolean captureevents;
  private final String name;
  private final String templateitemname;

  private Annotation(AnnotationBuilder builder) {
    this.visible = builder.visible;
    this.text = builder.text;
    this.font = builder.font;
    this.width = builder.width;
    this.height = builder.height;
    this.opacity = builder.opacity;
    this.align = builder.align;
    this.valign = builder.valign;
    this.bgcolor = builder.bgcolor;
    this.bordercolor = builder.bordercolor;
    this.borderpad = builder.borderpad;
    this.borderwidth = builder.borderwidth;
    this.showarrow = builder.showarrow;
    this.arrowcolor = builder.arrowcolor;
    this.arrowhead = builder.arrowhead;
    this.startarrowhead = builder.startarrowhead;
    this.arrowside = builder.arrowside;
    this.arrowsize = builder.arrowsize;
    this.startarrowsize = builder.startarrowsize;
    this.arrowwidth = builder.arrowwidth;
    this.standoff = builder.standoff;
    this.startstandoff = builder.startstandoff;
    this.ax = builder.ax;
    this.ay = builder.ay;
    this.axref = builder.axref;
    this.ayref = builder.ayref;
    this.xref = builder.xref;
    this.x = builder.x;
    this.xanchor = builder.xanchor;
    this.xshift = builder.xshift;
    this.yref = builder.yref;
    this.y = builder.y;
    this.yanchor = builder.yanchor;
    this.yshift = builder.yshift;
    this.clicktoshow = builder.clicktoshow;
    this.xclick = builder.xclick;
    this.yclick = builder.yclick;
    this.hovertext = builder.hovertext;
    this.hoverlabel = builder.hoverLabel;
    this.captureevents = builder.captureevents;
    this.name = builder.name;
    this.templateitemname = builder.templateitemname;
  }

  @Override
  public String asJavascript() {
    return asJavascript("annotation_template.html");
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    if (DEFAULT_VISIBLE != visible)
      context.put("visible", visible);
    if (text != null)
      context.put("text", text);
    if (!DEFAULT_FONT.equals(font))
      context.put("font", font);
    if (width != null)
      context.put("width", width);
    if (height != null)
      context.put("height", height);
    if (opacity != DEFAULT_OPACITY)
      context.put("opacity", opacity);
    if (!DEFAULT_ALIGN.equals(align))
      context.put("align", align);
    if (!DEFAULT_VALIGN.equals(valign))
      context.put("valign", valign);
    if (!DEFAULT_BGCOLOR.equals(bgcolor))
      context.put("bgcolor", bgcolor);
    if (borderpad != DEFAULT_BORDERPAD)
      context.put("borderpad", borderpad);
    if (borderwidth != borderwidth)
      context.put("borderwidth", bordercolor);
    if (showarrow != DEFAULT_SHOWARROW)
      context.put("showarrow", showarrow);
    if (arrowcolor != null)
      context.put("arrowcolor", arrowcolor);
    if (arrowhead != DEFAULT_ARROWHEAD)
      context.put("arrowhead", arrowhead);
    if (startarrowhead != DEFAULT_STARTARROWHEAD)
      context.put("startarrowhead", startarrowhead);
    if (arrowwidth != DEFAULT_ARROWWIDTH)
      context.put("arrowwidth", arrowwidth);
    if (standoff != DEFAULT_STANDOFF)
      context.put("standoff", standoff);
    if (ax != null)
      context.put("ax", ax);
    if (ay != null)
      context.put("ay", ay);
    if (!DEFAULT_AXREF.equals(axref))
      context.put("axref", axref);
    if (!DEFAULT_AYREF.equals(ayref))
      context.put("ayref", ayref);
    if (!DEFAULT_XREF.equals(xref))
      context.put("xref", xref);
    if (x != null)
      context.put("x", x);
    if (!DEFAULT_XANCHOR.equals(xanchor))
      context.put("xanchor", xanchor);
    if (xshift != DEFAULT_XSHIFT)
      context.put("xshift", xshift);
    if (!DEFAULT_YREF.equals(yref))
      context.put("yref", yref);
    if (y != null)
      context.put("y", y);
    if (!DEFAULT_YANCHOR.equals(yanchor))
      context.put("yanchor", yanchor);
    if (yshift != DEFAULT_YSHIFT)
      context.put("yshift", yshift);
    if (!DEFAULT_CLICKTOSHOW.equals(clicktoshow))
      context.put("clicktoshow", clicktoshow);
    if (xclick != null)
      context.put("xclick", xclick);
    if (yclick != null)
      context.put("yclick", yclick);
    if (hoverlabel != null)
      context.put("hoverlabel", hoverlabel);
    if (captureevents != DEFAULT_CAPTUREEVENTS)
      context.put("captureevents", captureevents);
    if (name != null)
      context.put("name", name);
    if (templateitemname != null)
      context.put("templateitemname", templateitemname);

    return context;
  }

  public static AnnotationBuilder builder() {
    return new AnnotationBuilder();
  }

  public static class AnnotationBuilder {
    private boolean visible = DEFAULT_VISIBLE;
    private String text;
    private Font font = DEFAULT_FONT;
    private Double width;
    private Double height;
    private double opacity = DEFAULT_OPACITY;
    private Align align = DEFAULT_ALIGN;
    private Valign valign = DEFAULT_VALIGN;
    private String bgcolor = DEFAULT_BGCOLOR;
    private String bordercolor = DEFAULT_BORDERCOLOR;
    private double borderpad = DEFAULT_BORDERPAD;
    private double borderwidth = DEFAULT_BORDERWIDTH;
    private boolean showarrow = DEFAULT_SHOWARROW;
    private String arrowcolor;
    private int arrowhead = DEFAULT_ARROWHEAD;
    private int startarrowhead = DEFAULT_STARTARROWHEAD;
    private String arrowside = DEFAULT_ARROWSIDE;
    private double arrowsize = DEFAULT_ARROWSIZE;
    private double startarrowsize = DEFAULT_STARTARROWSIZE;
    private double arrowwidth = DEFAULT_ARROWWIDTH;
    private double standoff = DEFAULT_STANDOFF;
    private double startstandoff = DEFAULT_STARTSTANDOFF;
    private Double ax;
    private Double ay;
    private String axref = DEFAULT_AXREF;
    private String ayref = DEFAULT_AYREF;
    private String xref = DEFAULT_XREF;
    private Double x;
    private Xanchor xanchor = DEFAULT_XANCHOR;
    private double xshift = DEFAULT_XSHIFT;
    private String yref = DEFAULT_YREF;
    private Double y;
    private Yanchor yanchor = DEFAULT_YANCHOR;
    private double yshift = DEFAULT_YSHIFT;
    private ClicktoShow clicktoshow;
    private Double xclick;
    private Double yclick;
    private String hovertext;
    private HoverLabel hoverLabel;
    private boolean captureevents = DEFAULT_CAPTUREEVENTS;
    private String name;
    private String templateitemname;

    private AnnotationBuilder() {
    }

    public AnnotationBuilder visible(boolean visible) {
      this.visible = visible;
      return this;
    }

    public AnnotationBuilder text(String text) {
      this.text = text;
      return this;
    }

    public AnnotationBuilder font(Font font) {
      this.font = font;
      return this;
    }

    public AnnotationBuilder width(double width) {
      Preconditions.checkArgument(width >= 0);
      this.width = width;
      return this;
    }

    public AnnotationBuilder height(double height) {
      Preconditions.checkArgument(height >= 0);
      this.height = height;
      return this;
    }

    public AnnotationBuilder opacity(double opacity) {
      Preconditions.checkArgument(opacity >= 0 && opacity <= 1);
      this.opacity = opacity;
      return this;
    }

    public AnnotationBuilder align(Align align) {
      this.align = align;
      return this;
    }

    public AnnotationBuilder Valign(Valign valign) {
      this.valign = valign;
      return this;
    }

    public AnnotationBuilder bgcolor(String bgcolor) {
      this.bgcolor = bgcolor;
      return this;
    }

    public AnnotationBuilder bordercolor(String bordercolor) {
      this.bordercolor = bordercolor;
      return this;
    }

    public AnnotationBuilder borderpad(double borderpad) {
      Preconditions.checkArgument(borderpad >= 0);
      this.borderpad = borderpad;
      return this;
    }

    public AnnotationBuilder borderwidth(double borderwidth) {
      Preconditions.checkArgument(borderwidth >= 0);
      this.borderwidth = borderwidth;
      return this;
    }

    public AnnotationBuilder showarrow(boolean showarrow) {
      this.showarrow = showarrow;
      return this;
    }

    public AnnotationBuilder arrowcolor(String arrowcolor) {
      this.arrowcolor = arrowcolor;
      return this;
    }

    public AnnotationBuilder arrowhead(int arrowhead) {
      this.arrowhead = arrowhead;
      return this;
    }

    public AnnotationBuilder startarrowhead(int startarrowhead) {
      this.startarrowhead = startarrowhead;
      return this;
    }

    public AnnotationBuilder arrowside(String arrowside) {
      this.arrowside = arrowside;
      return this;
    }

    public AnnotationBuilder arrowsize(double arrowsize) {
      Preconditions.checkArgument(arrowsize >= 0.3);
      this.arrowsize = arrowsize;
      return this;
    }

    public AnnotationBuilder startarrowsize(double startarrowsize) {
      Preconditions.checkArgument(startarrowsize >= 0.3);
      this.startarrowsize = startarrowsize;
      return this;
    }

    public AnnotationBuilder arrowwidth(double arrowwidth) {
      Preconditions.checkArgument(arrowwidth >= 0.1);
      this.arrowwidth = arrowwidth;
      return this;
    }

    public AnnotationBuilder standoff(double standoff) {
      Preconditions.checkArgument(standoff >= 0);
      this.standoff = standoff;
      return this;
    }

    public AnnotationBuilder startstandoff(double startstandoff) {
      Preconditions.checkArgument(startstandoff >= 0);
      this.startstandoff = startstandoff;
      return this;
    }

    public AnnotationBuilder ax(double ax) {
      this.ax = ax;
      return this;
    }

    public AnnotationBuilder ay(double ay) {
      this.ay = ay;
      return this;
    }

    public AnnotationBuilder axref(String axref) {
      this.axref = axref;
      return this;
    }

    public AnnotationBuilder ayref(String ayref) {
      this.ayref = ayref;
      return this;
    }

    public AnnotationBuilder xref(String xref) {
      this.xref = xref;
      return this;
    }

    public AnnotationBuilder x(double x) {
      this.x = x;
      return this;
    }

    public AnnotationBuilder xanchor(Xanchor xanchor) {
      this.xanchor = xanchor;
      return this;
    }

    public AnnotationBuilder xshift(double xshift) {
      this.xshift = xshift;
      return this;
    }

    public AnnotationBuilder yref(String yref) {
      this.yref = yref;
      return this;
    }

    public AnnotationBuilder y(double y) {
      this.y = y;
      return this;
    }

    public AnnotationBuilder yanchor(Yanchor yanchor) {
      this.yanchor = yanchor;
      return this;
    }

    public AnnotationBuilder yshift(double yshift) {
      this.yshift = yshift;
      return this;
    }

    public AnnotationBuilder clicktoshow(ClicktoShow clicktoShow) {
      this.clicktoshow = clicktoShow;
      return this;
    }

    public AnnotationBuilder xclick(double xclick) {
      this.xclick = xclick;
      return this;
    }

    public AnnotationBuilder yclick(double yclick) {
      this.yclick = yclick;
      return this;
    }

    public AnnotationBuilder hovertext(String hovertext) {
      this.hovertext = hovertext;
      return this;
    }

    public AnnotationBuilder hoverlabel(HoverLabel hoverLabel) {
      this.hoverLabel = hoverLabel;
      return this;
    }

    public AnnotationBuilder captureevents(boolean captureevents) {
      this.captureevents = captureevents;
      return this;
    }

    public AnnotationBuilder name(String name) {
      this.name = name;
      return this;
    }

    public AnnotationBuilder templateitemname(String templateitemname) {
      this.templateitemname = templateitemname;
      return this;
    }

    public Annotation build() {
      return new Annotation(this);
    }
  }
}
