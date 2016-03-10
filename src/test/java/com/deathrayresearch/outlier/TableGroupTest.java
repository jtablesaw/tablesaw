package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.deathrayresearch.outlier.columns.ColumnType.*;

/**
 *
 */
public class TableGroupTest {

  ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CAT             // polling org
  };

  Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read("data/BushApproval.csv", types);
  }

  @Test
  public void testGetSubTables() {
    TableGroup tableGroup = new TableGroup(table, table.column(2).name());
    List<SubTable> tables = tableGroup.getSubTables();
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