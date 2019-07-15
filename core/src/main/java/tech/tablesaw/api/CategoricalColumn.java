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

package tech.tablesaw.api;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import tech.tablesaw.columns.Column;

import java.util.Map;

/**
 * A column type that can be summarized, or serve as a grouping variable in cross tabs or other aggregation operations.
 * <p>
 * The column data is generally discrete, however NumberColumn implements CategoricalColumn so that it can be used to
 * summarize when it contains ints. If you use it to summarize over a large range of floating point numbers, you
 * will likely run out of memory.
 * <p>
 * Supporting subtypes include:
 * - StringColumn
 * - BooleanColumn
 * - DateColumn,
 * - etc
 * <p>
 * DateTimeColumn is not included. TimeColumn can be converted to ints without loss of data, so it does implement this
 * interface
 */
public interface CategoricalColumn<T> extends Column<T> {

    default Table countByCategory() {

        final Table t = new Table("Column: " + name());
        final CategoricalColumn<?> categories = (CategoricalColumn<?>) type().create("Category");
        final IntColumn counts = IntColumn.create("Count");

        final Object2IntMap<String> valueToCount = new Object2IntOpenHashMap<>();

        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                final String next = getString(i);
                if (valueToCount.containsKey(next)) {
                    valueToCount.put(next, valueToCount.getInt(next) + 1);
                } else {
                    valueToCount.put(next, 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : valueToCount.object2IntEntrySet()) {
            categories.appendCell(entry.getKey());
            counts.append(entry.getValue());
        }
        if (countMissing() > 0) {
            categories.appendMissing();
            counts.append(countMissing());
        }
        t.addColumns(categories);
        t.addColumns(counts);
        return t;
    }

}
