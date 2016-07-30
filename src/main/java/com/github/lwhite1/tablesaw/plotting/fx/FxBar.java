package com.github.lwhite1.tablesaw.plotting.fx;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class FxBar extends FxBuilder{

  public static BarChart<String, Number> chart(String title, Table table, String categoryColumnName, String numericColumnName) {

    CategoryColumn categoryColumn = table.categoryColumn(categoryColumnName);
    NumericColumn numericColumn = table.nCol(numericColumnName);

    return chart(title, categoryColumn, numericColumn);
  }

  public static BarChart<String, Number> chart(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) {

    final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
    final NumberAxis numberAxis = getNumberAxis(numericColumn);

    final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
    List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

    for (int i = 0; i < categoryColumn.size(); i++) {
      data.add(new XYChart.Data<>(categoryColumn.get(i), numericColumn.getFloat(i)));
    }

    barChart.getData().addAll(getSeries(numericColumn, data));
    return barChart;
  }

  public static BarChart<String, Number> chart(String title, ShortColumn categoryColumn, NumericColumn numericColumn) {

    final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
    final NumberAxis numberAxis = getNumberAxis(numericColumn);

    final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
    List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

    for (int i = 0; i < categoryColumn.size(); i++) {
      data.add(new XYChart.Data<>(categoryColumn.getString(i), numericColumn.getFloat(i)));
    }

    barChart.getData().addAll(getSeries(numericColumn, data));
    return barChart;
  }

  public static BarChart<String, Number> chart(String title, IntColumn categoryColumn, NumericColumn numericColumn) {

    final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
    final NumberAxis numberAxis = getNumberAxis(numericColumn);

    final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
    List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

    for (int i = 0; i < categoryColumn.size(); i++) {
      data.add(new XYChart.Data<>(categoryColumn.getString(i), numericColumn.getFloat(i)));
    }

    barChart.getData().addAll(getSeries(numericColumn, data));
    return barChart;
  }
}

