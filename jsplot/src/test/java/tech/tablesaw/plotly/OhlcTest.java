package tech.tablesaw.plotly;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.OHLCPlot;
import tech.tablesaw.plotly.components.Figure;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OhlcTest {

    @Test
    void ohlcPlotDoesNotThrowIllegalArgumentException() {
        // Test to fix bug reported at https://github.com/jtablesaw/tablesaw/issues/1237
        String timeTitle = "time";
        String openTitle = "open";
        String closeTitle = "close";
        String highTitle = "high";
        String lowTitle = "low";
        String graphTitle = "title";

        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> time = List.of(now, now.plusSeconds(5), now.plusSeconds(10));
        List<Double> open = List.of(1d, 2d, 3d);
        List<Double> close = List.of(1d, 2d, 3d);
        List<Double> high = List.of(1d, 2d, 3d);
        List<Double> low = List.of(1d, 2d, 3d);

        DateTimeColumn timeColumn = DateTimeColumn.create(timeTitle, time);
        DoubleColumn openColumn = DoubleColumn.create(openTitle, open);
        DoubleColumn closeColumn = DoubleColumn.create(closeTitle, close);
        DoubleColumn highColumn = DoubleColumn.create(highTitle, high);
        DoubleColumn lowColumn = DoubleColumn.create(lowTitle, low);

        Table priceTable = Table.create(timeColumn, openColumn, closeColumn, highColumn, lowColumn);
        Figure figure = OHLCPlot.create(graphTitle, priceTable, timeTitle, openTitle, highTitle, lowTitle, closeTitle);

        assertNotNull(figure);
        assertDoesNotThrow(() -> IllegalArgumentException.class);
    }
}
