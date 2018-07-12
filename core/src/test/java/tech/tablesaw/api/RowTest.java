package tech.tablesaw.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * TODO All the methods on this class should be tested carefully
 */
public class RowTest {

    @Test
    public void columnNames() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        assertEquals(table.columnNames(), row.columnNames());
    }
}