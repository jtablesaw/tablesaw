package tech.tablesaw.plotly;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.ContourPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.ContourTrace;

import java.io.IOException;

@Disabled
public class ContourTest {

  private final Object[] x = {-9, -6, -5, -3, -1};
  private final Object[] y = {0, 1, 4, 5, 7};
  private final double[][] z = {
      {10, 10.625, 12.5, 15.625, 20},
      {5.625, 6.25, 8.125, 11.25, 15.625},
      {2.5, 3.125, 5.0, 8.125, 12.5},
      {0.625, 1.25, 3.125, 6.25, 10.625},
      {0, 0.625, 2.5, 5.625, 10}
  };

  @Test
  public void testAsJavascript() {
    ContourTrace trace = ContourTrace.builder(x, y, z).build();
    System.out.println(trace.asJavascript(1));
  }

  @Test
  public void testContourTrace() {
    ContourTrace trace = ContourTrace.builder(x, y, z).build();
    Figure figure = new Figure(trace);
    Plot.show(figure);
  }

  @Test
  public void testContourPlot() throws IOException {
    Table table = Table.read().csv("../data/bush.csv");
    StringColumn yearsMonth = table.dateColumn("date").yearMonth();
    String name = "Year and month";
    yearsMonth.setName(name);
    table.addColumns(yearsMonth);

    Figure figure = ContourPlot.create("Polls conducted by year and month", table, name, "who");
    Plot.show(figure);
  }
}
