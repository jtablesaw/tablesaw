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

import javax.swing.SwingUtilities;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotting.fx.FxPlot;
import tech.tablesaw.plotting.fx.FxTable;

/**
 *
 */
public class TableView extends FxPlot {

    private static final String WINDOW_TITLE = "Tablesaw";

    public static void show(String title, Table data) throws Exception {

        SwingUtilities.invokeLater(() -> {
            try {
                initAndShowGUI(title, data, 640, 480);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initAndShowGUI(String title, Table data, int width, int height) {
        final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE + " - " + title, width, height);
        FxTable tableView = FxTable.build(data);
        Platform.runLater(() -> initFX(fxPanel, tableView));
    }

    private static void initFX(JFXPanel fxPanel, FxTable tableView) {
        // This method is invoked on the JavaFX thread
        Scene scene = new Scene(tableView, tableView.getWidth(), tableView.getHeight());
        fxPanel.setScene(scene);
    }

}
