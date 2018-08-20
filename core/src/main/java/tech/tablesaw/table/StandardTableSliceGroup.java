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

package tech.tablesaw.table;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class StandardTableSliceGroup extends TableSliceGroup {

    private StandardTableSliceGroup(Table original, CategoricalColumn<?>... columns) {
        super(original, splitColumnNames(columns));
        setSourceTable(getSourceTable().sortOn(getSplitColumnNames()));
        splitOn(getSplitColumnNames());
    }

    private static String[] splitColumnNames(CategoricalColumn<?>... columns) {
        String[] splitColumnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            splitColumnNames[i] = columns[i].name();
        }
        return splitColumnNames;
    }

    /**
     * Returns a viewGroup splitting the original table on the given columns.
     * The named columns must be CategoricalColumns
     */
    public static StandardTableSliceGroup create(Table original, String... columnsNames) {
        List<CategoricalColumn<?>> columns = original.categoricalColumns(columnsNames);
        return new StandardTableSliceGroup(original, columns.toArray(new CategoricalColumn<?>[0]));
    }

    /**
     * Returns a viewGroup splitting the original table on the given columns.
     * The named columns must be CategoricalColumns
     */
    public static StandardTableSliceGroup create(Table original, CategoricalColumn<?>... columns) {
        return new StandardTableSliceGroup(original, columns);
    }

    /**
     * Splits the sourceTable table into sub-tables, grouping on the columns whose names are given in
     * splitColumnNames
     */
    private void splitOn(String... columnNames) {

        List<Column<?>> columns = getSourceTable().columns(columnNames);
        int byteSize = getByteSize(columns);

        byte[] currentKey = null;
        String currentStringKey = null;
        TableSlice view;

        Selection selection = new BitmapBackedSelection();

        for (int row = 0; row < getSourceTable().rowCount(); row++) {

            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            String newStringKey = "";

            for (int col = 0; col < columnNames.length; col++) {
                if (col > 0) {
                    newStringKey = newStringKey + SPLIT_STRING;
                }

                Column<?> c = getSourceTable().column(columnNames[col]);
                String groupKey = getSourceTable().getUnformatted(row, getSourceTable().columnIndex(c));
                newStringKey = newStringKey + groupKey;
                byteBuffer.put(c.asBytes(row));
            }
            byte[] newKey = byteBuffer.array();
            if (row == 0) {
                currentKey = newKey;
                currentStringKey = newStringKey;
            }
            if (!Arrays.equals(newKey, currentKey)) {
                currentKey = newKey;
                view = new TableSlice(getSourceTable(), selection);
                view.setName(currentStringKey);
                currentStringKey = newStringKey;
                addSlice(view);
                selection = new BitmapBackedSelection();
                selection.add(row);
            } else {
                selection.add(row);
            }
        }
        if (!selection.isEmpty()) {
            view = new TableSlice(getSourceTable(), selection);
            view.setName(currentStringKey);
            addSlice(view);
        }
    }
}
