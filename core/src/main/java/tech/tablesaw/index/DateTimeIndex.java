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

import java.time.LocalDateTime;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;
import tech.tablesaw.util.Selection;

/**
 * An index for four-byte integer and Date columns
 */
public class DateTimeIndex {

    private final LongIndex index;

    public DateTimeIndex(DateTimeColumn column) {
        index = new LongIndex(column);
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given int
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(LocalDateTime value) {
        return index.get(PackedLocalDateTime.pack(value));
    }

    public Selection atLeast(LocalDateTime value) {
        return index.atLeast(PackedLocalDateTime.pack(value));
    }

    public Selection greaterThan(LocalDateTime value) {
        return index.greaterThan(PackedLocalDateTime.pack(value));
    }

    public Selection atMost(LocalDateTime value) {
        return index.atMost(PackedLocalDateTime.pack(value));
    }

    public Selection lessThan(LocalDateTime value) {
        return index.lessThan(PackedLocalDateTime.pack(value));
    }
}