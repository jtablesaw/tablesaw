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

package tech.tablesaw.api;

import tech.tablesaw.columns.Column;

/**
 * A column type that can be summarized, or serve as a grouping variable in cross tabs or other aggregation operations.
 *
 * The column data is discrete.
 *
 * Supporting subtypes include:
 * - CategoryColumn
 * - IntColumn
 * - BooleanColumn
 * - ShortColumn
 * - DateColumn
 *
 * Floating point types (float, double) and near-continuous time columns (Time and DateTime) are not included.
 */
public interface CategoricalColumn extends Column {

}
