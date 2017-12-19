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

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotting.xchart.XchartLine;
import tech.tablesaw.util.BitmapBackedSelection;

/**
 * A control chart
 * Displays the data column in sequence, with upper and lower control lines set by default
 * at + or - 3 standard deviations from the mean
 */
public class Control {

    public static void show(String name, NumericColumn data) {
        show(name, data, 0, data.size());
    }

    public static void show(String name, NumericColumn sourceData, int controlRangeStart, int controlRangeEnd) {

        BitmapBackedSelection selection = new BitmapBackedSelection();
        selection.addRange(controlRangeStart, controlRangeEnd);
        NumericColumn data = (NumericColumn) sourceData.subset(selection);
        double avg = data.mean();
        double stdDev = data.standardDeviation();
        double controlLimit = 3 * stdDev;

        show(name, data, avg, controlLimit);
    }

    public static void show(String name, NumericColumn data, double avg, double controlLimit) {
        IntColumn index = new IntColumn("Observations");
        DoubleColumn mean = new DoubleColumn("Mean");
        DoubleColumn ucl = new DoubleColumn("UCL");
        DoubleColumn lcl = new DoubleColumn("LCL");

        for (int i = 0; i < data.size(); i++) {
            index.append(i + 1);
            mean.append(avg);
            ucl.append(avg + controlLimit);
            lcl.append(avg - controlLimit);
        }

        XchartLine.show(name, index, data, mean, ucl, lcl);
    }
}
