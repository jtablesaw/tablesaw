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

package tech.tablesaw.filtering;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class IntEqualTo extends ColumnFilter {

    private final int value;

    public IntEqualTo(ColumnReference reference, int value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table table) {
        Column column = table.column(columnReference.getColumnName());
        ColumnType type = column.type();
        switch (type) {
            case INTEGER:
                IntColumn intColumn = (IntColumn) column;
                return intColumn.isEqualTo(value);
            case SHORT_INT:
                ShortColumn shorts = (ShortColumn) column;
                return shorts.isEqualTo((short) value);
            case LONG_INT:
                LongColumn longs = (LongColumn) column;
                return longs.isEqualTo(value);
            case FLOAT:
                FloatColumn floats = (FloatColumn) column;
                return floats.isEqualTo((float) value);
            default:
                throw new UnsupportedOperationException("IsEqualTo(anInt) is not supported for column type " + type);
        }
    }
}
