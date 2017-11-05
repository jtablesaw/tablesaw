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

import java.time.LocalTime;

import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.packeddata.PackedLocalTime;
import tech.tablesaw.util.Selection;

/**
 * An index for four-byte integer and Date columns
 */
public class TimeIndex {

    private final IntIndex index;

    public TimeIndex(TimeColumn column) {
        index = new IntIndex(column);
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given int
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(LocalTime value) {
        return index.get(PackedLocalTime.pack(value));
    }

    public Selection atLeast(LocalTime value) {
        return index.atLeast(PackedLocalTime.pack(value));
    }

    public Selection greaterThan(LocalTime value) {
        return index.greaterThan(PackedLocalTime.pack(value));
    }

    public Selection atMost(LocalTime value) {
        return index.atMost(PackedLocalTime.pack(value));
    }

    public Selection lessThan(LocalTime value) {
        return index.lessThan(PackedLocalTime.pack(value));
    }
}