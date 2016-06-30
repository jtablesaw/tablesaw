package com.github.lwhite1.tablesaw.plotting.plotly;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class XySeriesTest {

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read("data/BushApproval.csv");
  }

  @Test
  public void testAsString() {
    XySeries<DateColumn, ShortColumn> series = new XySeries<>(table.dateColumn("date"), table.shortColumn("approval"));

    //System.out.println(series.asString(1));
  }

}