package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.TimeSeriesPlot;
import tech.tablesaw.plotly.components.Figure;

class TimeSeriesTest {

  @Test
  void testWithInstant() throws IOException {

    Table dateTable = Table.read().csv("../data/dateTimeTestFile.csv");
    dateTable.addColumns(dateTable.dateTimeColumn(0).asInstantColumn().setName("Instant"));
    Figure figure =
        TimeSeriesPlot.create(
            "Value over time",
            "time",
            dateTable.instantColumn("Instant"),
            "values",
            dateTable.numberColumn("Value"));
    assertNotNull(figure);
  }
}
