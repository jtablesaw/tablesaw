package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tech.tablesaw.io.csv.CsvReadOptions;

class TableCompareRowsTest {

    private static final String SOURCE_FILE_NAME = "../data/missing_values.csv";
    private static Table testTable;

    @BeforeAll
    static void readTables() {
        testTable = Table.read().usingOptions(CsvReadOptions
            .builder(new File(SOURCE_FILE_NAME))
            .missingValueIndicator("-"));
    }
    
    @Test
    void testCompareRowsIdentical() {
        for(int i = 0; i < testTable.rowCount(); i++) {
            assertTrue(Table.compareRows(i, testTable, testTable), "Row " + i + " is not equal to itself");
        }
    }

    @Test
    void testCompareRowsDifferent() {
        Table differentTable = testTable.copy().sortDescendingOn("Sales");
        for(int i = 0; i < testTable.rowCount(); i++) {
            assertFalse(Table.compareRows(i, testTable, differentTable), "Row " + i + " is equal to a different row");
        }
    }

    @Test
    void testCompareRowsDifferentColumns() {
        Table differentTable = testTable.copy().removeColumns("Sales");
        for(int i = 0; i < testTable.rowCount(); i++) {
            assertFalse(Table.compareRows(i, testTable, differentTable), "Row " + i + " is equal to a row with less columns");
        }
    }
    
    @Test
    void testCompareRowsOutOfBound() {
        Table differentTable = testTable.copy().dropRows(0);
        int lastRowNumber = testTable.rowCount() - 1;
        assertThrows(IndexOutOfBoundsException.class,
            () -> Table.compareRows(lastRowNumber, testTable, differentTable),
            "Row outside range does not throw exception");
    }
    
}
