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

package tech.tablesaw.columns;

import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.IsMissing;
import tech.tablesaw.filtering.IsNotMissing;

/**
 * A reference to a column that can be used in evaluating query predicates. It is a key part of having a fluent API
 * for querying tables.
 * <p>
 * Basically, it lets you write a query like this:
 * <p>
 * table.selectWhere(column("foo").isEqualTo("Bar"));
 * <p>
 * In that example, column() is a static method that returns a ColumnReference for a column named "foo".
 * The method isEqualTo(), is implemented on ColumnReference in a way that it can be applied to potentially, multiple
 * column types, although in this case, it only makes sense for CategoryColumns since the argument is a string.
 * <p>
 * When selectWhere() isExecuted, it supplies the table to the ColumnReference. The ColumnReference uses the table
 * and columnName to get access to the right column, and then fulfils its role by ensuring that the filtering
 * "isEqualTo("Bar") is applied to all the cells in the column.
 */
public class ColumnReference {

    private final String columnName;

    public ColumnReference(String column) {
        this.columnName = column;
    }

    public String getColumnName() {
        return columnName;
    }

    public Filter isNotMissing() {
        return new IsNotMissing(this);
    }

    public Filter isMissing() {
        return new IsMissing(this);
    }
}
