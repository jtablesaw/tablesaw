package com.github.lwhite1.tablesaw.api.plot.swing;

import com.github.lwhite1.tablesaw.api.Table;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.min;

/**
 *
 */
public class BarTest {

  public static void main(String[] args) throws Exception {
    Table bush = Table.createFromCsv("data/BushApproval.csv");
    Bar.show(bush, bush.categoryColumn("who"));

  //  SummaryFunction summary = bush.summarize("approval", median);
  //  Bar.show(summary, "who");
    Bar.show(bush.summarize("approval", min).by("who"));

  }
}