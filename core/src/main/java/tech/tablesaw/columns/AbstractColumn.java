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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;

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

    @Override
    public StringColumn asStringColumn() {
        StringColumn sc = StringColumn.create(name() + " strings");
        for (T value: this) {
            sc.append(String.valueOf(value));
        }
        return sc;
    }

    /**
     * Maps the function across all rows, appending the results to a new BooleanColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into BooleanColumn.
     * @return BooleanColumn with the results appended.
     */
    public BooleanColumn mapToBooleanColumn(String name, Function<? super T, ? extends Boolean> function) {
        BooleanColumn newColumn = BooleanColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new DateColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into DateColumn.
     * @return DateColumn with the results appended.
     */
    public DateColumn mapToDateColumn(String name, Function<? super T, ? extends LocalDate> function) {
        DateColumn newColumn = DateColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new DateTimeColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into DateTimeColumn.
     * @return DateTimeColumn with the results appended.
     */
    public DateTimeColumn mapToDateTimeColumn(String name, Function<? super T, ? extends LocalDateTime> function) {
        DateTimeColumn newColumn = DateTimeColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new DoubleColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into DoubleColumn.
     * @return DoubleColumn with the results appended.
     */
    public DoubleColumn mapToDoubleColumn(String name, Function<? super T, ? extends Double> function) {
        DoubleColumn newColumn = DoubleColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new FloatColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into FloatColumn.
     * @return FloatColumn with the results appended.
     */
    public FloatColumn mapToFloatColumn(String name, Function<? super T, ? extends Float> function) {
        FloatColumn newColumn = FloatColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new InstantColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into InstantColumn.
     * @return InstantColumn with the results appended.
     */
    public InstantColumn mapToInstantColumn(String name, Function<? super T, ? extends Instant> function) {
        InstantColumn newColumn = InstantColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new IntColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into IntColumn.
     * @return IntColumn with the results appended.
     */
    public IntColumn mapToIntColumn(String name, Function<? super T, ? extends Integer> function) {
        IntColumn newColumn = IntColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new LongColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into LongColumn.
     * @return LongColumn with the results appended.
     */
    public LongColumn mapToLongColumn(String name, Function<? super T, ? extends Long> function) {
        LongColumn newColumn = LongColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new ShortColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into ShortColumn.
     * @return ShortColumn with the results appended.
     */
    public ShortColumn mapToShortColumn(String name, Function<? super T, ? extends Short> function) {
        ShortColumn newColumn = ShortColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new StringColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into StringColumn.
     * @return StringColumn with the results appended.
     */
    public StringColumn mapToStringColumn(String name, Function<? super T, ? extends String> function) {
        StringColumn newColumn = StringColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new TextColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into TextColumn.
     * @return TextColumn with the results appended.
     */
    public TextColumn mapToTextColumn(String name, Function<? super T, ? extends String> function) {
        TextColumn newColumn = TextColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }

    /**
     * Maps the function across all rows, appending the results to a new TimeColumn.
     *
     * Ignores missing values.
     *
     * @param name of the new column.
     * @param function to map current column into TimeColumn.
     * @return TimeColumn with the results appended.
     */
    public TimeColumn mapToTimeColumn(String name, Function<? super T, ? extends LocalTime> function) {
        TimeColumn newColumn = TimeColumn.create(name, size());
        mapInto(function, newColumn);
        return newColumn;
    }
}