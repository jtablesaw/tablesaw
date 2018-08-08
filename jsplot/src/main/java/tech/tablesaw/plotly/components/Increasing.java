package tech.tablesaw.plotly.components;

public class Increasing extends Change {

    private Increasing(IncreasingBuilder builder) {
        super(builder);
    }

    public static IncreasingBuilder increasingBuilder() {
        return new IncreasingBuilder();
    }

    public static class IncreasingBuilder extends ChangeBuilder {

        private String fillColor;
        private ChangeLine changeLine;

        public Increasing.IncreasingBuilder fillColor(String color) {
            this.fillColor = color;
            return this;
        }

        public Increasing.IncreasingBuilder changeLine(ChangeLine line) {
            this.changeLine = line;
            return this;
        }

        public Increasing build() {
            return new Increasing(this);
        }
    }
}
