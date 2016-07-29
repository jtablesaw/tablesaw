package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.plot.TimeSeries;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 *
 */
public class TimeSeriesExample {

  public static void main(String[] args) throws Exception {
    Table air = Table.createFromCsv("data/BushApproval.csv");
    air = air.selectWhere(column("who").isEqualTo("fox"));
    System.out.println(air.shape());
    DateColumn dates = air.dateColumn("date");
    NumericColumn approval = air.nCol("approval");
    TimeSeries.show("Fox news poll: George W Bush", dates, approval);
  }

}
