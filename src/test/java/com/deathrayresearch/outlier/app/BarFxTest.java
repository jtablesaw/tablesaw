package com.deathrayresearch.outlier.app;

import static com.deathrayresearch.outlier.columns.ColumnType.*;
import static com.deathrayresearch.outlier.columns.ColumnType.TEXT;

import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.stage.Stage;

/**
 *
 */
public class BarFxTest extends Application {

  ColumnType[] heading = {
      FLOAT,   // number by year
      FLOAT,   // year
      FLOAT,   // month
      FLOAT,   // day
      LOCAL_DATE,  // date
      LOCAL_TIME,  // time
      TEXT,  // tz
      TEXT,  // st
      TEXT, // state fips
      FLOAT,   // state torn number
      FLOAT,   // scale
      FLOAT,   // injuries
      FLOAT,    // fatalities
      TEXT,  // loss
      FLOAT,  // crop loss
      FLOAT,  // St. Lat
      FLOAT,  // St. Lon
      FLOAT,  // End Lat
      FLOAT,  // End Lon
      FLOAT, // length
      FLOAT,   // width
      FLOAT,   // NS
      FLOAT,   // SN
      FLOAT,   // SG
      TEXT,  // Count FIPS 1-4
      TEXT,
      TEXT,
      TEXT};

  Table table;

  @Override
  public void start(Stage stage) throws Exception {

    BarChart<String, Number> sc = setup();

    stage.setTitle("Bar Chart Sample");

    Scene scene = new Scene(sc, 500, 400);
    stage.setScene(scene);
    stage.show();
  }

  public BarChart<String, Number> setup() throws Exception {

    table = CsvReader.read("data/1950-2014_torn.csv", heading);

/*
    table = table.rejectColumns(
        "State FIPS",
        "State No",
        "FIPS 1",
        "FIPS 2",
        "FIPS 3",
        "FIPS 4");
*/

    // eliminate the duplicate rows so we can sum by state correctly;
    //  table = table.select (valueOf("SN").isEqualTo(1));

/*
    Table xtab2 = CrossTab.xCount(table, table.textColumn("State"), table.floatColumn("Month"));
    xtab2 = xtab2.rejectIf(valueOf("value").isEqualTo("Total"));

*/
/*
    return Bar.chart("tornados by state",
        (TextColumn) xtab2.column("value"),
        (FloatColumn) xtab2.column("total"));
*/
    return null;
  }

  public static void main(String[] args) throws Exception {
    launch(args);
  }
}
