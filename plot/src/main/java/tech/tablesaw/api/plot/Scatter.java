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

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.plotting.xchart.XchartScatter;
import tech.tablesaw.table.ViewGroup;

/**
 *
 */
public class Scatter {

    public static void show(NumberColumn x, NumberColumn y) {

        XchartScatter.show("Scatterplot", x, y);
    }

    public static void show(double[] x, double[] y) {

        XchartScatter.show("", x, "", y, "", 640, 480);
    }

    public static void show(double[] x, String xLabel, double[] y, String yLabel) {

        XchartScatter.show("", x, xLabel, y, yLabel, 640, 480);
    }

    public static void show(String title, NumberColumn x, NumberColumn y, ViewGroup groups) {
        XchartScatter.show(title, x, y, groups);
    }

    public static void show(String title, NumberColumn x, NumberColumn y) {
        XchartScatter.show(title, x, y);
    }
}
