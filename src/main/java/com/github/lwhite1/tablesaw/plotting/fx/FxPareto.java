package com.github.lwhite1.tablesaw.plotting.fx;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class FxPareto extends FxBuilder {

  public static BarChart<String, Number> chart(
      String title,
      CategoryColumn x,
      NumericColumn y) {

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel(x.name());
    yAxis.setLabel(y.name());

    Table t = Table.create("", x, y);
    t = t.sortDescendingOn(y.name());

    final BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);

    bar.setTitle(title);

    List<XYChart.Data<String, Number>> d2 = new ArrayList<>(x.size());

    for (int i = 0; i < x.size(); i++) {
      d2.add(new XYChart.Data<>(t.categoryColumn(0).get(i), t.nCol(1).getFloat(i)));
    }
    XYChart.Series<String, Number> series1
        = new XYChart.Series<>(FXCollections.observableList(d2));

    series1.setName(y.name());

    bar.setLegendVisible(false);
    bar.setCategoryGap(0.0);
    bar.setBarGap(0.1);
    bar.setBackground(Background.EMPTY);
    bar.setVerticalGridLinesVisible(false);

    bar.getData().addAll(series1);
    return bar;
  }

  public static BarChart<String, Number> chart(String title, IntColumn categoryColumn, NumericColumn numericColumn) {

    Table t = Table.create("", categoryColumn, numericColumn);
    t = t.sortDescendingOn(numericColumn.name());

    final CategoryAxis categoryAxis = getCategoryAxis(t.categoryColumn(0));
    final NumberAxis numberAxis = getNumberAxis(t.numericColumn(1));

    final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
    List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

    for (int i = 0; i < categoryColumn.size(); i++) {
      data.add(new XYChart.Data<>(categoryColumn.getString(i), numericColumn.getFloat(i)));
    }

    barChart.getData().addAll(getSeries(numericColumn, data));
    return barChart;
  }

  public static BarChart<String, Number> chart(String title, ShortColumn categoryColumn, NumericColumn numericColumn) {

    Table t = Table.create("", categoryColumn, numericColumn);
    t = t.sortDescendingOn(numericColumn.name());

    final CategoryAxis categoryAxis = getCategoryAxis(t.categoryColumn(0));
    final NumberAxis numberAxis = getNumberAxis(t.numericColumn(1));

    final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
    List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

    for (int i = 0; i < categoryColumn.size(); i++) {
      data.add(new XYChart.Data<>(categoryColumn.getString(i), numericColumn.getFloat(i)));
    }

    barChart.getData().addAll(getSeries(numericColumn, data));
    return barChart;
  }

}

