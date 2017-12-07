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

import java.time.LocalDate;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class LocalDateBetweenInclusive extends ColumnFilter {
    private final LocalDate low;
    private final LocalDate high;

    public LocalDateBetweenInclusive(ColumnReference reference, LocalDate lowValue, LocalDate highValue) {
        super(reference);
        this.low = lowValue;
        this.high = highValue;
    }

    public Selection apply(Table relation) {
        DateColumn column = (DateColumn) relation.column(columnReference.getColumnName());
        Selection matches = column.isOnOrAfter(low);
        matches.and(column.isOnOrBefore(high));
        return matches;
    }
}
