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

import javafx.scene.chart.PieChart;
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
public class FxPie extends FxBuilder {

    public static PieChart chart(String title, Table table, String categoryColumnName, String numericColumnName) {

        CategoryColumn categoryColumn = table.categoryColumn(categoryColumnName);
        NumericColumn numericColumn = table.nCol(numericColumnName);

        return chart(title, categoryColumn, numericColumn);
    }

    public static PieChart chart(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new PieChart.Data(categoryColumn.getString(i), numericColumn.getDouble(i)));
        }

        return createChart(title, data);
    }

    public static PieChart chart(String title, ShortColumn categoryColumn, NumericColumn numericColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            String name = Short.toString(categoryColumn.get(i));
            data.add(new PieChart.Data(name, numericColumn.getDouble(i)));
        }

        return createChart(title, data);
    }

    public static PieChart chart(String title, IntColumn categoryColumn, NumericColumn numericColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            String name = Integer.toString(categoryColumn.get(i));
            data.add(new PieChart.Data(name, numericColumn.getDouble(i)));
        }

        return createChart(title, data);
    }

    /**
     * Independent of the type of data
     *
     */
    private static PieChart createChart(String title, List<PieChart.Data> data) {
        final PieChart pieChart = getPieChart(title);

        pieChart.getData().setAll(data);
        return pieChart;
    }
}
