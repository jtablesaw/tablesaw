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
import tech.tablesaw.plotting.xchart.XchartLine;

/**
 * Displays a line chart
 */
@Deprecated
public class Line {

    public static void show(String chartTitle, NumberColumn x, NumberColumn y) {
        XchartLine.show(chartTitle, x, y);
    }

    /**
     * Displays a line chart with multiple series
     *
     * @param chartTitle    The main title
     * @param x             The column supplying the x values
     * @param y             The column supplying the y values
     */
    public static void show(String chartTitle, NumberColumn x, NumberColumn... y) {
        XchartLine.show(chartTitle, x, y);
    }
}
