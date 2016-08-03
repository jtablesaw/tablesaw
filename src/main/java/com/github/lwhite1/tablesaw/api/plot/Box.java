package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.plotting.smile.SmileBox;
import com.github.lwhite1.tablesaw.table.ViewGroup;

/**
 *
 */
public class Box {

  public static void show(String title, ViewGroup groups, int columnIndex) {
    SmileBox.show(title, groups, columnIndex);
  }

  public static void show(String title, Table table, String summaryColumnName, String groupingColumnName) {
    SmileBox.show(title, table, summaryColumnName, groupingColumnName);
  }
}
