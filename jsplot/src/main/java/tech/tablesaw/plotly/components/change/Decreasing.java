package tech.tablesaw.plotly.components.change;

public class Decreasing extends Change {

  private Decreasing(DecreasingBuilder builder) {
    super(builder);
  }

  public static DecreasingBuilder builder() {
    return new DecreasingBuilder();
  }

  public static class DecreasingBuilder extends ChangeBuilder {

    public DecreasingBuilder fillColor(String color) {
      this.fillColor = color;
      return this;
    }

    public DecreasingBuilder changeLine(ChangeLine line) {
      this.changeLine = line;
      return this;
    }

    public Decreasing build() {
      return new Decreasing(this);
    }
  }
}
