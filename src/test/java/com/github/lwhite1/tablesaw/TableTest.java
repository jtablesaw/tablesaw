package com.github.lwhite1.tablesaw;

import au.com.bytecode.opencsv.CSVReader;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for Table
 */
public class TableTest {

    private Table table;
    private FloatColumn column = new FloatColumn("f1");

    @Before
    public void setUp() throws Exception {
        table = new Table("t");
        table.addColumn(column);
    }

    @Test
    public void testColumn() throws Exception {
        Column column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    public void testColumnCount() throws Exception {
        assertEquals(0, new Table("t").columnCount());
        assertEquals(1, table.columnCount());
    }

    @Test
    public void testRowCount() throws Exception {
        assertEquals(0, table.rowCount());
        FloatColumn floatColumn = column;
        floatColumn.add(2f);
        assertEquals(1, table.rowCount());

        floatColumn.add(2.2342f);
        assertEquals(2, table.rowCount());
    }

    @Test
    public void testGetRow() throws Exception {

        Table table = TestData.SIMPLE_DATA_WITH_CANONICAL_DATE_FORMAT.getTable();
        Path source = TestData.SIMPLE_DATA_WITH_CANONICAL_DATE_FORMAT.getSource();
        List<String[]> rawData;
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(source, Charset.defaultCharset()))) {
            rawData = csvReader.readAll();
        }
        // remove the first row from the raw data since tables don't keep column header row as part of their data
        rawData.remove(0);
        int maxRows = table.rowCount();
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            String row = table.getRow(rowIndex);
            String expectedRow = getCSVfromListOfStrings(rawData.get(rowIndex));
            assertEquals("Row " + rowIndex + " matches", expectedRow,row);
        }
    }

    private String getCSVfromListOfStrings(String[] data) {
        StringJoiner stringJoiner = new StringJoiner(",");
        Arrays.asList(data).forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

}