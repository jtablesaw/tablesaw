package com.deathrayresearch.outlier.app.plot;

import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.TextColumn;
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
public class Bar {

  public static BarChart<String, Number> chart(
      String title,
      TextColumn x,
      FloatColumn y) {

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel(x.name());
    yAxis.setLabel(y.name());

    final BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);

    bar.setTitle(title);

    List<XYChart.Data<String, Number>> d2 = new ArrayList<>(x.size());

    for (int i = 0; i < x.size(); i++) {
      d2.add(new XYChart.Data<>(x.get(i), (Number) y.get(i)));
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
}

