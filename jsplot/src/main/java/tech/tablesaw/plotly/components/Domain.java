package tech.tablesaw.plotly.components;

import java.util.HashMap;
import java.util.Map;

public class Domain extends Component {

  private final Integer row;
  private final Integer column;
  private final double[] x;
  private final double[] y;

  private Domain(DomainBuilder builder) {
    this.x = builder.x;
    this.y = builder.y;
    this.row = builder.row;
    this.column = builder.column;
  }

  @Override
  public String asJavascript() {
    return asJSON();
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("column", column);
    context.put("row", row);
    context.put("x", x);
    context.put("y", y);
    return context;
  }

  @Override
  protected Map<String, Object> getJSONContext() {
    return getContext();
  }

  public static DomainBuilder builder() {
    return new DomainBuilder();
  }

  public static class DomainBuilder {

    private Integer row;
    private Integer column;
    private double[] x;
    private double[] y;

    public DomainBuilder row(int row) {
      this.row = row;
      return this;
    }

    public DomainBuilder column(int column) {
      this.column = column;
      return this;
    }

    public DomainBuilder x(double[] x) {
      this.x = x;
      return this;
    }

    public DomainBuilder y(double[] y) {
      this.y = y;
      return this;
    }

    public Domain build() {
      return new Domain(this);
    }
  }
}
