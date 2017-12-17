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

    BOOLEAN(Byte.MIN_VALUE),
    CATEGORY(""),
    FLOAT(Float.NaN),
    DOUBLE(Double.NaN),
    SHORT_INT(Short.MIN_VALUE),
    INTEGER(Integer.MIN_VALUE),
    LONG_INT(Long.MIN_VALUE),
    LOCAL_DATE(Integer.MIN_VALUE),
    LOCAL_DATE_TIME(Long.MIN_VALUE),
    LOCAL_TIME(-1),
    SKIP(null);

    private final Comparable<?> missingValue;

    ColumnType(Comparable<?> missingValue) {
        this.missingValue = missingValue;
    }

    public Comparable<?> getMissingValue() {
        return missingValue;
    }
}
