package tech.tablesaw.api;

import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

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
            LocalDate date = table.dateColumn("date").get(row.getRowNumber());
            assertEquals(date, row.getDate(0));
            assertEquals(date, row.getDate("date"));
            assertEquals(date, row.getObject("date"));
            assertEquals(date, row.getObject(0));
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
    public void testGetInt() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.intColumn(1).getInt(row.getRowNumber()),
                    row.getInt(1));
            assertEquals(table.intColumn("approval").getInt(row.getRowNumber()),
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
                    row.getPackedDate(0));
            assertEquals(table.dateColumn("date").getIntInternal(row.getRowNumber()),
                    row.getPackedDate("date"));
        }
    }
}