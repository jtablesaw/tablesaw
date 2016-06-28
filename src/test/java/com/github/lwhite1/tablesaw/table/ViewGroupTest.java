package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ViewGroupTest {

  private final ColumnType[] types = {
      ColumnType.LOCAL_DATE,     // date of poll
      ColumnType.INTEGER,        // approval rating (pct)
      ColumnType.CATEGORY        // polling org
  };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
  }

  @Test
  public void testViewGroupCreation() {

    ViewGroup group = new ViewGroup(table, table.column("who"));
    assertEquals(6, group.size());
    List<TemporaryView> viewList = group.getSubTables();

    int count = 0;
    for (TemporaryView view : viewList) {
      count += view.rowCount();
    }
    assertEquals(table.rowCount(), count);
  }

  @Test
  public void testViewTwoColumn() {

    ViewGroup group = new ViewGroup(table, table.column("who"), table.column("approval"));
    List<TemporaryView> viewList = group.getSubTables();

    int count = 0;
    for (TemporaryView view : viewList) {
      count += view.rowCount();
    }
    assertEquals(table.rowCount(), count);
  }

  @Test
  public void testWith2GroupingCols() {
    CategoryColumn month = table.dateColumn(0).month();
    month.setName("month");
    table.addColumn(month);
    String[] splitColumnNames = {table.column(2).name(), "month"};
    ViewGroup tableGroup = ViewGroup.create(table, splitColumnNames);
    List<TemporaryView> tables = tableGroup.getSubTables();
    Table t = table.sum(table.intColumn(1), splitColumnNames);

    // compare the sum of the original column with the sum of the sums of the group table
    assertEquals(table.intColumn(1).sum(), t.intColumn(1).sum());
    assertEquals(65, tables.size());
  }

  @Test
  public void testCountByGroup() {
    Table groups = table.countBy("who");
    assertEquals(2, groups.columnCount());
    assertEquals(6, groups.rowCount());
    CategoryColumn group = groups.categoryColumn(0);
    assertTrue(group.contains("fox"));
  }

  @Test
  public void testSumGroup() {
    Table groups = table.sum(table.intColumn(1), table.categoryColumn(2));
    // compare the sum of the original column with the sum of the sums of the group table
    assertEquals(table.intColumn(1).sum(), groups.intColumn(1).sum());
  }
}