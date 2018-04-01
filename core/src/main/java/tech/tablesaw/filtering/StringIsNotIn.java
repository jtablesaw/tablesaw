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

import com.google.common.collect.Lists;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.selection.Selection;

import java.util.Collection;
import java.util.Set;

/**
 * Implements NotEqualTo testing for Category and Text Columns
 */
public class StringIsNotIn extends ColumnFilter {

    private final StringColumn filterColumn;

    public StringIsNotIn(ColumnReference reference, StringColumn filterColumn) {
        super(reference);
        this.filterColumn = filterColumn;
    }

    public StringIsNotIn(ColumnReference reference, Collection<String> strings) {
        super(reference);
        this.filterColumn = new StringColumn("temp", Lists.newArrayList(strings));
    }

    public StringIsNotIn(ColumnReference reference, String... strings) {
        super(reference);
        this.filterColumn = new StringColumn("temp", Lists.newArrayList(strings));
    }

    public Selection apply(Table relation) {
        StringColumn stringColumn = (StringColumn) relation.column(columnReference.getColumnName());
        Set<String> firstSet = stringColumn.asSet();
        firstSet.removeAll(filterColumn.data());
        return stringColumn.select(firstSet::contains);
    }
}
