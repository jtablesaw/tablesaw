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

package tech.tablesaw.columns.packeddata;

import com.google.common.annotations.Beta;

import tech.tablesaw.columns.DateIntervalColumn;

/**
 * EXPERIMENTAL
 */
@Beta
public abstract class DateInterval {

    // boolean operations
    abstract boolean equals(DateIntervalColumn interval);

    abstract boolean before(DateIntervalColumn interval);

    abstract boolean after(DateIntervalColumn interval);

    abstract boolean contains(DateIntervalColumn interval);

    abstract boolean containedIn(DateIntervalColumn interval);

    abstract boolean meets(DateIntervalColumn interval);

    // combination operations
    abstract DateInterval union(DateInterval interval);      // or

    abstract DateInterval intersect(DateInterval interval);  // and

    abstract DateInterval minus(DateInterval interval);      // and not

    abstract DateInterval gap(DateInterval interval);        // the difference between two intervals

}
