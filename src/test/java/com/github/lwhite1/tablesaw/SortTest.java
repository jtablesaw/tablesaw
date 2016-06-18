package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.Table;
import org.junit.Test;

/**
 *
 */
public class SortTest {

    @Test
    public void sort() {
        Table table = TestData.TORNADOES.getTable();
        Table sortedTable = table.sortAscendingOn("Fatalities", "State");

    }

}