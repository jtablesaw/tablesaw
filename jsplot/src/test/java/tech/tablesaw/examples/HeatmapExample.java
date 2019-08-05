package tech.tablesaw.examples;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.Heatmap;
import tech.tablesaw.plotly.components.Figure;

public class HeatmapExample {

  public static void main(String[] args) throws Exception {
    Table table = Table.read().csv("../data/bush.csv");
    StringColumn yearsMonth = table.dateColumn("date").yearMonth();
    String name = "Year and month";
    yearsMonth.setName(name);
    table.addColumns(yearsMonth);

    Figure heatmap = Heatmap.create("Polls conducted by year and month", table, name, "who");
    Plot.show(heatmap);
  }
}
