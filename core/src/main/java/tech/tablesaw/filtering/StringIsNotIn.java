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

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Lists;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 * Implements NotEqualTo testing for Category and Text Columns
 */
public class StringIsNotIn extends ColumnFilter {

    private final CategoryColumn filterColumn;

    public StringIsNotIn(ColumnReference reference, CategoryColumn filterColumn) {
        super(reference);
        this.filterColumn = filterColumn;
    }

    public StringIsNotIn(ColumnReference reference, Collection<String> strings) {
      super(reference);
      this.filterColumn = new CategoryColumn("temp", Lists.newArrayList(strings));
    }
 
    public StringIsNotIn(ColumnReference reference, String... strings) {
        super(reference);
        this.filterColumn = new CategoryColumn("temp", Lists.newArrayList(strings));
    }

    public Selection apply(Table relation) {
        CategoryColumn categoryColumn = (CategoryColumn) relation.column(columnReference.getColumnName());
        Set<String> firstSet = categoryColumn.asSet();
        firstSet.removeAll(filterColumn.data());
        return categoryColumn.select(firstSet::contains);
    }
}
