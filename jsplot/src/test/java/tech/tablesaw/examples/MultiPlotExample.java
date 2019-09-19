package tech.tablesaw.examples;

import java.util.List;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

public class MultiPlotExample {

  private static final String pageTop =
      "<html>"
          + System.lineSeparator()
          + "<head>"
          + System.lineSeparator()
          + "    <title>Multi-plot test</title>"
          + System.lineSeparator()
          + "    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>"
          + System.lineSeparator()
          + "</head>"
          + System.lineSeparator()
          + "<body>"
          + System.lineSeparator()
          + "<div id='plot1'>"
          + System.lineSeparator()
          + "<div id='plot2'>"
          + System.lineSeparator();

  private static final String pageBottom = "</body>" + System.lineSeparator() + "</html>";

  private static String makePage(Figure figure1, Figure figure2, String divName1, String divName2) {
    return new StringBuilder()
        .append(pageTop)
        .append(System.lineSeparator())
        .append(figure1.asJavascript(divName1))
        .append(System.lineSeparator())
        .append(figure2.asJavascript(divName2))
        .append(System.lineSeparator())
        .append(pageBottom)
        .toString();
  }

  public static void main(String[] args) throws Exception {

    /*
    Preliminaries:
    1. Get a table
    2. Split the data into two tables, one for each league
    3. Get the columns we're going to use in the plot as x1, y1, x2, y2
    4. Create a layout for each plot
    5. Create the traces for each plot
    6. Build a Figure for each plot from the layout and trace
    7. Create an HTML page as a string
    8. Write the string to a file
    9. Open the default desktop Web browser on the file so you can see it
    */

    // 1. Get a table
    Table baseball = Table.read().csv("../data/baseball.csv");

    // 2. Split the data into two tables, one for each league
    List<Table> leagueTables = baseball.splitOn("league").asTableList();
    Table league1 = leagueTables.get(0);
    Table league2 = leagueTables.get(1);

    // 3. Get the columns we're going to use in the plot as x1, y1, x2, y2
    NumericColumn<?> x1 = league1.nCol("BA");
    NumericColumn<?> y1 = league1.nCol("W");

    NumericColumn<?> x2 = league2.nCol("BA");
    NumericColumn<?> y2 = league2.nCol("W");

    // 4. Create a layout for each plot
    Layout layout1 =
        Layout.builder()
            .title("American League Wins vs BA")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Layout layout2 =
        Layout.builder()
            .title("National League Wins vs BA")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    // 5. Create the traces for each plot
    Trace trace1 = ScatterTrace.builder(x1, y1).build();
    Trace trace2 = ScatterTrace.builder(x2, y2).build();

    // 6. Build a Figure for each plot from the layout and trace
    Figure figure1 = new Figure(layout1, trace1);
    Figure figure2 = new Figure(layout2, trace2);

    // 7. Create an HTML page as a string
    String divName1 = "plot1";
    String divName2 = "plot2";
    String page = makePage(figure1, figure2, divName1, divName2);

    // 8. Write the string to a file
    // uncomment to try (see imports also)

    /*
        File outputFile = Paths.get("multiplot.html").toFile();
        try {
          try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(page);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
    */

    // 9. Open the default desktop Web browser on the file so you can see it
    // uncomment to try

    // new Browser().browse(outputFile);
  }
}
