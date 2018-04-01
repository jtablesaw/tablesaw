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
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FxPie extends FxBuilder {

    public static PieChart chart(String title, Table table, String categoryColumnName, String numberColumnName) {

        StringColumn stringColumn = table.stringColumn(categoryColumnName);
        NumberColumn numberColumn = table.nCol(numberColumnName);

        return chart(title, stringColumn, numberColumn);
    }

    public static PieChart chart(String title, StringColumn stringColumn, NumberColumn numberColumn) {

        List<PieChart.Data> data = new ArrayList<>(stringColumn.size());

        for (int i = 0; i < stringColumn.size(); i++) {
            data.add(new PieChart.Data(stringColumn.getString(i), numberColumn.get(i)));
        }

        return createChart(title, data);
    }

    public static PieChart chart(String title, NumberColumn categoryColumn, NumberColumn numberColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            String name = Integer.toString(categoryColumn.roundInt(i));
            data.add(new PieChart.Data(name, numberColumn.get(i)));
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
