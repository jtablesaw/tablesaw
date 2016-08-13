package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ml.classification.ConfusionMatrix;
import com.github.lwhite1.tablesaw.api.ml.classification.Knn;

/**
 *
 */
public class SfCrimeTest {

  public static void main(String[] args) throws Exception {

    Table crime = Table.createFromCsv("/Users/larrywhite/IdeaProjects/testdata/bigdata/train.csv");

    out(crime.shape());
    out(crime.structure().print());

    out(crime.first(4).print());

    crime.removeColumns("DayOfWeek");

    ShortColumn year = crime.dateTimeColumn("Dates").year();
    year.setName("Year");
    crime.addColumn(year);

    CategoryColumn category = crime.categoryColumn("Category");
    Table categorySummary = category.summary().sortDescendingOn("Count");
    out(categorySummary.print());

    ShortColumn minuteOfDay = crime.dateTimeColumn("Dates").minuteOfDay();
    minuteOfDay.setName("MinuteOfDay");
    crime.addColumn(minuteOfDay);

    ShortColumn dayOfYear = crime.dateTimeColumn("Dates").dayOfYear();
    dayOfYear.setName("DayOfYear");
    crime.addColumn(dayOfYear);

    ShortColumn dayOfWeekValue = crime.dateTimeColumn("Dates").dayOfWeekValue();
    dayOfWeekValue.setName("DayOfWeek");
    crime.addColumn(dayOfWeekValue);

    Table[] subTables = crime.sampleSplit(.5);
    Table train = subTables[0];
    Table test = subTables[1];

    Knn model = Knn.learn(15,
        train.categoryColumn("Category"),
        train.nCol("X"),
        train.nCol("Y"),
        train.nCol("MinuteOfDay"),
        train.nCol("DayOfYear"),
        train.nCol("DayOfWeek"),
        train.nCol("Year"));

    out("Model trained");

    ConfusionMatrix matrix = model.predictMatrix(test.categoryColumn("Category"),
        test.nCol("X"),
        test.nCol("Y"),
        test.nCol("MinuteOfDay"),
        test.nCol("DayOfYear"),
        test.nCol("DayOfWeek"),
        test.nCol("Year"));

    out(matrix.accuracy());
    out(matrix.toTable().print());

  }

  private static void out(Object str) {
    System.out.println(String.valueOf(str));
  }
}
