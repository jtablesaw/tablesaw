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

import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.interpolation.Interpolator;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.table.RollingColumn;
import tech.tablesaw.util.StringUtils;

/**
 * The general interface for columns.
 *
 * <p>Columns can either exist on their own or be a part of a table. All the data in a single column
 * is of a particular type.
 */
public interface Column<T> extends Iterable<T>, Comparator<T> {

  /** Returns the number of elements in this column, including missing values */
  int size();

  /** Returns a table containing a ColumnType specific summary of the data in this column */
  Table summary();

  /** Returns an array of objects as appropriate for my type of column */
  T[] asObjectArray();

  /**
   * Returns the count of missing values in this column.
   *
   * @return missing values as int
   */
  int countMissing();

  /**
   * Returns the count of unique values in this column.
   *
   * @return unique values as int
   */
  default int countUnique() {
    return unique().size();
  }

  /**
   * Returns the column's name.
   *
   * @return name as String
   */
  String name();

  /**
   * Returns this column's ColumnType
   *
   * @return {@link ColumnType}
   */
  ColumnType type();

  /**
   * Returns the parser used by {@link #appendCell(String)} ()}.
   *
   * @return {@link AbstractColumnParser}
   */
  AbstractColumnParser<T> parser();

  /**
   * Returns a string representation of the value at the given row.
   *
   * @param row The index of the row.
   * @return value as String
   */
  String getString(int row);

  /** Returns the value at the given zero-based index */
  T get(int row);

  /**
   * Reduction with binary operator and initial value
   *
   * @param initial initial value
   * @param op the operator
   * @return the result of reducing initial value and all rows with operator
   */
  default T reduce(T initial, BinaryOperator<T> op) {
    T acc = initial;
    for (T t : this) {
      acc = op.apply(acc, t);
    }
    return acc;
  }

  /**
   * Reduction with binary operator
   *
   * @param op the operator
   * @return Optional with the result of reducing all rows with operator
   */
  default Optional<T> reduce(BinaryOperator<T> op) {
    boolean first = true;
    T acc = null;
    for (T t : this) {
      if (first) {
        acc = t;
        first = false;
      } else {
        acc = op.apply(acc, t);
      }
    }
    return (first ? Optional.empty() : Optional.of(acc));
  }

  /** Removes all elements TODO: Make this return this column */
  void clear();

  /** Sorts my values in ascending order */
  void sortAscending();

  /** Sorts my values in descending order */
  void sortDescending();

  /**
   * Returns true if the column has no data
   *
   * @return true if empty, false if not
   */
  boolean isEmpty();

  /** Returns an IntComparator for sorting my rows */
  IntComparator rowComparator();

  default String title() {
    return "Column: " + name() + System.lineSeparator();
  }

  /** Returns a selection containing an index for every missing value in this column */
  Selection isMissing();

  /** Returns a selection containing an index for every non-missing value in this column */
  Selection isNotMissing();

  /**
   * Returns the width of a cell in this column, in bytes.
   *
   * @return width in bytes
   */
  int byteSize();

  /**
   * Returns the contents of the cell at rowNumber as a byte[].
   *
   * @param rowNumber index of the row
   * @return content as byte[]
   */
  byte[] asBytes(int rowNumber);

  /** Returns a Set containing all the unique values in this column */
  Set<T> asSet();

  /**
   * Returns a {@link RollingColumn} with the given windowSize, which can be used for performing
   * calculations on rolling subsets of my data
   *
   * @param windowSize The number of elements to include in each calculation
   * @return a RollingColumn
   */
  default RollingColumn rolling(final int windowSize) {
    return new RollingColumn(this, windowSize);
  }

  /** Returns a String representation of the value at index r, without any formatting applied */
  String getUnformattedString(int r);

  /** Returns true if the value at rowNumber is missing */
  boolean isMissing(int rowNumber);

  /** TODO(lwhite): Print n from the top and bottom, like a table; */
  default String print() {
    final StringBuilder builder = new StringBuilder();
    builder.append(title());
    for (int i = 0; i < size(); i++) {
      builder.append(getString(i));
      builder.append(System.lineSeparator());
    }
    return builder.toString();
  }

  /** Returns the width of the column in characters, for printing */
  default int columnWidth() {

    int width = name().length();
    for (int rowNum = 0; rowNum < size(); rowNum++) {
      width = Math.max(width, StringUtils.length(getString(rowNum)));
    }
    return width;
  }

  /**
   * Returns a list of all the elements in this column
   *
   * <p>Note, if a value in the column is missing, a {@code null} is added in it's place
   */
  default List<T> asList() {
    List<T> results = new ArrayList<>();
    for (int i = 0; i < this.size(); i++) {
      if (isMissing(i)) {
        results.add(null);
      } else {
        results.add(get(i));
      }
    }
    return results;
  }

  /**
   * Returns {@code true} if the given object appears in this column, and false otherwise
   *
   * <p>TODO override in column subtypes for performance
   */
  default boolean contains(T object) {
    for (int i = 0; i < this.size(); i++) {
      if (object != null) {
        if (object.equals(get(i))) {
          return true;
        }
      } else {
        if (get(i) == null) return true;
      }
    }
    return false;
  }

  // functional methods corresponding to those in Stream

  /**
   * Counts the number of rows satisfying predicate, but only upto the max value
   *
   * @param test the predicate
   * @param max the maximum number of rows to count
   * @return the number of rows satisfying the predicate
   */
  default int count(Predicate<? super T> test, int max) {
    int count = 0;
    for (T t : this) {
      if (test.test(t)) {
        count++;
        if (max > 0 && count >= max) {
          return count;
        }
      }
    }
    return count;
  }

  /**
   * Counts the number of rows satisfying predicate
   *
   * @param test the predicate
   * @return the number of rows satisfying the predicate
   */
  default int count(Predicate<? super T> test) {
    return count(test, size());
  }

  /**
   * Returns true if all rows satisfy the predicate, false otherwise
   *
   * @param test the predicate
   * @return true if all rows satisfy the predicate, false otherwise
   */
  default boolean allMatch(Predicate<? super T> test) {
    return count(test.negate(), 1) == 0;
  }

  /**
   * Returns true if any row satisfies the predicate, false otherwise
   *
   * @param test the predicate
   * @return true if any rows satisfies the predicate, false otherwise
   */
  default boolean anyMatch(Predicate<? super T> test) {
    return count(test, 1) > 0;
  }

  /**
   * Returns true if no row satisfies the predicate, false otherwise
   *
   * @param test the predicate
   * @return true if no row satisfies the predicate, false otherwise
   */
  default boolean noneMatch(Predicate<? super T> test) {
    return count(test, 1) == 0;
  }

  /**
   * Returns the maximum row according to the provided Comparator
   *
   * @param comp
   * @return the maximum row
   */
  default Optional<T> max(Comparator<? super T> comp) {
    boolean first = true;
    T o1 = null;
    for (T o2 : this) {
      if (first) {
        o1 = o2;
        first = false;
      } else if (comp.compare(o1, o2) < 0) {
        o1 = o2;
      }
    }
    return (first ? Optional.<T>empty() : Optional.<T>of(o1));
  }

  /**
   * Returns the minimum value according to the provided Comparator
   *
   * @param comp the Comparator to use
   * @return the minimum value
   */
  default Optional<T> min(Comparator<? super T> comp) {
    boolean first = true;
    T o1 = null;
    for (T o2 : this) {
      if (first) {
        o1 = o2;
        first = false;
      } else if (comp.compare(o1, o2) > 0) {
        o1 = o2;
      }
    }
    return (first ? Optional.<T>empty() : Optional.<T>of(o1));
  }

  /**
   * Maps the function across all rows, storing the results into the provided Column.
   *
   * <p>The target column must have at least the same number of rows.
   *
   * @param fun function to map
   * @param into Column into which results are set
   * @return the provided Column
   */
  default <R, C extends Column<R>> C mapInto(Function<? super T, ? extends R> fun, C into) {
    for (int i = 0; i < size(); i++) {
      if (isMissing(i)) {
        into.setMissing(i);
      } else {
        into.set(i, fun.apply(get(i)));
      }
    }
    return into;
  }

  /**
   * Maps the function across all rows, appending the results to the created Column.
   *
   * <p>Example:
   *
   * <pre>
   * DoubleColumn d;
   * StringColumn s = d.map(String::valueOf, StringColumn::create);
   * </pre>
   *
   * @param fun function to map
   * @param creator the creator of the Column. Its String argument will be the name of the current
   *     column (see {@link #name()})
   * @return the Column with the results
   */
  default <R, C extends Column<R>> C map(
      Function<? super T, ? extends R> fun, Function<String, C> creator) {
    C into = creator.apply(name());
    for (int i = 0; i < size(); i++) {
      if (isMissing(i)) {
        into.appendMissing();
      } else {
        into.append(fun.apply(get(i)));
      }
    }
    return into;
  }

  /**
   * Sets the value at index i to the missing-value indicator for this column type, and return this
   * column
   */
  Column<T> setMissing(int i);

  /**
   * Sets the value of any missing data in the column to newValue and returns the same column
   *
   * @param newValue the value to be used for all missing data in this column
   * @return the column updated
   */
  default Column<T> setMissingTo(T newValue) {
    for (int i = 0; i < size(); i++) {
      if (isMissing(i)) {
        set(i, newValue);
      }
    }
    return this;
  }

  /**
   * Returns a new Column of the same type with only those rows satisfying the predicate
   *
   * @param test the predicate
   * @return a new Column of the same type with only those rows satisfying the predicate
   */
  default Column<T> filter(Predicate<? super T> test) {
    Column<T> result = emptyCopy();
    for (T t : this) {
      if (test.test(t)) {
        result.append(t);
      }
    }
    return result;
  }

  /**
   * Return a column of the same type containing just those elements whose indexes are included in
   * the given array
   */
  default Column<T> subset(int[] rows) {
    final Column<T> c = this.emptyCopy();
    for (final int row : rows) {
      c.appendObj(get(row));
    }
    return c;
  }

  /**
   * Returns a new Column of the same type sorted according to the provided Comparator
   *
   * @param comp the Comparator
   * @return a sorted Column
   */
  default Column<T> sorted(Comparator<? super T> comp) {
    List<T> list = asList();
    list.sort(comp);
    Column<T> result = emptyCopy();
    for (T t : list) {
      result.append(t);
    }
    return result;
  }

  /**
   * Returns a copy of the receiver with no data. The column name and type are the same.
   *
   * @return a empty copy of {@link Column}
   */
  Column<T> emptyCopy();

  /**
   * Returns a deep copy of the receiver
   *
   * @return a {@link Column}
   */
  Column<T> copy();

  /**
   * Returns an empty copy of the receiver, with its internal storage initialized to the given row
   * size.
   *
   * @param rowSize the initial row size
   * @return a {@link Column}
   */
  Column<T> emptyCopy(int rowSize);

  /**
   * Maps the function across all rows, appending the results to a new Column of the same type
   *
   * @param fun function to map
   * @return the Column with the results
   */
  default Column<T> map(Function<? super T, ? extends T> fun) {
    return mapInto(fun, emptyCopy(size()));
  }

  /**
   * Returns a column containing the element-wise min between this column and other column
   *
   * <p>TODO(lwhite) Override in column subtypes for better performance
   */
  default Column<T> min(Column<T> other) {
    Preconditions.checkArgument(size() == other.size());
    Column<T> newCol = emptyCopy();
    for (int i = 0; i < this.size(); i++) {
      if (isMissing(i) || other.isMissing(i)) {
        newCol.appendMissing();
      } else {
        T thisValue = get(i);
        T otherValue = other.get(i);
        int result = compare(thisValue, otherValue);
        newCol.append(result <= 0 ? thisValue : otherValue);
      }
    }
    return newCol;
  }

  /**
   * Returns a column containing the element-wise min between this column and other column
   *
   * <p>TODO(lwhite) Override in column subtypes for better performance
   */
  default Column<T> max(Column<T> other) {
    Preconditions.checkArgument(size() == other.size());
    Column<T> newCol = emptyCopy();
    for (int i = 0; i < this.size(); i++) {
      if (isMissing(i) || other.isMissing(i)) {
        newCol.appendMissing();
      } else {
        T thisValue = get(i);
        T otherValue = other.get(i);
        int result = compare(thisValue, otherValue);
        newCol.append(result >= 0 ? thisValue : otherValue);
      }
    }
    return newCol;
  }

  /**
   * Updates this column where values matching the selection are replaced with the corresponding
   * value from the given column
   */
  default Column<T> set(Predicate<T> condition, Column<T> other) {
    for (int row = 0; row < size(); row++) {
      if (condition.test(get(row))) {
        set(row, other.get(row));
      }
    }
    return this;
  }

  /**
   * Updates this column where values matching the selection are replaced with the corresponding
   * value from the given column
   */
  default Column<T> set(Selection condition, Column<T> other) {
    for (int row : condition) {
      set(row, other.get(row));
    }
    return this;
  }

  /**
   * Returns a column of the same type as the receiver, containing the receivers values offset -n
   * For example if you lead a column containing 2, 3, 4 by 1, you get a column containing 3, 4, NA.
   */
  default Column<T> lead(final int n) {
    return lag(-n);
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   */
  default Column<T> set(Selection rowSelection, T newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /**
   * Returns a column of the same type and size as the receiver, containing the receivers values
   * offset by n.
   *
   * <p>For example if you lag a column containing 2, 3, 4 by 1, you get a column containing NA, 2,
   * 3
   */
  Column<T> lag(int n);

  /**
   * Add one element to the bottom of this column and set its value to the parsed value of the given
   * String. Parsing is type-specific
   */
  Column<T> appendCell(String stringValue);

  /**
   * Add one element to the bottom of this column and set its value to the parsed value of the given
   * String, as performed by the given parser
   */
  Column<T> appendCell(String stringValue, AbstractColumnParser<?> parser);

  /** Sets the value at index row to the given value and return this column */
  Column<T> set(int row, T value);

  /**
   * Sets the value at row to the parsed value of the given String using the given parser and
   * returns this column
   */
  @SuppressWarnings("unchecked")
  default Column<T> set(int row, String stringValue, AbstractColumnParser<?> parser) {
    AbstractColumnParser<T> typedParser = (AbstractColumnParser<T>) parser;
    return set(row, typedParser.parse(stringValue));
  }

  /** Sets the value at row to the value at sourceRow in the given column and return this column */
  Column<T> set(int row, Column<T> sourceColumn, int sourceRow);

  /** Appends value to the bottom of this column and return this column */
  Column<T> append(T value);

  /** Appends all the values in the argument to the bottom of this column and return this column */
  Column<T> append(Column<T> column);

  /**
   * Appends the value at the given row in the given column to the bottom of this column and return
   * this column
   */
  Column<T> append(Column<T> column, int row);

  /** Appends the given value to the bottom of this column and return this column */
  Column<T> appendObj(Object value);

  /** Appends a missing value appropriate to the column */
  Column<T> appendMissing();

  /** Returns an int suitable as a hash for the value in this column at the given index */
  int valueHash(int rowNumber);

  /** Returns true if the value in this column at rowNumber1 is equal to the value at rowNumber2 */
  boolean equals(int rowNumber1, int rowNumber2);

  /** Returns a new column containing the subset referenced by the {@link Selection} */
  Column<T> where(Selection selection);

  /** Returns a copy of this column with the missing values removed */
  Column<T> removeMissing();

  /**
   * Returns a column of the same type containing only the unique values
   *
   * @return a {@link Column}
   */
  Column<T> unique();

  /** Returns a column of the same type containing the first {@code numRows} of this column. */
  default Column<T> first(final int numRows) {
    int newRowCount = Math.min(numRows, size());
    return inRange(0, newRowCount);
  }

  /** Returns a column of the same type containing the last {@code numRows} of this column. */
  default Column<T> last(final int numRows) {
    int newRowCount = Math.min(numRows, size());
    return inRange(size() - newRowCount, size());
  }

  /**
   * Sets the columns name to the given string
   *
   * @param name The new name MUST be unique for any table containing this column
   * @return this Column to allow method chaining
   */
  Column<T> setName(String name);

  /**
   * Sets the parser used by {@link #appendCell(String)}
   *
   * @param parser a column parser that converts text input to the column data type
   * @return this Column to allow method chaining
   */
  Column<T> setParser(AbstractColumnParser<T> parser);

  /**
   * Returns a column containing the rows in this column beginning with start inclusive, and ending
   * with end exclusive
   */
  default Column<T> inRange(int start, int end) {
    Preconditions.checkArgument(start < end);
    Preconditions.checkArgument(end <= size());
    return where(Selection.withRange(start, end));
  }

  /**
   * Returns a column containing a random sample of the values in this column
   *
   * @param n the number of values to select
   * @return A column of the same type as the receiver
   */
  default Column<T> sampleN(int n) {
    Preconditions.checkArgument(
        n > 0 && n < size(),
        "The number of rows sampled must be greater than 0 and less than the number of rows in the table.");
    return where(selectNRowsAtRandom(n, size()));
  }

  /**
   * Returns a table consisting of randomly selected values from this column. The sample size is
   * based on the given proportion of the total number of cells in this column
   *
   * @param proportion The proportion to go in the sample
   */
  default Column<T> sampleX(double proportion) {
    Preconditions.checkArgument(
        proportion <= 1 && proportion >= 0, "The sample proportion must be between 0 and 1");

    int tableSize = (int) Math.round(size() * proportion);
    return where(selectNRowsAtRandom(tableSize, size()));
  }

  /**
   * Provides the ability to create a new column with missing cells filled based off the value of
   * nearby cells.
   */
  default Interpolator<T> interpolate() {
    return new Interpolator<>(this);
  }

  /**
   * Returns a StringColumn consisting of the (unformatted) String representation of this column
   * values
   *
   * @return a {@link StringColumn} built using the column {@link #getUnformattedString} method
   */
  StringColumn asStringColumn();

  /**
   * Returns the index of the first occurrence of {@code o} in the column or -1 if the element is
   * not in the column.
   */
  int indexOf(Object o);

  /**
   * Returns the index of the last occurrence of {@code o} in the column or -1 if the element is not
   * in the column.
   */
  int lastIndexOf(Object o);
}
