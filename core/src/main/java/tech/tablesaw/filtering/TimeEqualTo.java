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

import java.time.LocalTime;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class TimeEqualTo extends ColumnFilter {

    final LocalTime value;

    public TimeEqualTo(ColumnReference reference, LocalTime value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        TimeColumn dateColumn = (TimeColumn) relation.column(columnReference.getColumnName());
        return dateColumn.isEqualTo(value);
    }
}
