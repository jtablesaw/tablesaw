package tech.tablesaw.plotly.components;

public class Increasing extends Change {

    private ChangeLine changeLine;
    private String fillColor;

    @Override
    public String asJavascript() {
        return null;
    }

    private Increasing(ChangeBuilder builder) {
        super(builder);
    }


    private static class ChangeLine {

    }
}
