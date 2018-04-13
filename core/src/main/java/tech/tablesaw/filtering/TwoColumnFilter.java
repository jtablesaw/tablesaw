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

import com.google.common.base.Preconditions;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.selection.Selection;

/**
 * A two-column filter is based on the relationship between two columns of the same type
 */
public abstract class TwoColumnFilter implements Filter {

    private final ColumnReference columnReference;

    // One of the following is used. OtherColumn is used when we have a column to filter against.
    private Column otherColumn;

    // otherColumnReference is used when the actual column is not available until application time
    private ColumnReference otherColumnReference;

    public TwoColumnFilter(ColumnReference reference, Column otherColumn) {
        columnReference = reference;
        this.otherColumn = otherColumn;
    }

    public TwoColumnFilter(Column columnToCompareAgainst) {
        columnReference = null;
        Preconditions.checkNotNull(columnToCompareAgainst);
        this.otherColumn = columnToCompareAgainst;
    }

    /**
     * Returns a Filter that compares the two columns
     *
     * @param reference              A reference to the column to be filtered on.
     *                               Must be from same table as {@code columnToCompareAgainst}
     * @param columnToCompareAgainst A reference to the column to be filtered against
     *                               Must be from same table as {@code reference}
     */
    public TwoColumnFilter(ColumnReference reference, ColumnReference columnToCompareAgainst) {
        columnReference = reference;
        Preconditions.checkNotNull(columnToCompareAgainst);
        otherColumnReference = columnToCompareAgainst;
    }

    @Override
    public Selection apply(Table relation) {
        if (otherColumn == null) {
            otherColumn = relation.column(otherColumnReference.getColumnName());
        }
        return apply(relation.column(columnReference().getColumnName()));
    }

    public ColumnReference columnReference() {
        return columnReference;
    }

    public ColumnReference otherColumnReference() {
        return otherColumnReference;
    }

    public Column otherColumn() {
        Preconditions.checkNotNull(otherColumn, "Column references can only be used in multi-column filters " +
                "when both columns are in the same table, and the apply(table) method is used.");

        return otherColumn;
    }
}
