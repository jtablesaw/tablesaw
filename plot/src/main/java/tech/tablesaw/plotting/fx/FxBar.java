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

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class FxBar extends FxBuilder {

    public static BarChart<String, Number> chart(String title, Table table, String categoryColumnName, String
            numericColumnName) {

        CategoryColumn categoryColumn = table.categoryColumn(categoryColumnName);
        NumericColumn numericColumn = table.nCol(numericColumnName);

        return chart(title, categoryColumn, numericColumn);
    }

    public static BarChart<String, Number> chart(String title, CategoryColumn categoryColumn, NumericColumn
            numericColumn) {

        final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
        final NumberAxis numberAxis = getNumberAxis(numericColumn);

        final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
        List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new XYChart.Data<>(categoryColumn.get(i), numericColumn.getDouble(i)));
        }

        barChart.getData().add(getSeries(numericColumn, data));
        return barChart;
    }

    public static BarChart<String, Number> chart(String title, ShortColumn categoryColumn, NumericColumn
            numericColumn) {

        final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
        final NumberAxis numberAxis = getNumberAxis(numericColumn);

        final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
        List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new XYChart.Data<>(categoryColumn.getString(i), numericColumn.getDouble(i)));
        }

        barChart.getData().add(getSeries(numericColumn, data));
        return barChart;
    }

    public static BarChart<String, Number> chart(String title, IntColumn categoryColumn, NumericColumn numericColumn) {

        final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
        final NumberAxis numberAxis = getNumberAxis(numericColumn);

        final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
        List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new XYChart.Data<>(categoryColumn.getString(i), numericColumn.getDouble(i)));
        }

        barChart.getData().add(getSeries(numericColumn, data));
        return barChart;
    }
}

