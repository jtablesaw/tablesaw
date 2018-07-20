package tech.tablesaw.plotly;

import tech.tablesaw.AbstractExample;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.BubblePlot;
import tech.tablesaw.plotly.api.Scatter3DPlot;
import tech.tablesaw.plotly.api.ScatterPlot;

public class ScatterVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table wines = Table.read().csv("../data/test_wines.csv");
        out(wines);
        out(wines.structure().printAll());
        out(wines.column("varietal").unique().print());
        out(wines.column("region").unique().print());
        out(wines.column("wine type").unique().print());

        Table california = wines.where(wines.stringColumn("region").isEqualTo("California"));

        StringColumn hps = california.stringColumn("highest com score");
        StringColumn yearString = california.stringColumn("year");

        hps.set(hps.isEqualTo("N.A."), "");
        yearString.set(yearString.isEqualTo("N.A."), "");

        hps.setName("highest com score string");
        yearString.setName("year string");

        NumberColumn nhps = DoubleColumn.create("highest com score");
        NumberColumn year = DoubleColumn.create("year");

        for (String s : hps) {
            nhps.appendCell(s);
        }
        for (String s : yearString) {
            year.appendCell(s);
        }
        california.addColumns(nhps, year);

        Table champagne = california
                .where(california.stringColumn("wine type").isEqualTo("Champagne & Sparkling"));

        BubblePlot.show("Champagne prices and ratings",
                champagne,
                "highest pro score",
                "year",
                "Mean Retail"
        );

        BubblePlot.show("Champagne prices and ratings",
                champagne,
                "highest pro score",
                "year",
                "Mean Retail",
                "appellation"
        );

        ScatterPlot.show("Wine prices and ratings", california, "Mean Retail", "highest pro score", "wine type");

        Scatter3DPlot.show("", champagne, "year", "highest pro score", "mean retail", "appellation");

    }
}
