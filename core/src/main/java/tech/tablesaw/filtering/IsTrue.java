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

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.Table;
import tech.tablesaw.util.Selection;

/**
 * A boolean filtering. For symmetry with IsFalse
 */
@Immutable
public class IsTrue extends CompositeFilter {

    private final Filter filter;

    private IsTrue(Filter filter) {
        this.filter = filter;
    }

    public static IsTrue isTrue(Filter filter) {
        return new IsTrue(filter);
    }

    /**
     * Returns true if the element in the given row in my {@code column} is true
     *
     * @param relation
     */
    @Override
    public Selection apply(Table relation) {
        return filter.apply(relation);
    }
}
