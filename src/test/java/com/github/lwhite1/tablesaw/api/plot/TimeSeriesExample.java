package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.plotting.glimpse.GlimpseTimeSeries;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 * NOTE: Times series plotting is not yet a supported feature, and may be removed at any time
 */
public class TimeSeriesExample {

  public static void main(String[] args) throws Exception {
    Table air = Table.createFromCsv("data/BushApproval.csv");
    air = air.selectWhere(column("who").isEqualTo("fox"));
    System.out.println(air.shape());
    DateColumn dates = air.dateColumn("date");
    NumericColumn approval = air.nCol("approval");
    GlimpseTimeSeries.show("Fox news poll: George W Bush", dates, approval);
  }

}
