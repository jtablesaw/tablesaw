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

    BOOLEAN(Byte.MIN_VALUE, 1, "Boolean"),
    STRING("", 4, "String"),
    NUMBER(Double.NaN, 8, "Number"),
    LOCAL_DATE(Integer.MIN_VALUE, 4, "Date"),
    LOCAL_DATE_TIME(Long.MIN_VALUE, 8, "DateTime"),
    LOCAL_TIME(Integer.MIN_VALUE, 4, "Time"),
    SKIP(null, 0, "Skipped");

    private final Comparable<?> missingValue;

    private final int byteSize;

    private final String printerFriendlyName;

    ColumnType(Comparable<?> missingValue, int byteSize, String name) {
        this.missingValue = missingValue;
        this.byteSize = byteSize;
        this.printerFriendlyName = name;
    }

    public Column create(String name) {
        switch (this) {
            case BOOLEAN: return BooleanColumn.create(name);
            case STRING: return StringColumn.create(name);
            case NUMBER: return DoubleColumn.create(name);
            case LOCAL_DATE: return DateColumn.create(name);
            case LOCAL_DATE_TIME: return DateTimeColumn.create(name);
            case LOCAL_TIME: return TimeColumn.create(name);
            case SKIP: throw new IllegalArgumentException("Cannot create column of type SKIP");
        }
        throw new UnsupportedOperationException("Column type " + this.name() + " doesn't support column creation");
    }

    public Comparable<?> getMissingValue() {
        return missingValue;
    }

    public int byteSize() {
        return byteSize;
    }

    public String getPrinterFriendlyName() {
        return printerFriendlyName;
    }
}
