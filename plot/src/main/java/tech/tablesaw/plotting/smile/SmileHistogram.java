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

package tech.tablesaw.plotting.smile;

import smile.plot.PlotCanvas;
import tech.tablesaw.api.NumericColumn;

import javax.swing.*;

import static tech.tablesaw.plotting.smile.SmilePlotUtils.getjFrame;

import java.awt.*;

/**
 * Simple API for producing basic histogram plots directly from Tablesaw tables and columns
 */
public class SmileHistogram {


    public static PlotCanvas create(String plotTitle, String xTitle, String yTitle, NumericColumn column) {
        PlotCanvas canvas = smile.plot.Histogram.plot(column.asDoubleArray());
        canvas.setForeground(Color.DARK_GRAY);
        canvas.setTitle(plotTitle);
        canvas.setAxisLabel(0, xTitle);
        canvas.setAxisLabel(1, yTitle);
        return canvas;
    }

    public static PlotCanvas create(double[] x, String yTitle) {
        PlotCanvas canvas = smile.plot.Histogram.plot(x);
        return canvas;
    }

    public static void show(double[] x) {
        JFrame frame = getjFrame(600, 400);
        String yTitle = "proportion";
        PlotCanvas canvas = create(x, yTitle);
        canvas.setAxisLabel(1, yTitle);
        frame.add(canvas);
        frame.setVisible(true);
    }

    public static void show(String plotTitle, NumericColumn column) {
        JFrame frame = getjFrame(600, 400);
        PlotCanvas canvas = create(plotTitle, column.name(), "proportion", column);
        frame.add(canvas);
        frame.setVisible(true);
    }

    public static void show(NumericColumn column) {
        JFrame frame = getjFrame(600, 400);
        PlotCanvas canvas = create("", column.name(), "proportion", column);
        frame.add(canvas);
        frame.setVisible(true);
    }
}
