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

package tech.tablesaw.columns.strings.filters;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.StringColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.selection.Selection;

import javax.annotation.concurrent.Immutable;

import static tech.tablesaw.columns.strings.StringPredicates.*;

/**
 * A filtering that selects cells in which all text matches the given regex
 */
@Immutable
public class MatchesRegex extends ColumnFilter {

    private final String string;

    public MatchesRegex(StringColumnReference reference, String string) {
        super(reference);
        this.string = string;
    }

    @Override
    public Selection apply(Table relation) {
        return apply(relation.column(columnReference().getColumnName()));
    }

    @Override
    public Selection apply(Column column) {
        StringColumn textColumn = (StringColumn) column;
        return textColumn.eval(matchesRegex, string);
    }
}
