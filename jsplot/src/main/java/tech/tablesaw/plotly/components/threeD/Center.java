package tech.tablesaw.plotly.components.threeD;

class Center extends CameraComponent {

  private Center(CenterBuilder builder) {
    super(builder.x, builder.y, builder.z);
  }

  public static CenterBuilder centerBuilder(double x, double y, double z) {
    return new CenterBuilder(x, y, z);
  }

  public static class CenterBuilder {

    private final double x;
    private final double y;
    private final double z;

    private CenterBuilder(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Center build() {
      return new Center(this);
    }
  }
}
