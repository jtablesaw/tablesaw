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
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

/**
 * A boolean filtering, returns true if the filtering it wraps returns false, and vice-versa.
 */
@Immutable
public class IsFalse extends CompositeFilter {

    private final Filter filter;

    private IsFalse(Filter filter) {
        this.filter = filter;
    }

    public static IsFalse isFalse(Filter filter) {
        return new IsFalse(filter);
    }

    /**
     * Returns true if the element in the given row in my {@code column} is true
     */
    @Override
    public Selection apply(Table relation) {
        Selection selection = new BitmapBackedSelection();
        selection.addRange(0, relation.rowCount());
        selection.andNot(filter.apply(relation));
        return selection;
    }
}
