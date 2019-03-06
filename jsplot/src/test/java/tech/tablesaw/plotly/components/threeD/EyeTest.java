package tech.tablesaw.plotly.components.threeD;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EyeTest {

    private static final String javaScript = "{" + System.lineSeparator() +
            "    x: 1.0," + System.lineSeparator() +
            "    y: 2.0," + System.lineSeparator() +
            "    z: 3.0," + System.lineSeparator() +
            "}";

    @Test
    public void eyeBuilder() {
        assertEquals(javaScript, Eye.eyeBuilder(1,2, 3).build().asJavascript());
    }
}