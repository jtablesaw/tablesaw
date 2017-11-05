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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.util.Selection;

/**
 * A composite filtering that only returns {@code true} if all component filters return true
 */
public class AllOf extends CompositeFilter {

    private final List<Filter> filterList = new ArrayList<>();

    private AllOf(Collection<Filter> filters) {
        this.filterList.addAll(filters);
    }

    public static AllOf allOf(Filter... filters) {
        List<Filter> filterList = new ArrayList<>();
        Collections.addAll(filterList, filters);
        return new AllOf(filterList);
    }

    public static AllOf allOf(Collection<Filter> filters) {
        return new AllOf(filters);
    }

    public Selection apply(Table relation) {
        Selection selection = null;
        for (Filter filter : filterList) {
            if (selection == null) {
                selection = filter.apply(relation);
            } else {
                selection.and(filter.apply(relation));
            }
        }
        return selection;
    }
}
