package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
}