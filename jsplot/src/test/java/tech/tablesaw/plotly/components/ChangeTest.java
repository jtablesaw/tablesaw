package tech.tablesaw.plotly.components;

import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.change.ChangeLine;
import tech.tablesaw.plotly.components.change.Increasing;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChangeTest {

    @Test
    public void testJavascript() {

        Increasing increasing =
                Increasing.builder()
                        .changeLine(
                                ChangeLine.builder()
                                        .width(3)
                                        .color("blue")
                                        .build())
                        .fillColor("444")
                        .build();

        assertTrue(increasing.asJavascript().contains("line"));
        assertTrue(increasing.asJavascript().contains("color"));
        assertTrue(increasing.asJavascript().contains("width"));
        assertTrue(increasing.asJavascript().contains("fillcolor"));
    }

    @Test
    public void testJavascript2() {

        ChangeLine line =
                ChangeLine.builder()
                        .width(4)
                        .color("444")
                        .build();

        assertTrue(line.asJavascript().contains("color"));
        assertTrue(line.asJavascript().contains("width"));
    }
}