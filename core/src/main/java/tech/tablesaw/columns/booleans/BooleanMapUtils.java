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

package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

/**
 * An interface for mapping operations unique to Boolean columns
 */
public interface BooleanMapUtils extends Column<Boolean> {

    /*
     * Returns a Boolean column made by and-ing this column with the arguments
     */
    default BooleanColumn and(BooleanColumn... columns) {
        String name = name() + " and: ";
        Selection selection = asSelection();
        for (BooleanColumn column : columns) {
            if (! column.name().equals(columns[0].name())) {
                name += ", ";
            }
            name += column.name();
            selection.and(column.asSelection());
        }
        return BooleanColumn.create(name, selection, size());
    }

    default BooleanColumn or(BooleanColumn... columns) {
        String name = name() + " or: ";
        Selection selection = asSelection();
        for (BooleanColumn column : columns) {
            if (! column.name().equals(columns[0].name())) {
                name += ", ";
            }
            name += column.name();
            selection.or(column.asSelection());
        }
        return BooleanColumn.create(name, selection, size());
    }

    /**
     * Returns a column made by combining the receiver and each of the arguments using the operation: A andNot V.
     * For example, the value of a cell in the result column would be true if the corresponding value in A is true and
     * the corresponding value in B is false
     */
    default BooleanColumn andNot(BooleanColumn... columns) {
        String name = name() + " and not: ";
        Selection selection = asSelection();
        for (BooleanColumn column : columns) {
            if (! column.name().equals(columns[0].name())) {
                name += ", ";
            }
            name += column.name();
            selection.andNot(column.asSelection());
        }
        return BooleanColumn.create(name, selection, size());
    }

    Selection asSelection();
}
