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

        wines.column("year").setName("vintage");

        Table champagne =
                wines.where(
                        wines.stringColumn("wine type").isEqualTo("Champagne & Sparkling")
                            .and(wines.stringColumn("region").isEqualTo("California")));

        ScatterPlot.show("Champagne prices by vintage", champagne, "mean retail", "vintage");

        BubblePlot.show("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail");

        BubblePlot.show("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail",
                "appellation");

        Scatter3DPlot.show("Average retail price for champagnes by vintage and rating",
                champagne,				    // table
                "highest pro score",  	// x
                "vintage", 			// y
                "Mean Retail"); 		// z

        ScatterPlot.show("Wine prices and ratings", wines, "Mean Retail", "highest pro score", "wine type");

        Scatter3DPlot.show("Champagne (prices, ratings, vintage, appellation) ",
                champagne,
                "vintage",
                "highest pro score",
                "mean retail",
                "appellation");

        Scatter3DPlot.show("Champagne (prices, ratings, vintage, appellation) ",
                champagne,
                "vintage",
                "highest pro score",
                "mean retail");

    }
}
