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

import com.google.common.base.Preconditions;
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

  private AbstractColumnParser<T> parser;

  /**
   * Constructs a column with the given {@link ColumnType}, name, and {@link AbstractColumnParser}
   */
  public AbstractColumn(ColumnType type, final String name, final AbstractColumnParser<T> parser) {
    this.type = type;
    setParser(parser);
    setName(name);
  }

  /** {@inheritDoc} */
  @Override
  public String name() {
    return name;
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C setName(final String name) {
    this.name = name.trim();
    return (C) this;
  }

  /** {@inheritDoc} */
  @Override
  public AbstractColumnParser<T> parser() {
    return parser;
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C setParser(final AbstractColumnParser<T> parser) {
    Preconditions.checkNotNull(parser);
    this.parser = parser;
    return (C) this;
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType type() {
    return type;
  }

  /** {@inheritDoc} */
  @Override
  public abstract Column<T> emptyCopy();

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C filter(Predicate<? super T> test) {
    return (C) Column.super.filter(test);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C sorted(Comparator<? super T> comp) {
    return (C) Column.super.sorted(comp);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C map(Function<? super T, ? extends T> fun) {
    return (C) Column.super.map(fun);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C min(Column<T> other) {
    return (C) Column.super.min(other);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C max(Column<T> other) {
    return (C) Column.super.max(other);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C set(Selection condition, Column<T> other) {
    return (C) Column.super.set(condition, other);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C set(Selection rowSelection, T newValue) {
    return (C) Column.super.set(rowSelection, newValue);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C first(int numRows) {
    return (C) Column.super.first(numRows);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C last(int numRows) {
    return (C) Column.super.last(numRows);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C sampleN(int n) {
    return (C) Column.super.sampleN(n);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C sampleX(double proportion) {
    return (C) Column.super.sampleX(proportion);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C subset(int[] rows) {
    return (C) Column.super.subset(rows);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public C inRange(int start, int end) {
    return (C) Column.super.inRange(start, end);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return type().getPrinterFriendlyName() + " column: " + name();
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn asStringColumn() {
    StringColumn sc = StringColumn.create(name() + " strings");
    for (int i = 0; i < size(); i++) {
      sc.append(getUnformattedString(i));
    }
    return sc;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final Object o) {
    return IntStream.range(0, size()).filter(i -> get(i).equals(o)).findFirst().orElse(-1);
  }

  /** {@inheritDoc} */
  @Override
  public int lastIndexOf(Object o) {
    return IntStream.iterate(size() - 1, i -> (i >= 0), i -> i - 1).filter(i -> get(i).equals(o))
            .findFirst().orElse(-1);
  }
}
