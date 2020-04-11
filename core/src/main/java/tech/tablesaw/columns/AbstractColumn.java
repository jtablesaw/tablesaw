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

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.selection.Selection;

/** Partial implementation of the {@link Column} interface */
public abstract class AbstractColumn<C extends Column<T>, T> implements Column<T> {

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
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C setName(final String name) {
    this.name = name.trim();
    return (C) this;
  }

  @Override
  public ColumnType type() {
    return type;
  }

  @Override
  public abstract Column<T> emptyCopy();

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C filter(Predicate<? super T> test) {
    return (C) Column.super.filter(test);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C sorted(Comparator<? super T> comp) {
    return (C) Column.super.sorted(comp);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C map(Function<? super T, ? extends T> fun) {
    return (C) Column.super.map(fun);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C min(Column<T> other) {
    return (C) Column.super.min(other);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C max(Column<T> other) {
    return (C) Column.super.max(other);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C set(Selection condition, Column<T> other) {
    return (C) Column.super.set(condition, other);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C set(Selection rowSelection, T newValue) {
    return (C) Column.super.set(rowSelection, newValue);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C first(int numRows) {
    return (C) Column.super.first(numRows);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C last(int numRows) {
    return (C) Column.super.last(numRows);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C sampleN(int n) {
    return (C) Column.super.sampleN(n);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C sampleX(double proportion) {
    return (C) Column.super.sampleX(proportion);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C subset(int[] rows) {
    return (C) Column.super.subset(rows);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C inRange(int start, int end) {
    return (C) Column.super.inRange(start, end);
  }

  @Override
  public String toString() {
    return type().getPrinterFriendlyName() + " column: " + name();
  }

  @Override
  public StringColumn asStringColumn() {
    StringColumn sc = StringColumn.create(name() + " strings");
    for (int i = 0; i < size(); i++) {
      sc.append(getUnformattedString(i));
    }
    return sc;
  }

  @Override
  public int indexOf(final Object o) {
    return IntStream.range(0, size()).filter(i -> get(i).equals(o)).findFirst().orElse(-1);
  }
}
