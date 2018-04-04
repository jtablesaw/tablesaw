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

package tech.tablesaw.filtering.composite;

import com.google.common.collect.Lists;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.selection.Selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A composite filtering that only returns {@code true} if all component filters return true
 */
public class AllOf implements Filter {

    private final List<Filter> filterList = new ArrayList<>();

    private AllOf(Collection<Filter> filters) {
        this.filterList.addAll(filters);
    }

    public static AllOf allOf(Filter... filters) {
        List<Filter> filterList = new ArrayList<>();
        Collections.addAll(filterList, filters);
        return new AllOf(filterList);
    }

    public static AllOf both(Filter filter1, Filter filter2) {
        List<Filter> filterList = Lists.newArrayList(filter1, filter2);
        return new AllOf(filterList);
    }

    public static AllOf allOf(Collection<Filter> filters) {
        return new AllOf(filters);
    }

    @Override
    public Selection apply(int size) {
        Selection selection = null;
        for (Filter filter : filterList) {
            if (selection == null) {
                selection = filter.apply(size);
            } else {
                selection.and(filter.apply(size));
            }
        }
        return selection;
    }
}
