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

package tech.tablesaw.sorting;

import com.google.common.base.MoreObjects;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides fine-grained control over sorting.
 * <p>
 * Use:
 * <p>
 * table.sortOn(first("Year", DESCEND).next("State", ASCEND));
 * <p>
 * This sorts table on the column named year in descending order, such that the most recent years
 * appear first, then on State, in ascending order so "AL" will appear before "CA". You can add
 * additional instructions for multi-column sorts by chaining additional calls to next() with the
 * appropriate column names and Order.
 */
public class Sort implements Iterable<Map.Entry<String, Sort.Order>> {

    private final LinkedHashMap<String, Order> sortOrder = new LinkedHashMap<>();

    public Sort(String columnName, Order order) {
        next(columnName, order);
    }

    public static Sort on(String columnName, Order order) {
        return new Sort(columnName, order);
    }

    public Sort next(String columnName, Order order) {
        sortOrder.put(columnName, order);
        return this;
    }

    public boolean isEmpty() {
        return sortOrder.isEmpty();
    }

    public int size() {
        return sortOrder.size();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Map.Entry<String, Order>> iterator() {
        return sortOrder.entrySet().iterator();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("order", sortOrder)
                .toString();
    }

    public enum Order {ASCEND, DESCEND}
}
