package com.github.lwhite1.tablesaw.plotting.fx;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;

import java.util.ArrayList;
import java.util.List;

/**
 * A JavaFx-based Horizontal bar chart
 */
public class FxHorizontalBar extends FxBuilder {

  public static BarChart<Number, String> chart(
      String title,
      Table table,
      String categoryColumnName,
      String numericColumnName) {

    CategoryColumn categoryColumn = table.categoryColumn(categoryColumnName);
    NumericColumn numericColumn = table.nCol(numericColumnName);

    return chart(title, categoryColumn, numericColumn);
  }

  public static BarChart<Number, String> chart(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) {

    final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
    final NumberAxis numberAxis = getNumberAxis(numericColumn);

    final BarChart<Number, String> bar = getNumberStringBarChart(title, numberAxis, categoryAxis);
    List<XYChart.Data<Number, String>> d2 = new ArrayList<>(numericColumn.size());

    for (int i = 0; i < numericColumn.size(); i++) {
      d2.add(new XYChart.Data<>(numericColumn.getFloat(i), categoryColumn.get(i)));
    }

    XYChart.Series<Number, String> series1 = getNumberStringSeries(categoryColumn, d2);

    bar.getData().addAll(series1);
    return bar;
  }

  public static BarChart<Number, String> chart(String title, ShortColumn categoryColumn, NumericColumn numericColumn) {

    final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
    final NumberAxis numberAxis = getNumberAxis(numericColumn);

    final BarChart<Number, String> bar = getNumberStringBarChart(title, numberAxis, categoryAxis);
    List<XYChart.Data<Number, String>> d2 = new ArrayList<>(numericColumn.size());

    for (int i = 0; i < numericColumn.size(); i++) {
      d2.add(new XYChart.Data<>(numericColumn.getFloat(i), categoryColumn.getString(i)));
    }

    XYChart.Series<Number, String> series1 = getNumberStringSeries(categoryColumn, d2);

    bar.getData().addAll(series1);
    return bar;
  }

  public static BarChart<Number, String> chart(String title, IntColumn categoryColumn, NumericColumn numericColumn) {

    final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
    final NumberAxis numberAxis = getNumberAxis(numericColumn);

    final BarChart<Number, String> bar = getNumberStringBarChart(title, numberAxis, categoryAxis);
    List<XYChart.Data<Number, String>> d2 = new ArrayList<>(numericColumn.size());

    for (int i = 0; i < numericColumn.size(); i++) {
      d2.add(new XYChart.Data<>(numericColumn.getFloat(i), categoryColumn.getString(i)));
    }

    XYChart.Series<Number, String> series1 = getNumberStringSeries(categoryColumn, d2);

    bar.getData().addAll(series1);
    return bar;
  }

  private static XYChart.Series<Number, String> getNumberStringSeries(Column categoryColumn, List<XYChart.Data<Number, String>> d2) {
    XYChart.Series<Number, String> series1 = new XYChart.Series<>(FXCollections.observableList(d2));
    series1.setName(categoryColumn.name());
    return series1;
  }

  private static BarChart<Number, String> getNumberStringBarChart(String title, NumberAxis xAxis, CategoryAxis yAxis) {
    final BarChart<Number, String> bar = new BarChart<>(xAxis, yAxis);
    bar.setTitle(title);
    bar.setLegendVisible(false);
    bar.setCategoryGap(0.0);
    bar.setBarGap(0.25);
    bar.setBackground(Background.EMPTY);
    bar.setHorizontalGridLinesVisible(false);
    return bar;
  }
}

