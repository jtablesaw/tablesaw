package com.deathrayresearch.outlier.sorting;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.store.StorageManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class SortTest {

  private Table table1;
  private Table table2;

  private CategoryColumn column1;
  private CategoryColumn column2;

  @Before
  public void setUp() throws Exception {
    table1 = StorageManager.readTable("bigdata/3f07b9bf-053f-4f9b-9dff-9d354835b276");
    table2 = StorageManager.readTable("bigdata/3f07b9bf-053f-4f9b-9dff-9d354835b276");

    column1 = table1.categoryColumn("ORIGIN");
    column2 = table2.categoryColumn("ORIGIN");
  }

  @Ignore
  @Test
  public void sort() {

    Sort key = Table.getSort("ORIGIN");
    IntComparator rowComparator = table1.getComparator(key);

    int[] t1rows = table1.rows();
    IntArrayList rows1 = IntArrayList.wrap(t1rows);
    Collections.sort(rows1, rowComparator);

    IntComparator rowComparator2 = table2.getComparator(key);
    int[] values = table2.rows();

    IntArrays.mergeSort(values, 0, values.length, rowComparator2);
    assertTrue(java.util.Arrays.equals(values, rows1.elements()));
  }

  @Ignore
  @Test
  public void sortColumn() {

    IntComparator rowComparator1 = column1.rowComparator();
    IntComparator rowComparator2 = column2.rowComparator();

    IntArrayList t1rows = IntArrayList.wrap(column1.indexes());
    Collections.sort(t1rows, rowComparator1);

    int[] values = column2.indexes();

    IntArrays.mergeSort(values, 0, values.length, rowComparator2);
    assertTrue(java.util.Arrays.equals(values, t1rows.elements()));
  }
}