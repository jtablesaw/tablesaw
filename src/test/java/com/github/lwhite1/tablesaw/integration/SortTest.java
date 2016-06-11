package com.github.lwhite1.tablesaw.integration;

import com.github.lwhite1.tablesaw.columns.IntColumn;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.junit.Test;

/**
 *
 */
public class SortTest {

  @Test
  public void testName() {

    int[] values = new int[10];

    for (int i = 0; i < 10; i++) {
      values[i] = 10 - i;
    }
    System.out.println(java.util.Arrays.toString(values));

    Arrays.quickSort(0, values.length, new IntComparator() {
      @Override
      public int compare(int i, int i1) {
        return i1 - i;
      }

      @Override
      public int compare(Integer o1, Integer o2) {
        return o1 - o2;
      }
    }, new Swapper() {

      @Override
      public void swap(int a, int b) {
        int temp = values[a];
        values[a] = values[b];
        values[b] = temp;
      }
    });

    IntColumn newColumn = IntColumn.create("test");

    System.out.println(java.util.Arrays.toString(values));
    for (int i = 0; i < values.length; i++) {
      newColumn.add(values[i]);
    }

    newColumn.print();
  }
}
