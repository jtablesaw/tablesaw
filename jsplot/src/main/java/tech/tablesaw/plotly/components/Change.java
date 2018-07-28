package tech.tablesaw.plotly.components;

public class Change extends Component {

    private ChangeLine changeLine;
    private String fillColor;

    @Override
    public String asJavascript() {
        return null;
    }

    Change(ChangeBuilder builder) {
        this.changeLine = builder.changeLine;
        this.fillColor = builder.fillColor;
    }


    private static class ChangeLine {

    }

    public static class ChangeBuilder {

        String fillColor;
        ChangeLine changeLine;

        public ChangeBuilder fillColor(String color) {
            this.fillColor = color;
            return this;
        }

        public ChangeBuilder changeLine(ChangeLine line) {
            this.changeLine = line;
            return this;
        }

        public Change build() {
            return new Change(this);
        }
    }
}
