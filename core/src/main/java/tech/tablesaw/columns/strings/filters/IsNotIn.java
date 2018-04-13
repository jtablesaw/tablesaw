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

package tech.tablesaw.columns.strings.filters;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.StringColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.selection.Selection;

import java.util.Collection;

/**
 * Implements NotEqualTo testing for Category and Text Columns
 */
public class IsNotIn extends ColumnFilter {

    private final String[] filter;

    public IsNotIn(StringColumnReference reference, Collection<String> strings) {
        super(reference);
        this.filter = strings.toArray(new String[strings.size()]);
    }

    public IsNotIn(StringColumnReference reference, String... strings) {
        super(reference);
        this.filter = strings;
    }

    public Selection apply(Table relation) {
        return apply(relation.column(columnReference().getColumnName()));
    }

    @Override
    public Selection apply(Column columnBeingFiltered) {
        StringColumn stringColumn = (StringColumn) columnBeingFiltered;
        return stringColumn.isNotIn(filter);
    }
}
