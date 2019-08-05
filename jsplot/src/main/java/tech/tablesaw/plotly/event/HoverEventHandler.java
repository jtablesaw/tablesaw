package tech.tablesaw.plotly.event;

public class HoverEventHandler implements EventHandler {

  private final EventHandlerBody body;
  private final String eventDataVarName = "eventData";

  public static HoverEventHanlderBuilder builder() {
    return new HoverEventHanlderBuilder();
  }

  private HoverEventHandler(HoverEventHanlderBuilder builder) {
    this.body = builder.body;
  }

  /**
   * Returns a string of Javascript code that implements a plotly hover event handler
   *
   * @param targetName name of the target document
   * @param divName target document id
   * @return A string that can be rendered in javascript
   */
  @Override
  public String asJavascript(String targetName, String divName) {
    StringBuilder builder = new StringBuilder();

    builder.append(
        String.format("%s.on('plotly_hover', function(%s){", targetName, eventDataVarName));
    builder.append(System.lineSeparator());

    builder.append(body.asJavascript(targetName, divName, eventDataVarName));

    builder.append("});");
    builder.append(System.lineSeparator());

    return builder.toString();
  }

  public static class HoverEventHanlderBuilder {
    private EventHandlerBody body;

    public HoverEventHanlderBuilder body(EventHandlerBody body) {
      this.body = body;
      return this;
    }

    public HoverEventHandler build() {
      return new HoverEventHandler(this);
    }
  }
}
