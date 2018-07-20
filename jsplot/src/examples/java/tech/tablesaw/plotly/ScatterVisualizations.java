package tech.tablesaw.plotly;

import tech.tablesaw.AbstractExample;
import tech.tablesaw.api.Table;
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

       // BPlot.show("", california, "Mean Retail", "highest pro score", "suggestRetail");
        ScatterPlot.show("", california, "Mean Retail", "highest pro score", "wine type");

        Scatter3DPlot.show("", california, "Mean Retail", "highest pro score", "suggested retail");

    }
}
