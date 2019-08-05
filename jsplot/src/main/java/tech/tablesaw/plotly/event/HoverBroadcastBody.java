package tech.tablesaw.plotly.event;

public class HoverBroadcastBody implements EventHandlerBody {

  private final String[] subPlots;
  private final int nTraces;

  public static HoverBroadcastBuilder builder() {
    return new HoverBroadcastBuilder();
  }

  private HoverBroadcastBody(HoverBroadcastBuilder builder) {
    this.subPlots = builder.subPlots;
    this.nTraces = builder.nTraces;
  }

  @Override
  public String asJavascript(String targetName, String divName, String eventData) {
    StringBuilder builder = new StringBuilder();

    builder.append(String.format("\tvar pointIndex = %s.points[0].pointNumber;", eventData));
    builder.append(System.lineSeparator());
    builder.append(String.format("\tPlotly.Fx.hover('%s',[ ", divName));
    builder.append(System.lineSeparator());

    for (int i = 0; i < nTraces; i++) {
      builder.append(String.format("\t\t{ curveNumber: %d, pointNumber: pointIndex }", i));
      if (i < nTraces - 1) {
        builder.append(", ");
      }
      builder.append(System.lineSeparator());
    }
    builder.append("\t\t]");

    if (subPlots.length > 0) {
      builder.append(", [");
      for (int i = 0; i < subPlots.length; i++) {
        builder.append(String.format("'%s'", subPlots[i]));
        if (i < subPlots.length - 1) {
          builder.append(", ");
        }
      }
      builder.append("]");
      builder.append(System.lineSeparator());
    }
    builder.append("\t);");
    builder.append(System.lineSeparator());
    return builder.toString();
  }

  public static class HoverBroadcastBuilder {
    private String[] subPlots;
    private int nTraces;

    public HoverBroadcastBuilder subPlots(String[] subPlots) {
      this.subPlots = subPlots;
      return this;
    }

    public HoverBroadcastBuilder numTraces(int nTraces) {
      this.nTraces = nTraces;
      return this;
    }

    public HoverBroadcastBody build() {
      return new HoverBroadcastBody(this);
    }
  }
}
