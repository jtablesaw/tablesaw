package tech.tablesaw.plotly.components.threeD;

import org.junit.Test;

import static org.junit.Assert.*;

public class EyeTest {

    private static final String javaScript = "{\n" +
            "    x: 1.0,\n" +
            "    y: 2.0,\n" +
            "    z: 3.0,\n" +
            "}";

    @Test
    public void eyeBuilder() {
        assertEquals(javaScript, Eye.eyeBuilder(1,2, 3).build().asJavascript());
    }
}