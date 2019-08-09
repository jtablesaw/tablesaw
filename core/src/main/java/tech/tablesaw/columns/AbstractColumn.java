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

/** Partial implementation of the {@link Column} interface */
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
    for (T value : this) {
      sc.append(String.valueOf(value));
    }
    return sc;
  }
}
