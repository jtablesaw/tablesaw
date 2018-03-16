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
 * Defines the type of data held by a {@link Column}
 */
public enum ColumnType {

    BOOLEAN(Byte.MIN_VALUE, true),
    CATEGORY("", true),
    FLOAT(Float.NaN, false),
    DOUBLE(Double.NaN, false),
    SHORT_INT(Short.MIN_VALUE, true),
    INTEGER(Integer.MIN_VALUE, true),
    LONG_INT(Long.MIN_VALUE, true),
    LOCAL_DATE(Integer.MIN_VALUE, true),
    LOCAL_DATE_TIME(Long.MIN_VALUE, false),
    LOCAL_TIME(-1, false),
    SKIP(null, false);

    private final Comparable<?> missingValue;

    // does this column type handle data that is suitable for summarization (aggregation) operations?
    private final boolean isCategorical;

    ColumnType(Comparable<?> missingValue, boolean isCategorical) {
        this.isCategorical = isCategorical;
        this.missingValue = missingValue;
    }

    public Comparable<?> getMissingValue() {
        return missingValue;
    }

    public boolean isCategorical() {
        return isCategorical;
    }
}
