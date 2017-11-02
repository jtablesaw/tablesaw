/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.plotting.fx;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;

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

    static PieChart getPieChart(String title) {

        PieChart pie = new PieChart();
        pie.setTitle(title);
        pie.setLegendVisible(false);
        pie.setBackground(Background.EMPTY);
        pie.setLegendVisible(true);
        pie.setLegendSide(Side.RIGHT);
        return pie;
    }
}
