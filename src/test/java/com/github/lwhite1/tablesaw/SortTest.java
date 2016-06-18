package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.io.CsvReader;
import com.github.lwhite1.tablesaw.sorting.Sort;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;

/**
 *
 */
public class SortTest {

  private ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CATEGORY        // polling org
  };

  private Table table1;
  private Table table2;
  private CategoryColumn column1;
  private IntColumn column2;
  private LocalDateColumn column3;

  @Before
  public void setUp() throws Exception {
    table1 = CsvReader.read(types, "data/BushApproval.csv");
    table2 = CsvReader.read(types, "data/BushApproval.csv");
    column1 = table1.categoryColumn("who");
    column2 = table1.intColumn("approval");
    column3 = table1.localDateColumn("date");
  }


  @Ignore
  @Test
  public void sort() {

    Sort key = Table.getSort("who");
    IntComparator rowComparator = table1.getComparator(key);

    // sort the table and then
    table1 = table1.sortOn(key);
    int[] t1rows = table1.rows();

    IntComparator rowComparator2 = table2.getComparator(key);
    int[] values = table2.rows();

    System.out.println(table1.print());

    IntArrays.mergeSort(values, 0, values.length, rowComparator2);
   // assertTrue(java.util.Arrays.equals(values, t1rows));
  }

  @Ignore
  @Test
  public void sortColumn() {

    IntComparator rowComparator1 = column1.rowComparator();
    IntComparator rowComparator2 = column2.rowComparator();

    IntArrayList t1rows = IntArrayList.wrap(column1.indexes());
    Collections.sort(t1rows, rowComparator1);

/*
    int[] values = column2.indexes();

    IntArrays.mergeSort(values, 0, values.length, rowComparator2);
    assertTrue(java.util.Arrays.equals(values, t1rows.elements()));
*/
  }
}