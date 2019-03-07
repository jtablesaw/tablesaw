package tech.tablesaw.plotly.components.threeD;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CenterTest {

    private static final String javaScript = "{" + System.lineSeparator() +
            "    x: 1.0," + System.lineSeparator() +
            "    y: 2.0," + System.lineSeparator() +
            "    z: 3.0," + System.lineSeparator() +
            "}";

    @Test
    public void centerBuilder() {
        assertEquals(javaScript, Center.centerBuilder(1, 2, 3).build().asJavascript());
    }
}
