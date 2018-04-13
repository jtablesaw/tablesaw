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

package tech.tablesaw.columns.datetimes.filters;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.TwoColumnFilter;
import tech.tablesaw.selection.Selection;

import static tech.tablesaw.columns.datetimes.DateTimePredicates.isNotEqualTo;

/**
 * A filtering that selects cells whose contents equal the given text ignoring case
 */
public class ColumnNotEqualTo extends TwoColumnFilter {

    public ColumnNotEqualTo(ColumnReference reference, Column otherColumn) {
        super(reference, otherColumn);
    }

    public ColumnNotEqualTo(DateTimeColumn columnToCompareAgainst) {
        super(columnToCompareAgainst);
    }

    public ColumnNotEqualTo(ColumnReference reference, ColumnReference columnToCompareAgainst) {
        super(reference, columnToCompareAgainst);
    }

    /**
     * Returns a selection formed by applying a predicate for case-neutral equality between
     * each value in the given column and the corresponding value of the column passed into the constructor.
     *
     * @param columnBeingFiltered The column whose results will be filtered by the operation
     */
    @Override
    public Selection apply(Column columnBeingFiltered) {
        DateTimeColumn dateColumn = (DateTimeColumn) columnBeingFiltered;
        return dateColumn.eval(isNotEqualTo, (DateTimeColumn) otherColumn());
    }
}