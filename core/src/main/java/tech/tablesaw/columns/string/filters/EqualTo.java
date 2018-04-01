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

package tech.tablesaw.columns.string.filters;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.string.StringColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.selection.Selection;

/**
 * Implements EqualTo testing for Category and Text Columns
 */
public class EqualTo extends ColumnFilter {

    private final String value;

    public EqualTo(StringColumnReference reference, String value) {
        super(reference);
        this.value = value;
    }

    @Override
    public Selection apply(Table relation) {
        return apply(relation.column(columnReference().getColumnName()));
    }

    @Override
    public Selection apply(Column column) {
        ColumnType type = column.type();
        switch (type) {
            case STRING: {
                StringColumn stringColumn = (StringColumn) column;
                return stringColumn.isEqualTo(value);
            }
            default:
                throw new UnsupportedOperationException(
                        String.format("ColumnType %s does not support equalTo on a String value", type));
        }
    }
}
