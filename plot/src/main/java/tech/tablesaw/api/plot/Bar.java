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
import javafx.scene.chart.BarChart;
import tech.tablesaw.aggregate.NumericSummaryTable;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.plotting.fx.FxBar;
import tech.tablesaw.plotting.fx.FxPlot;

import javax.swing.*;

public class Bar extends FxPlot {

    private static final String WINDOW_TITLE = "Tablesaw";

    public static void show(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, ShortColumn categoryColumn, NumericColumn numericColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, IntColumn categoryColumn, NumericColumn numericColumn) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(String title, NumericSummaryTable table) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                if (table.column(0) instanceof CategoryColumn) {
                    initAndShowGUI(title, table.categoryColumn(0), table.nCol(1), 640, 480);
                }
                if (table.column(0) instanceof ShortColumn) {
                    initAndShowGUI(title, table.shortColumn(0), table.nCol(1), 640, 480);
                }
                if (table.column(0) instanceof IntColumn) {
                    initAndShowGUI(title, table.intColumn(0), table.nCol(1), 640, 480);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initAndShowGUI(String title,
                                       CategoryColumn categoryColumn,
                                       NumericColumn numericColumn,
                                       int width,
                                       int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        BarChart<String, Number> chart = FxBar.chart(title, categoryColumn, numericColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initAndShowGUI(String title,
                                       ShortColumn categoryColumn,
                                       NumericColumn numericColumn,
                                       int width,
                                       int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        BarChart<String, Number> chart = FxBar.chart(title, categoryColumn, numericColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initAndShowGUI(String title,
                                       IntColumn categoryColumn,
                                       NumericColumn numericColumn,
                                       int width,
                                       int height) throws Exception {

        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
        BarChart<String, Number> chart = FxBar.chart(title, categoryColumn, numericColumn);
        Platform.runLater(() -> initFX(fxPanel, chart));
    }

    private static void initFX(JFXPanel fxPanel, BarChart<String, Number> chart) {
        // This method is invoked on the JavaFX thread
        Scene scene = new Scene(chart, chart.getWidth(), chart.getHeight());
        fxPanel.setScene(scene);
    }
}
