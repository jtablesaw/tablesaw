package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.io.CsvReader;
import com.github.lwhite1.tablesaw.table.SubTable;
import com.github.lwhite1.tablesaw.table.TableGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 *
 */
public class TableGroupTest {

  private static ColumnType[] types = {
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
  public void testGetSubTables() {
    TableGroup tableGroup = new TableGroup(table, table.column("who"));
    List<SubTable> tables = tableGroup.getSubTables();
    System.out.println(tables.size());
  }

  @Test
  public void testWith2GroupingCols() {
    CategoryColumn month = table.localDateColumn(0).month();
    month.setName("month");
    table.addColumn(month);
    String[] splitColumnNames = {table.column(2).name(), "month"};
    TableGroup tableGroup = new TableGroup(table, splitColumnNames);
    List<SubTable> tables = tableGroup.getSubTables();
    Table t = table.sum(table.intColumn(1), splitColumnNames);

    System.out.println(t.print());
    System.out.println(tables.size());
  }

  @Test
  public void testCountByGroup() {
    System.out.println(table.columnNames());
    Table groups = table.countBy("who");
    System.out.println(groups.print());
  }

  @Test
  public void testSumGroup() {
    System.out.println(table.columnNames());
    Table groups = table.sum(table.intColumn(1), table.categoryColumn(2));
    System.out.println(groups.print());
  }
}