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

package tech.tablesaw.columns;


import com.google.common.base.Objects;
import tech.tablesaw.api.ColumnType;

/**
 * Defines the type of data held by a {@link Column}
 */
public abstract class AbstractColumnType<T> implements ColumnType<T> {

    private final Comparable<?> missingValue;

    private final int byteSize;

    private final String name;

    private final String printerFriendlyName;

    protected AbstractColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        this.missingValue = missingValue;
        this.byteSize = byteSize;
        this.name = name;
        this.printerFriendlyName = printerFriendlyName;
        ColumnType.register(this);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String name() {
        return name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractColumnType that = (AbstractColumnType) o;
        return byteSize == that.byteSize &&
                Objects.equal(missingValue, that.missingValue) &&
                Objects.equal(name, that.name) &&
                Objects.equal(printerFriendlyName, that.printerFriendlyName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public void appendColumns(Column<T> column, Column<T> columnToAppend) {
        column.append(columnToAppend);
    }
}
