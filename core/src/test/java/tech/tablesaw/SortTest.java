/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.Table;

/**
 * Verify sorting functions
 */
public class SortTest {

    private final static int IQ_INDEX = 1;
    private final static int DOB_INDEX = 3;
    // Name,IQ,City,DOB
    private final static String[] columnNames = TestData.SIMPLE_UNSORTED_DATA.getColumnNames();
    private Table unsortedTable;

    @BeforeEach
    public void setUp() {
        unsortedTable = TestData.SIMPLE_UNSORTED_DATA.getTable();
    }

    @Test
    public void sortAscending() {
        // sort ascending by date and then an integer
        Table sortedTable = unsortedTable.sortAscendingOn("IQ", "DOB");
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    /**
     * Same as sortAscending but descending
     */
    @Test
    public void sortDescending() {
        unsortedTable = TestData.SIMPLE_UNSORTED_DATA.getTable();
        Table sortedTable = unsortedTable.sortDescendingOn("IQ", "DOB");
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    /**
     * Verify data that is not sorted descending does match data that has been
     * (this test verifies the accuracy of our positive tests)
     */
    @Disabled
    public void sortDescendingNegative() {
        Table sortedTable = unsortedTable.sortDescendingOn("IQ", "DOB");
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testMultipleSortOrdersVerifyMinus() {
        Table sortedTable = unsortedTable.sortOn("-" + columnNames[IQ_INDEX], "-" + columnNames[DOB_INDEX]);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testAscendingAndDescending() {
        Table sortedTable = unsortedTable.sortOn("+" + columnNames[IQ_INDEX], "-" + columnNames[DOB_INDEX]);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_ASCENDING_AND_THEN_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testMultipleSortOrdersVerifyPlus() {
        Table sortedTable = unsortedTable.sortOn("+" + columnNames[IQ_INDEX], "+" + columnNames[DOB_INDEX]);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);

        sortedTable = unsortedTable.sortOn(columnNames[IQ_INDEX], columnNames[DOB_INDEX]);
        expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testAscendingWithPlusSign() {
        Table sortedTable = unsortedTable.sortOn("+" + columnNames[IQ_INDEX]);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testSortOnIndices() {
        Table sortedTable = unsortedTable.sortOn(IQ_INDEX, DOB_INDEX);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testSortOnIndicesAscendingAndDescending() {
        Table sortedTable = unsortedTable.sortOn(IQ_INDEX, -DOB_INDEX);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_ASCENDING_AND_THEN_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Disabled
    public void testAscendingWithPlusSignNegative() {
        Table sortedTable = unsortedTable.sortOn("+" + columnNames[IQ_INDEX], "-" + columnNames[DOB_INDEX]);
        Table expectedResults = TestData.SIMPLE_DATA_WITH_CANONICAL_DATE_FORMAT.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    /**
     * Make sure each row in each table match
     *
     * @param compareWith the table that was sorted using some external means e.g. excel. i.e known good data
     * @param sortedTable the table that was sorted with Tablesaw
     */
    private void assertTablesEquals(Table compareWith, Table sortedTable) {
        assertEquals(sortedTable.rowCount(), compareWith.rowCount(), "both tables have the same number of rows");
        int maxRows = sortedTable.rowCount();
        int numberOfColumns = sortedTable.columnCount();
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                assertEquals(sortedTable.get(rowIndex, columnIndex), compareWith.get(rowIndex, columnIndex),
                        "cells[" + rowIndex + ", " + columnIndex + "] do not match");
            }
        }
    }

}