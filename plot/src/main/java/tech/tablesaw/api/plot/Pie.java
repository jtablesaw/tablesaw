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

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotting.fx.FxPie;
import tech.tablesaw.plotting.fx.FxPlot;

import javax.swing.*;

@Deprecated
public class Pie extends FxPlot {

    private static final String WINDOW_TITLE = "Tablesaw";

    public static void show(String title, StringColumn stringColumn, NumberColumn numberColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, stringColumn, numberColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, NumberColumn categoryColumn, NumberColumn numberColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numberColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Display a pie chart with the given title, derived from the given table
     * @param title The main title for the plot
     * @param table Table must have its first column as the grouping column, and the second as the number column
     */
    public static void show(String title, Table table) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                if (table.column(0) instanceof StringColumn) {
                    initAndShowGUI(title, table.stringColumn(0), table.nCol(1), 640, 480);
                }
                if (table.column(0) instanceof DoubleColumn) {
                    initAndShowGUI(title, table.numberColumn(0), table.nCol(1), 640, 480);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initAndShowGUI(String title,
            StringColumn stringColumn,
            NumberColumn numberColumn,
            int width,
            int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        PieChart chart = FxPie.chart(title, stringColumn, numberColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initAndShowGUI(String title,
            NumberColumn categoryColumn,
            NumberColumn numberColumn,
            int width,
            int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        PieChart chart = FxPie.chart(title, categoryColumn, numberColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initFX(JFXPanel fxPanel, PieChart chart) {
        // This method is invoked on the JavaFX thread
        Scene scene = new Scene(chart, chart.getWidth(), chart.getHeight());
        fxPanel.setScene(scene);
    }
}
