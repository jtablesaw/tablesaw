package com.github.lwhite1.tablesaw.plotting;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import org.junit.*;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;

/**
 *  Interactive tests for plotting
 */
public class PlotUtilsTest {

  private ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CATEGORY        // polling org
  };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
  }

  @Ignore
  @Test
  public void testPlot() {
    PlotUtils.plot(table.intColumn("approval"), table.intColumn("approval"));
  }

}