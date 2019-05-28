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

package tech.tablesaw.columns.temporal;

import java.time.temporal.Temporal;
import java.util.Iterator;
import java.util.function.Supplier;

import tech.tablesaw.columns.Column;

public interface TemporalFillers<T extends Temporal, C extends Column<T>> {

    C fillWith(Iterator<T> iterator);

    default C fillWith(final Iterable<T> iterable) {
        return fillWith(iterable.iterator());
    }

    C fillWith(Supplier<T> supplier);
}
