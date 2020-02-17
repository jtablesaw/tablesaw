package tech.tablesaw.plotly.components.threeD;

public class Eye extends CameraComponent {

  private Eye(EyeBuilder builder) {
    super(builder.x, builder.y, builder.z);
  }

  public static EyeBuilder eyeBuilder(double x, double y, double z) {
    return new EyeBuilder(x, y, z);
  }

  public static class EyeBuilder {

    private final double x;
    private final double y;
    private final double z;

    private EyeBuilder(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Eye build() {
      return new Eye(this);
    }
  }
}
