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

package tech.tablesaw.aggregate;

import tech.tablesaw.api.Table;

/**
 * NumericSummaryTable is a standard table, but one with a specific format:
 * It has two columns, the first a category column and the second a numeric column,
 * so that it is appropriate for managing data that summarizes numeric variables by groups
 */
public class NumericSummaryTable extends Table {

    /**
     * Returns a new Table initialized with the given names and columns
     *
     * @param name The name of the table
     */
    private NumericSummaryTable(String name) {
        super(name);
    }

    /**
     * Returns a new, empty table (without rows or columns) with the given name
     */
    public static NumericSummaryTable create(String tableName) {
        return new NumericSummaryTable(tableName);
    }
}
