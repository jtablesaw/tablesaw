package com.deathrayresearch.outlier.sorting;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.IntColumn;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SortTest {

  private final Table table = new Table("test");

  @Before
  public void setUp() throws Exception {
    int size = 10;
    IntColumn c1 = IntColumn.create("c1");
    IntColumn c2 = IntColumn.create("c2");

    for (int i = 0; i < size; i++) {
      c1.add(i);
      c2.add(size - i);
    }

    table.addColumn(c1);
    table.addColumn(c2);
    System.out.println(table.print());
  }

  @Test
  public void sort() {
    Table t2 = table.sortOn("c1");
    System.out.println(t2.print());
    t2 = table.sortOn("c2");
    System.out.println(t2.print());
  }
}