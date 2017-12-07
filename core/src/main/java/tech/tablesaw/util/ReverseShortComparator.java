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

package tech.tablesaw.util;

import it.unimi.dsi.fastutil.shorts.ShortComparator;

import javax.annotation.concurrent.Immutable;

/**
 * A Comparator for int primitives for sorting in reverse order, using the given comparator
 */
@Immutable
public final class ReverseShortComparator {

    public static ShortComparator reverseShortComparator = new ShortComparator() {

        @Override
        public int compare(Short o2, Short o1) {
            return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
        }

        @Override
        public int compare(short o2, short o1) {
            return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
        }
    };

    public static ShortComparator instance() {
        return reverseShortComparator;
    }
}