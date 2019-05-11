package tech.tablesaw.io.string;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

public class DataFramePrinterTest {

    @Test
    public void printNull() {
        DoubleColumn col = DoubleColumn.create("testCol");
        col.append(5.0);
        col.appendCell(null);
        col.append(3.0);
        Table table = Table.create("nullCellTable", col);
        String out = table.print();
        assertTrue(out.contains("          "));
    }

    @Test
    public void printOneRow() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        String out = table.print(1);
        assertTrue(out.contains("2004-02-04"));
        assertTrue(out.contains("53"));
        assertTrue(out.contains("fox"));
    }

    
}
