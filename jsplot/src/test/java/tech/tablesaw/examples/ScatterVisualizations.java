package tech.tablesaw.examples;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
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

        wines.column("year").setName("vintage");

        Table champagne =
                wines.where(
                        wines.stringColumn("wine type").isEqualTo("Champagne & Sparkling")
                            .and(wines.stringColumn("region").isEqualTo("California")));

        Plot.show(ScatterPlot.create("Champagne prices by vintage", champagne, "mean retail", "vintage"));

        Plot.show(BubblePlot.create("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail"));

        Plot.show(BubblePlot.create("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail",
                "appellation"));

        Plot.show(Scatter3DPlot.create("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail",
                "appellation"));

        Plot.show(Scatter3DPlot.create("Highest & lowest retail price for champagnes by vintage and rating",
                champagne,
                "vintage",
                "highest retail",
                "lowest retail",
                "highest pro score",
                "appellation"));

        Plot.show(Scatter3DPlot.create("Average retail price for champagnes by vintage and rating",
                champagne,            // table
                "highest pro score",  // x
                "vintage",            // y
                "Mean Retail"));      // z

        Plot.show(ScatterPlot.create("Wine prices and ratings", wines, "Mean Retail", "highest pro score", "wine type"));

        Plot.show(Scatter3DPlot.create("Champagne (prices, ratings, vintage, appellation) ",
                champagne,
                "vintage",
                "highest pro score",
                "mean retail",
                "appellation"));
    }
}
