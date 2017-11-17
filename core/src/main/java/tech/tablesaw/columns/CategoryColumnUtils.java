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

package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.aggregate.CategoryReduceUtils;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.filtering.StringPredicate;
import tech.tablesaw.mapping.StringMapUtils;
import tech.tablesaw.util.DictionaryMap;

public interface CategoryColumnUtils extends Column, StringMapUtils, CategoryReduceUtils, Iterable<String> {

    StringPredicate isMissing = i -> i.equals(CategoryColumn.MISSING_VALUE);
    StringPredicate isNotMissing = i -> !i.equals(CategoryColumn.MISSING_VALUE);

    DictionaryMap dictionaryMap();

    IntArrayList values();
}
