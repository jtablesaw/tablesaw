package tech.tablesaw.plotly.components.threeD;

public class Up extends CameraComponent {

  public static final Up DEFAULT = Up.upBuilder(0, 0, 1).build();

  private Up(UpBuilder builder) {
    super(builder.x, builder.y, builder.z);
  }

  public static UpBuilder upBuilder(double x, double y, double z) {
    return new UpBuilder(x, y, z);
  }

  public static class UpBuilder {

    private final double x;
    private final double y;
    private final double z;

    private UpBuilder(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Up build() {
      return new Up(this);
    }
  }
}
