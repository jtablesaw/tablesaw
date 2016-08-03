package com.github.lwhite1.tablesaw.plotting.fx;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;

import java.util.List;

/**
 *
 */
public class FxBuilder {

  static NumberAxis getNumberAxis(NumericColumn numericColumn) {
    final NumberAxis numberAxis = new NumberAxis();
    numberAxis.setLabel(numericColumn.name());
    return numberAxis;
  }

  static CategoryAxis getCategoryAxis(Column categoryColumn) {
    final CategoryAxis categoryAxis = new CategoryAxis();
    categoryAxis.setLabel(categoryColumn.name());
    return categoryAxis;
  }

  static XYChart.Series<String, Number> getSeries(NumericColumn numericColumn, List<XYChart.Data<String, Number>> data) {
    XYChart.Series<String, Number> series1
        = new XYChart.Series<>(FXCollections.observableList(data));

    series1.setName(numericColumn.name());
    return series1;
  }

  static BarChart<String, Number> getBarChart(String title, CategoryAxis categoryAxis, NumberAxis numberAxis) {

    BarChart<String, Number> bar = new BarChart<>(categoryAxis, numberAxis);
    bar.setTitle(title);
    bar.setLegendVisible(false);
    bar.setCategoryGap(0.0);
    bar.setBarGap(0.1);
    bar.setBackground(Background.EMPTY);
    bar.setVerticalGridLinesVisible(false);
    return bar;
  }
}
