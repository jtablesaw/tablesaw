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

package tech.tablesaw.api.plotjs;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class Pareto extends JsPlot {

    /**
     * Display a pareto plot with the given title, derived from the given table
     * @param title The main title for the plot
     * @param table Table must have its first column as the grouping column, and the second as the number column
     */
    public static void show(String title, Table table) {

/*
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
                throw new RuntimeException(e);
            }
        });
*/
    }

    public static void show(String title, StringColumn stringColumn, NumberColumn numberColumn) {

    }

    public static void show(String title, NumberColumn categoryColumn, NumberColumn numberColumn) {

    }

}
