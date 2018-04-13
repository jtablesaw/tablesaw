package tech.tablesaw.io;

import org.junit.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertEquals;

public class DataFrameWriterTest {

    private double[] v1 = {1, 2, 3, 4, 5, NaN};
    private double[] v2 = {1, 2, 3, 4, 5, NaN};
    private Table table = Table.create("t",
            DoubleColumn.create("v", v1),
            DoubleColumn.create("v2", v2)
    );

    @Test
    public void csv() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        table.write().csv(baos);
        String output = baos.toString();
        assertEquals("v,v2\n" +
                "1.0,1.0\n" +
                "2.0,2.0\n" +
                "3.0,3.0\n" +
                "4.0,4.0\n" +
                "5.0,5.0\n" +
                ",\n" +
                "", output);
    }

    @Test
    public void csv2() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos);
        table.write().csv(osw);
        String output = baos.toString();
        assertEquals("v,v2\n" +
                "1.0,1.0\n" +
                "2.0,2.0\n" +
                "3.0,3.0\n" +
                "4.0,4.0\n" +
                "5.0,5.0\n" +
                ",\n" +
                "", output);
    }

    @Test
    public void html() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        table.write().html(baos);
        String output = baos.toString();
        assertEquals("<thead>\n" +
                "<tr><th>v</th><th>v 2</th></tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                "<tr><td>1.0</td><td>1.0</td></tr>\n" +
                "<tr><td>2.0</td><td>2.0</td></tr>\n" +
                "<tr><td>3.0</td><td>3.0</td></tr>\n" +
                "<tr><td>4.0</td><td>4.0</td></tr>\n" +
                "<tr><td>5.0</td><td>5.0</td></tr>\n" +
                "<tr><td></td><td></td></tr>\n" +
                "</tbody>\n", output);
    }
}