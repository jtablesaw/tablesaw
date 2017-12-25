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

import java.util.List;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;

/**
 * A specialization of the standard Relation used for tables formed by grouping operations on a Relation
 */
public class SubTable extends Table {

    /**
     * The values that will be summarized on
     */
    private List<String> values;

    /**
     * Returns a new SubTable from the given table that will include summaries for the given values
     *
     * @param original The table from which this one was derived
     */
    SubTable(Table original) {
        super(original.name(),
                original.emptyCopy().columns().toArray(new Column[original.columnCount()]));
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Adds a single row to this table from sourceTable, copying every column in sourceTable
     */
    void addRow(int rowIndex, Table sourceTable) {
        for (int i = 0; i < columnCount(); i++) {
            Column column = column(i);
            ColumnType type = column.type();
            switch (type) {
                case FLOAT:
                    FloatColumn floatColumn = (FloatColumn) column;
                    floatColumn.append(sourceTable.floatColumn(i).get(rowIndex));
                    break;
                case DOUBLE:
                    DoubleColumn doubleColumn = (DoubleColumn) column;
                    doubleColumn.append(sourceTable.doubleColumn(i).get(rowIndex));
                    break;
                case INTEGER:
                    IntColumn intColumn = (IntColumn) column;
                    intColumn.append(sourceTable.intColumn(i).get(rowIndex));
                    break;
                case SHORT_INT:
                    ShortColumn shortColumn = (ShortColumn) column;
                    shortColumn.append(sourceTable.shortColumn(i).get(rowIndex));
                    break;
                case LONG_INT:
                    LongColumn longColumn = (LongColumn) column;
                    longColumn.append(sourceTable.longColumn(i).get(rowIndex));
                    break;
                case BOOLEAN:
                    BooleanColumn booleanColumn = (BooleanColumn) column;
                    booleanColumn.append(sourceTable.booleanColumn(i).get(rowIndex));
                    break;
                case LOCAL_DATE:
                    DateColumn localDateColumn = (DateColumn) column;
                    localDateColumn.appendInternal(sourceTable.dateColumn(i).getIntInternal(rowIndex));
                    break;
                case LOCAL_TIME:
                    TimeColumn timeColumn = (TimeColumn) column;
                    timeColumn.appendInternal(sourceTable.timeColumn(i).getIntInternal(rowIndex));
                    break;
                case LOCAL_DATE_TIME:
                    DateTimeColumn localDateTimeColumn = (DateTimeColumn) column;
                    localDateTimeColumn.appendInternal(sourceTable.dateTimeColumn(i).getLongInternal(rowIndex));
                    break;
                case CATEGORY:
                    CategoryColumn categoryColumn = (CategoryColumn) column;
                    categoryColumn.append(sourceTable.categoryColumn(i).get(rowIndex));
                    break;
                default:
                    throw new IllegalStateException("Unhandled column type updating columns");
            }
        }
    }
}
