package tech.tablesaw.api;

import org.junit.Test;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.File;
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
    public void testGetShort() throws IOException {
        Table table = Table.read().csv(
                CsvReadOptions.builder(new File("../data/bush.csv"))
                    .minimizeColumnSizes(true));
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            assertEquals(table.shortColumn(1).getShort(row.getRowNumber()),
                    row.getShort(1));
            assertEquals(table.shortColumn("approval").getShort(row.getRowNumber()),
                    row.getShort("approval"));
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

    @Test
    public void testSetDate() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            LocalDate date = table.dateColumn("date").get(row.getRowNumber());

            // test setDate(index, value)
            LocalDate dateIncrementedByOne = date.plusDays(1);
            row.setDate(0, dateIncrementedByOne);
            assertEquals(dateIncrementedByOne, row.getDate(0));

            // test setDate(key, value)
            LocalDate dateIncrementedByTwo = date.plusDays(2);
            row.setDate("date", dateIncrementedByTwo);
            assertEquals(dateIncrementedByTwo, row.getDate("date"));

//            // test setObject(key, value)
//            LocalDate dateIncrementedByThree = date.plusDays(3);
//            row.setObject("date", dateIncrementedByThree);
//            assertEquals(dateIncrementedByThree, row.getObject("date"));
//
//            // test setObject(index, value)
//            LocalDate dateIncrementedByFour = date.plusDays(4);
//            row.setObject(0, dateIncrementedByFour);
//            assertEquals(dateIncrementedByFour, row.getObject(0));
        }
    }

    @Test
    public void testSetShort() throws IOException {
        Table table = Table.read().csv(
                CsvReadOptions.builder(new File("../data/bush.csv"))
                        .minimizeColumnSizes(true));
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            Short rowVal = table.shortColumn(1).getShort(row.getRowNumber());

            // setShort(columnIndex, value)
            row.setShort(1, (short)(rowVal + 1));
            assertEquals((short)(rowVal + 1),
                    row.getShort(1));

            // setShort(columnName, value)
            row.setShort("approval", (short)(rowVal + 2));
            assertEquals(rowVal + 2,
                    row.getShort("approval"));
        }
    }

    @Test
    public void testSetString() throws IOException {
        Table table = Table.read().csv("../data/bush.csv");
        Row row = new Row(table);
        while (row.hasNext()) {
            row.next();
            String rowVal = table.stringColumn(2).get(row.getRowNumber());
            String updateVal1 = rowVal.concat("2");
            String updateVal2 = rowVal.concat("3");

            // setString(columnIndex, value)
            row.setString(2, updateVal1);
            assertEquals(updateVal1,
                    row.getString(2));

            // setString(columnName, value)
            row.setString("who", updateVal2);
            assertEquals(updateVal2,
                    row.getString("who"));
        }
    }


}