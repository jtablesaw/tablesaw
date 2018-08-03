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


    @Test
    public void testGetDate() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.dateColumn(0).get(row.getRowNumber()),
                    row.getDate(0));
            assertEquals(table.dateColumn("date").get(row.getRowNumber()),
                    row.getDate("date"));
        }
    }

    @Test
    public void testGetDate2() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.dateColumn("date").get(row.getRowNumber()),
                    row.getDate("DATE"));
        }
    }

    @Test
    public void testGetDouble() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.numberColumn(1).get(row.getRowNumber()),
                    row.getDouble(1), 0.00001);
            assertEquals(table.numberColumn("approval").get(row.getRowNumber()),
                    row.getDouble("approval"), 0.00001);
        }
    }

    @Test
    public void testGetInt() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals((int) table.numberColumn(1).get(row.getRowNumber()),
                    row.getInt(1));
            assertEquals((int) table.numberColumn("approval").get(row.getRowNumber()),
                    row.getInt("approval"));
        }
    }

    @Test
    public void testGetString() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.stringColumn(2).get(row.getRowNumber()),
                    row.getString(2));
            assertEquals(table.stringColumn("who").get(row.getRowNumber()),
                    row.getString("who"));
        }
    }

    @Test
    public void testGetPackedDate() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.dateColumn(0).getIntInternal(row.getRowNumber()),
                    row.getPackedDate(0).getPackedValue());
            assertEquals(table.dateColumn("date").getIntInternal(row.getRowNumber()),
                    row.getPackedDate("date").getPackedValue());
        }
    }
}