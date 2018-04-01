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
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FxBar extends FxBuilder {

    public static BarChart<String, Number> chart(String title, Table table, String categoryColumnName, String
            numberColumnName) {

        StringColumn stringColumn = table.stringColumn(categoryColumnName);
        NumberColumn numberColumn = table.nCol(numberColumnName);

        return chart(title, stringColumn, numberColumn);
    }

    public static BarChart<String, Number> chart(String title, StringColumn stringColumn, NumberColumn
            numberColumn) {

        final CategoryAxis categoryAxis = getCategoryAxis(stringColumn);
        final NumberAxis numberAxis = getNumberAxis(numberColumn);

        final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
        List<XYChart.Data<String, Number>> data = new ArrayList<>(stringColumn.size());

        for (int i = 0; i < stringColumn.size(); i++) {
            data.add(new XYChart.Data<>(stringColumn.get(i), numberColumn.get(i)));
        }

        barChart.getData().add(getSeries(numberColumn, data));
        return barChart;
    }

    public static BarChart<String, Number> chart(String title, NumberColumn categoryColumn, NumberColumn numberColumn) {

        final CategoryAxis categoryAxis = getCategoryAxis(categoryColumn);
        final NumberAxis numberAxis = getNumberAxis(numberColumn);

        final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
        List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new XYChart.Data<>(categoryColumn.getString(i), numberColumn.get(i)));
        }

        barChart.getData().add(getSeries(numberColumn, data));
        return barChart;
    }
}

