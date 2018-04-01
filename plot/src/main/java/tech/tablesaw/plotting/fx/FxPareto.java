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

import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class FxPareto extends FxBuilder {

    public static BarChart<String, Number> chart(
            String title,
            StringColumn x,
            NumberColumn y) {

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
            d2.add(new XYChart.Data<>(t.stringColumn(0).get(i), t.nCol(1).get(i)));
        }
        XYChart.Series<String, Number> series1
                = new XYChart.Series<>(FXCollections.observableList(d2));

        series1.setName(y.name());

        bar.setLegendVisible(false);
        bar.setCategoryGap(0.0);
        bar.setBarGap(0.1);
        bar.setBackground(Background.EMPTY);
        bar.setVerticalGridLinesVisible(false);

        bar.getData().add(series1);
        return bar;
    }

    public static BarChart<String, Number> chart(String title, NumberColumn categoryColumn, NumberColumn numberColumn) {

        Table t = Table.create("", categoryColumn, numberColumn);
        t = t.sortDescendingOn(numberColumn.name());

        final CategoryAxis categoryAxis = getCategoryAxis(t.stringColumn(0));
        final NumberAxis numberAxis = getNumberAxis(t.numberColumn(1));

        final BarChart<String, Number> barChart = getBarChart(title, categoryAxis, numberAxis);
        List<XYChart.Data<String, Number>> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new XYChart.Data<>(categoryColumn.getString(i), numberColumn.get(i)));
        }

        barChart.getData().add(getSeries(numberColumn, data));
        return barChart;
    }
}

