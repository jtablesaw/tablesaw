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

package tech.tablesaw.filtering.columnbased;

import com.google.common.base.Preconditions;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

public class ColumnLessThan extends ColumnFilter {

    private final ColumnReference otherColumn;

    public ColumnLessThan(ColumnReference a, ColumnReference b) {
        super(a);
        otherColumn = b;
    }

    private static Selection apply(IntColumn column1, IntColumn column2) {
        return column1.isLessThan(column2);
    }

    private static Selection apply(ShortColumn column1, ShortColumn column2) {
        return column1.isLessThan(column2);
    }

    private static Selection apply(LongColumn column1, LongColumn column2) {
        return column1.isLessThan(column2);
    }

    private static Selection apply(FloatColumn column1, FloatColumn column2) {
        return column1.isLessThan(column2);
    }

    private static Selection apply(DoubleColumn column1, DoubleColumn column2) {
        return column1.isLessThan(column2);
    }

    public Selection apply(Table relation) {

        Column column = relation.column(columnReference().getColumnName());
        Column other = relation.column(otherColumn.getColumnName());

        Preconditions.checkArgument(column.type() == other.type());

        if (column.type() == ColumnType.INTEGER)
            return apply((IntColumn) column, (IntColumn) other);

        if (column.type() == ColumnType.LONG_INT)
            return apply((LongColumn) column, (LongColumn) other);

        if (column.type() == ColumnType.SHORT_INT)
            return apply((ShortColumn) column, (ShortColumn) other);

        if (column.type() == ColumnType.FLOAT)
            return apply((FloatColumn) column, (FloatColumn) other);

        if (column.type() == ColumnType.DOUBLE)
            return apply((DoubleColumn) column, (DoubleColumn) other);

        throw new UnsupportedOperationException("Not supported (currently) for this column type");
    }
}
