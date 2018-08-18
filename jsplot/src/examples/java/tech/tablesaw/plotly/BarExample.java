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

package tech.tablesaw.plotly;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.VerticalBarPlot;
import tech.tablesaw.plotly.components.Layout;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;

/**
 * Basic sample vertical bar chart
 */
public class BarExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        NumberColumn logNInjuries = table.numberColumn("injuries").add(1).logN();
        logNInjuries.setName("log injuries");
        table.addColumns(logNInjuries);
        NumberColumn scale = table.numberColumn("scale");
        scale.set(scale.isLessThan(0), DoubleColumnType.missingValueIndicator());

        Table s = table.summarize("fatalities", "log injuries", sum).by("Scale");
        System.out.println(s);

        Plot.show(HorizontalBarPlot.create("Tornado Impact", s, "scale", Layout.BarMode.STACK,"Sum [Fatalities]", "Sum [log injuries]"));
        Plot.show(VerticalBarPlot.create("Tornado Impact", s, "scale", Layout.BarMode.GROUP,"Sum [Fatalities]", "Sum [log injuries]"));
    }
}
