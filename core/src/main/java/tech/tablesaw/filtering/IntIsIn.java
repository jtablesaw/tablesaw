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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class IntIsIn extends ColumnFilter {

    private final IntColumn filterColumn;

    public IntIsIn(ColumnReference reference, IntColumn filterColumn) {
        super(reference);
        this.filterColumn = filterColumn;
    }

    public IntIsIn(ColumnReference reference, int... ints) {
        super(reference);
        this.filterColumn = new IntColumn("temp", new IntArrayList(ints));
    }

    public Selection apply(Table relation) {
        IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
        IntSet firstSet = intColumn.asSet();
        firstSet.retainAll(filterColumn.data());
        return intColumn.select(firstSet::contains);
    }
}
