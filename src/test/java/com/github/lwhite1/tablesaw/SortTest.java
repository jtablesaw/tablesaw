package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.Table;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verify sorting functions
 */
public class SortTest {

    @Test
    public void sortAscending() {

        // start with unsorted data
        Table table = TestData.SIMPLE_UNSORTED_DATA.getTable();

        // sort ascending by date and then an integer
        Table sortedTable = table.sortAscendingOn("DOB", "IQ");

        // get the data that has been presorted correctly - i.e. known good results
        Table compareWith = TestData.SIMPLE_SORTED_DATA_BY_INTEGER_AND_DATE_ASCENDING.getTable();

        compareTables(sortedTable, compareWith);
    }

    /**
     * Same as sortAscending but descending
     */
    @Test
    public void sortDescending() {
        Table table = TestData.SIMPLE_UNSORTED_DATA.getTable();
        Table sortedTable = table.sortDescendingOn("DOB", "IQ");
        Table compareWith = TestData.SIMPLE_SORTED_DATA_BY_INTEGER_AND_DATE_DESCENDING.getTable();
        compareTables(sortedTable, compareWith);
    }

    /**
     * Verify data that is not sorted descending does match data that has been
     * (this test verifies the accuracy of our positive tests)
     */
    @Test(expected = ComparisonFailure.class)
    public void sortDescendingNegative() {
        Table table = TestData.SIMPLE_UNSORTED_DATA.getTable();
        Table sortedTable = table.sortDescendingOn("DOB", "IQ");
        Table compareWith = TestData.SIMPLE_SORTED_DATA_BY_INTEGER_AND_DATE_ASCENDING.getTable();
        compareTables(sortedTable, compareWith);
    }

    @Test
    public void testMultipleSortOrders() {
        Table unsortedTable = TestData.SIMPLE_UNSORTED_DATA.getTable();
        // Name,IQ,City,DOB
        String[] columnNames = TestData.SIMPLE_UNSORTED_DATA.getColumnNames();
        int iqIndex = 1;
        int dobIndex = 3;

        Table sortedTable = unsortedTable.sortOn(columnNames[iqIndex], "-" + columnNames[dobIndex]);

        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_INTEGER_DESCENDING.getTable();

        assertEquals("Data sorted correctly", expectedResults, sortedTable);
    }


    /**
     * Make sure each row in each table match
     *
     * @param sortedTable the table that was sorted with tablesaw
     * @param compareWith the table that was sorted using some external means e.g. excel. i.e known good data
     */
    private void compareTables(Table sortedTable, Table compareWith) {
        assertEquals("both tables have the same number of rows", sortedTable.rowCount(), compareWith.rowCount());
        int maxRows = sortedTable.rowCount();
        int numberOfColumns = sortedTable.columnCount();
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                assertEquals("cells[" + rowIndex + ", " + columnIndex + "]  match",
                        sortedTable.get(rowIndex, columnIndex), compareWith.get(rowIndex, columnIndex));
            }
        }
    }

}