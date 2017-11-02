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

package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotting.xchart.XchartQuantile;

/**
 *
 */
public class Quantile {

    public static void show(String chartTitle, NumericColumn yColumn) {
        double[] x = new double[yColumn.size()];

        for (int i = 0; i < x.length; i++) {
            x[i] = i / (float) x.length;
        }

        NumericColumn copy = (NumericColumn) yColumn.copy();
        copy.sortAscending();
        show(chartTitle, x, copy);
    }

    public static void show(String chartTitle, double[] xData, NumericColumn yColumn) {

        // Create Chart
        XchartQuantile.show(chartTitle, xData, yColumn, 600, 400);
/*
    THIS CODE IS FOR A GLIMPSE-BACKED CHART
    double[] yData = yColumn.toDoubleArray();
    XchartScatter.SimpleScatter scatter = new XchartScatter.SimpleScatter(chartTitle, xData, yData);
    try {
      Display.showWithSwing(scatter);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
*/
    }
}
