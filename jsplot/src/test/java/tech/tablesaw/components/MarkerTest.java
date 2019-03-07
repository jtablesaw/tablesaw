package tech.tablesaw.components;

import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkerTest {


    @Test
    public void asJavascript() {
        Marker x = Marker.builder()
                .size(12.0)
                .symbol(Symbol.DIAMOND_TALL)
                .color("#c68486")
                .build();

        assertTrue(x.asJavascript().contains("color"));
        assertTrue(x.asJavascript().contains("symbol"));
        assertTrue(x.asJavascript().contains("size"));
    }

}

