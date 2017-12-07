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

package tech.tablesaw.index;

import java.time.LocalDate;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.util.Selection;

/**
 * An index for four-byte integer and Date columns
 */
public class DateIndex {

    private final IntIndex index;

    public DateIndex(DateColumn column) {
        index = new IntIndex(column);
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given int
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(LocalDate value) {
        return index.get(PackedLocalDate.pack(value));
    }

    public Selection atLeast(LocalDate value) {
        return index.atLeast(PackedLocalDate.pack(value));
    }

    public Selection greaterThan(LocalDate value) {
        return index.greaterThan(PackedLocalDate.pack(value));
    }

    public Selection atMost(LocalDate value) {
        return index.atMost(PackedLocalDate.pack(value));
    }

    public Selection lessThan(LocalDate value) {
        return index.lessThan(PackedLocalDate.pack(value));
    }
}