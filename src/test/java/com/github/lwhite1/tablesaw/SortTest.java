package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.Table;
import org.junit.Test;

/**
 * Verify sorting functions
 */
public class SortTest {

    @Test
    public void sort() {
        Table table = TestData.SIMPLE_UNSORTED_DATA.getTable();
        Table sortedTable = table.sortAscendingOn("DOB", "IQ");
        Table compareWith = TestData.SIMPLE_SORTED_DATA_BY_INTEGER_AND_DATE_ASCENDING.getTable();
    }

}