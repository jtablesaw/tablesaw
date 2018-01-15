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

package tech.tablesaw.plotting.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 *
 */
public class FxTable extends TableView<Integer> {

    private Table tableData;

    /**
     * Private constructor: use static build methods
     */
    private FxTable() {
    }

    /**
     * Return an empty TableSawFxViewer
     * @return an empty TableSawFxViewer
     */
    static public FxTable build() {
        FxTable tableSawFxViewer = new FxTable();

        return tableSawFxViewer;
    }

    /**
     * Return a TableSawFxViewer initialized with a Table
     * @param table the {@link Table} containing the data to insert in the {@link TableView}
     * @return a TableSawFxViewer initialized with a Table
     */
    static public FxTable build(Table table) {

        FxTable tableSawFxViewer = build();
        tableSawFxViewer.setData(table);

        return tableSawFxViewer;
    }

    /**
     * Assign new data to the {@link TableView}
     * @param table the {@link Table} containing the data to insert in the {@link TableView}
     */
    public void setData(Table table) {
        this.tableData = table;
        refreshTableView();
    }

    /**
     * Rebuild the {@link TableView}.
     * To be called every time the underlying {@link TableView} changed.
     */
    public void refreshTableView() {

        if (tableData == null) {
            return;
        }

        // the underlying data is just a list of integers
        ObservableList<Integer> data = FXCollections.observableArrayList();
        for (int i = 0; i < tableData.rowCount(); i++) {
            data.add(i);
        }

        // Loop over the table columns, create a TableColumn and append to the list.
        List<TableColumn<Integer, ?>> columnsList = new ArrayList<>();
        for (int iCol = 0; iCol < tableData.columnCount(); iCol++) {

            TableColumn newColumn = createColumn(tableData.column(iCol));
            columnsList.add(newColumn);

        }

        // set the data and the columns
        this.setItems(data);
        this.getColumns().setAll(columnsList);
    }

    /**
     * Build a TableView {@link TableColumn} from a TableSaw {@Column}
     * @param col a TableSaw {@Column}
     * @return a TableView {@link TableColumn}
     */
    private TableColumn createColumn(Column col) {

        // the name is just the column name
        TableColumn<Integer, String> stringColumn = new TableColumn(col.name());
        // Setup the cell value factory: return the string representation of the value at the given index
        stringColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Integer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Integer, String> param) {
                int rowIndex = param.getValue();
                String name = col.getString(rowIndex);

                return new SimpleStringProperty(name);
            }
        });

        return stringColumn;
    }

}
