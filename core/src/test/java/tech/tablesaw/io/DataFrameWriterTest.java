package tech.tablesaw.io;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class DataFrameWriterTest {

    private static final String LINE_END = System.lineSeparator();

    private double[] v1 = {1, 2, NaN};
    private double[] v2 = {1, 2, NaN};
    private Table table = Table.create("t",
            DoubleColumn.create("v", v1),
            DoubleColumn.create("v2", v2)
    );

    @Test
    public void csv() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        table.write().csv(baos);
        String output = baos.toString();
        assertEquals("v,v2" + LINE_END +
                "1.0,1.0" + LINE_END +
                "2.0,2.0" + LINE_END +
                "," + LINE_END +
                "", output);
    }

    @Test
    public void csv2() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos);
        table.write().csv(osw);
        String output = baos.toString();
        assertEquals("v,v2" + LINE_END +
                "1.0,1.0" + LINE_END +
                "2.0,2.0" + LINE_END +
                "," + LINE_END +
                "", output);
    }
}