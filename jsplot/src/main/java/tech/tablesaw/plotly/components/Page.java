package tech.tablesaw.plotly.components;

import java.util.HashMap;
import java.util.Map;

/** Represents an entire html page that contains a figure */
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
    return asJavascript("page_template.html");
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("figureScript", figure.asJavascript(divName));
    context.put("targetDiv", figure.divString(divName));
    context.put("figureTitle", figure.getLayout() != null ? figure.getLayout().getTitle() : null);
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
