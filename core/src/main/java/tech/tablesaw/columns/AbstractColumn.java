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

import tech.tablesaw.api.ColumnType;

/**
 * Partial implementation of the {@link Column} interface
 */
public abstract class AbstractColumn<T> implements Column<T> {

    public static final int DEFAULT_ARRAY_SIZE = 128;

    private String name;

    private final ColumnType type;

    public AbstractColumn(ColumnType type, final String name) {
        this.type = type;
        setName(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Column<T> setName(final String name) {
        this.name = name.trim();
        return this;
    }

    @Override
    public ColumnType type() {
        return type;
    }

    @Override
    public abstract Column<T> emptyCopy();

    @Override
    public String toString() {
        return type().getPrinterFriendlyName() + " column: " + name();
    }
}