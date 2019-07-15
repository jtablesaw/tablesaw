package tech.tablesaw.plotly.traces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class TraceBuilderTest {

    
    @Test
    public void shouldFailPreconditionTest() {
        TraceBuilder traceBuilder = new TraceBuilder() {
            @Override
            protected String getType() {
                return "dummy";
            }
        };

        assertThrows(IllegalArgumentException.class, () -> {
            traceBuilder.xAxis("xa");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            traceBuilder.yAxis("yy");
        });
    }
    
    @Test
    public void shouldBeAbleToSetAxis(){
        String xAxis ="x1";
        String yAxis = "y";
        TraceBuilder traceBuilder = new TraceBuilder() {
            @Override
            protected String getType() {
                return "dummy";
            }
        };
        traceBuilder.xAxis(xAxis).yAxis(yAxis);
        assertEquals(xAxis, traceBuilder.xAxis);
        assertEquals(yAxis, traceBuilder.yAxis);
    }
}
