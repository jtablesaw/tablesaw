package tech.tablesaw.plotly;

import tech.tablesaw.AbstractExample;
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

        Table champagne =
                wines.where(
                        wines.stringColumn("wine type").isEqualTo("Champagne & Sparkling")
                            .and(wines.stringColumn("region").isEqualTo("California")));

        BubblePlot.show("Average retail price for champagnes by year and rating",
                champagne,
                "highest pro score",
                "year",
                "Mean Retail");

        BubblePlot.show("Average retail price for champagnes by year and rating",
                champagne,
                "highest pro score",
                "year",
                "Mean Retail",
                "appellation");

        ScatterPlot.show("Wine prices and ratings", wines, "Mean Retail", "highest pro score", "wine type");

        Scatter3DPlot.show("Champagne (prices, ratings, year, appellation) ",
                champagne,
                "year",
                "highest pro score",
                "mean retail",
                "appellation");

        Scatter3DPlot.show("Champagne (prices, ratings, year, appellation) ",
                champagne,
                "year",
                "highest pro score",
                "mean retail");

    }
}
