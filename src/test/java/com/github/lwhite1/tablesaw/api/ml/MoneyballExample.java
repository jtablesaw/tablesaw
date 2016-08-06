package com.github.lwhite1.tablesaw.api.ml;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ml.regression.LeastSquares;
import com.github.lwhite1.tablesaw.api.plot.Histogram;
import com.github.lwhite1.tablesaw.api.plot.Scatter;
import com.github.lwhite1.tablesaw.columns.Column;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 * An example doing ordinary least squares regression
 */
public class MoneyballExample {

  public static void main(String[] args) throws Exception {

    // Get the data
    Table baseball = Table.createFromCsv("data/baseball.csv");
    out(baseball.structure().print());

    // filter to the data available in the 2002 season
    Table moneyball = baseball.selectWhere(column("year").isLessThan(2002));

    // plot regular season wins against year, segregating on whether the team made the plays
    NumericColumn wins = moneyball.numericColumn("W");
    NumericColumn year = moneyball.numericColumn("Year");
    Column playoffs = moneyball.column("Playoffs");
    Scatter.show("Regular season wins by year", wins, year, moneyball.splitOn(playoffs));

    // Calculate the run difference for use in the regression model
    IntColumn runDifference = moneyball.shortColumn("RS").subtract(moneyball.shortColumn("RA"));
    moneyball.addColumn(runDifference);
    runDifference.setName("RD");

    // Plot RD vs Wins to see if the relationship looks linear
    Scatter.show("RD x Wins", moneyball.numericColumn("RD"), moneyball.numericColumn("W"));

    // Create the regression model
    //ShortColumn wins = moneyball.shortColumn("W");
    LeastSquares winsModel = LeastSquares.train(wins, runDifference);
    out(winsModel);

    // Make a prediction of how many games we win if we score 135 more runs than our opponents
    double[] testValue = new double[1];
    testValue[0] = 135;
    double prediction = winsModel.predict(testValue);
    out("Predicted wins with RD = 135: " + prediction);

    // Predict runsScored based on On-base percentage, batting average and slugging percentage

    LeastSquares runsScored = LeastSquares.train(moneyball.nCol("RS"),
        moneyball.nCol("OBP"), moneyball.nCol("BA"), moneyball.nCol("SLG"));
    out(runsScored);

    LeastSquares runsScored2 = LeastSquares.train(moneyball.nCol("RS"),
        moneyball.nCol("OBP"), moneyball.nCol("SLG"));
    out(runsScored2);

    Histogram.show(runsScored2.residuals());

    Scatter.fittedVsResidual(runsScored2);
    Scatter.actualVsFitted(runsScored2);

    // We use opponent OBP and opponent SLG to model the efficacy of our pitching and defence

    Table moneyball2 = moneyball.selectWhere(column("year").isGreaterThan(1998));
    LeastSquares runsAllowed = LeastSquares.train(moneyball2.nCol("RA"),
        moneyball2.nCol("OOBP"), moneyball2.nCol("OSLG"));
    out(runsAllowed);

  }

  private static void out(Object o) {
    System.out.println(String.valueOf(o));
  }
}
