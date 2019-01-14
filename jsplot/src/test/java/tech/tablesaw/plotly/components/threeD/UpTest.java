package tech.tablesaw.plotly.components.threeD;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpTest {

    private static final String javaScript = "{" + System.lineSeparator() +
            "    x: 1.0," + System.lineSeparator() +
            "    y: 2.0," + System.lineSeparator() +
            "    z: 3.0," + System.lineSeparator() +
            "}";

    @Test
    public void upBuilder() {
        assertEquals(javaScript, Up.upBuilder(1, 2, 3).build().asJavascript());
    }
}
