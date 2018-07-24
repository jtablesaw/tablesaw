package tech.tablesaw.plotly.components.threeD;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CenterTest {

    private static final String javaScript = "{\n" +
            "    x: 1.0,\n" +
            "    y: 2.0,\n" +
            "    z: 3.0,\n" +
            "}";

    @Test
    public void centerBuilder() {
        assertEquals(javaScript, Center.centerBuilder(1, 2, 3).build().asJavascript());
    }
}
