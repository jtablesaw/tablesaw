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

import static com.google.common.base.Preconditions.checkArgument;
import static tech.tablesaw.api.ColumnType.*;
import static tech.tablesaw.columns.strings.BackingStringColumnType.BACKING_STRING;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.AbstractStringColumn;
import tech.tablesaw.columns.strings.BackingStringColumnType;
import tech.tablesaw.columns.strings.DictionaryMap;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A column that contains String values. They are assumed to be 'categorical' rather than free-form
 * text, so are stored in an encoding that takes advantage of the expected repetition of string
 * values.
 *
 * <p>Because the MISSING_VALUE for this column type is an empty string, there is little or no need
 * for special handling of missing values in this class's methods.
 */
public class StringColumn extends AbstractStringColumn<StringColumn> {

  private final AbstractStringColumn<?> backingColumn;

  public static boolean valueIsMissing(String string) {
    return StringColumnType.valueIsMissing(string);
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn appendMissing() {
    backingColumn.appendMissing();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int valueHash(int rowNumber) {
    return get(rowNumber).hashCode();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return getDictionary().getKeyAtIndex(rowNumber1) == getDictionary().getKeyAtIndex(rowNumber2);
  }

  public BackingStringColumnType backingColumnType() {
    return backingColumn.backingColumnType();
  }

  public static StringColumn create(String name) {
    return new StringColumn(name);
  }

  public static StringColumn create(String name, BackingStringColumnType backingType) {
    if (backingType.equals(BACKING_STRING)) {
      return new StringColumn(name, BackingStringColumn.create(name));
    } else if (backingType.equals(BackingStringColumnType.BACKING_TEXT)) {
      return new StringColumn(name, BackingTextColumn.create(name));
    }
    throw new RuntimeException("Unhandled backing column type for StringColumn: " + backingType);
  }

  public static StringColumn create(String name, String... strings) {
    return new StringColumn(name, strings);
  }

  public static StringColumn create(String name, AbstractStringColumn<?> backingColumn) {
    return new StringColumn(name, backingColumn);
  }

  public static StringColumn create(String name, Collection<String> strings) {
    return new StringColumn(name, strings);
  }

  public static StringColumn createInternal(String name, DictionaryMap map) {
    return new StringColumn(name, map);
  }

  public static StringColumn create(String name, int size) {
    StringColumn column = new StringColumn(name);
    for (int i = 0; i < size; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static StringColumn create(String name, Stream<String> stream) {
    StringColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  private StringColumn(String name, Collection<String> strings) {
    super(StringColumnType.instance(), name, StringColumnType.DEFAULT_PARSER);
    backingColumn = BackingStringColumn.create(name, strings);
  }

  private StringColumn(String name, DictionaryMap map) {
    super(StringColumnType.instance(), name, StringColumnType.DEFAULT_PARSER);
    backingColumn = BackingStringColumn.createInternal(name, map);
  }

  private StringColumn(String name) {
    super(StringColumnType.instance(), name, StringColumnType.DEFAULT_PARSER);
    backingColumn = BackingStringColumn.create(name);
  }

  private StringColumn(String name, AbstractStringColumn<?> backingColumn) {
    super(StringColumnType.instance(), name, StringColumnType.DEFAULT_PARSER);
    this.backingColumn = backingColumn;
  }

  private StringColumn(String name, String[] strings) {
    super(StringColumnType.instance(), name, StringColumnType.DEFAULT_PARSER);
    backingColumn = BackingStringColumn.create(name);
    for (String string : strings) {
      append(string);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return backingColumn.isMissing(rowNumber);
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn emptyCopy() {
    StringColumn empty = create(name());
    empty.setPrintFormatter(getPrintFormatter());
    return empty;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn emptyCopy(int rowSize) {
    return create(name(), rowSize);
  }

  /** {@inheritDoc} */
  @Override
  public void sortAscending() {
    backingColumn.sortAscending();
  }

  /** {@inheritDoc} */
  @Override
  public void sortDescending() {
    backingColumn.sortDescending();
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   *
   * @return size as int
   */
  @Override
  public int size() {
    return backingColumn.size();
  }

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @param rowIndex index of the row
   * @return value as String
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  @Override
  public String get(int rowIndex) {
    return backingColumn.get(rowIndex);
  }

  /**
   * Returns a List&lt;String&gt; representation of all the values in this column
   *
   * <p>NOTE: Unless you really need a string consider using the column itself for large datasets as
   * it uses much less memory
   *
   * @return values as a list of String.
   */
  @Override
  public List<String> asList() {
    List<String> strings = new ArrayList<>();
    for (String category : this) {
      strings.add(category);
    }
    return strings;
  }

  /** {@inheritDoc} */
  @Override
  public Table summary() {
    Table summary = Table.create(this.name());
    StringColumn measure = StringColumn.create("Measure");
    StringColumn value = StringColumn.create("Value");
    summary.addColumns(measure);
    summary.addColumns(value);

    measure.append("Count");
    value.append(String.valueOf(size()));

    measure.append("Unique");
    value.append(String.valueOf(countUnique()));

    Table countByCategory = countByCategory().sortDescendingOn("Count");
    measure.append("Top");
    value.append(countByCategory.stringColumn("Category").getString(0));

    measure.append("Top Freq.");
    value.appendObj(countByCategory.intColumn("Count").getString(0));
    return summary;
  }

  /** {@inheritDoc} */
  @Override
  public Table countByCategory() {
    return backingColumn.countByCategory();
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    backingColumn.clear();
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn lead(int n) {
    StringColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn lag(int n) {

    StringColumn copy = emptyCopy();
    copy.setName(name() + " lag(" + n + ")");

    if (n >= 0) {
      for (int m = 0; m < n; m++) {
        copy.appendMissing();
      }
      for (int i = 0; i < size(); i++) {
        if (i + n >= size()) {
          break;
        }
        copy.append(get(i));
      }
    } else {
      for (int i = -n; i < size(); i++) {
        copy.append(get(i));
      }
      for (int m = 0; m > n; m--) {
        copy.appendMissing();
      }
    }

    return copy;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   *
   * <p>Examples: myCatColumn.set(myCatColumn.isEqualTo("Cat"), "Dog"); // no more cats
   * myCatColumn.set(myCatColumn.valueIsMissing(), "Fox"); // no more missing values
   */
  @Override
  public StringColumn set(Selection rowSelection, String newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn set(int rowIndex, String stringValue) {
    backingColumn.set(rowIndex, stringValue);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int countUnique() {
    return backingColumn.countUnique();
  }

  /**
   * Returns true if this column contains a cell with the given string, and false otherwise
   *
   * @param aString the value to look for
   * @return true if contains, false otherwise
   */
  @Override
  public boolean contains(String aString) {
    return firstIndexOf(aString) >= 0;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn setMissing(int i) {
    return set(i, StringColumnType.missingValueIndicator());
  }

  /**
   * Add all the strings in the list to this column
   *
   * @param stringValues a list of values
   */
  public StringColumn addAll(List<String> stringValues) {
    for (String stringValue : stringValues) {
      append(stringValue);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn appendCell(String object) {
    return appendCell(object, parser());
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn appendCell(String object, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(object));
  }

  /** {@inheritDoc} */
  @Override
  public IntComparator rowComparator() {
    return backingColumn.rowComparator();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {
    return backingColumn.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public Selection isEqualTo(String string) {
    return backingColumn.isEqualTo(string);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotEqualTo(String string) {
    return backingColumn.isNotEqualTo(string);
  }

  /**
   * Returns a list of boolean columns suitable for use as dummy variables in, for example,
   * regression analysis, select a column of categorical data must be encoded as a list of columns,
   * such that each column represents a single category and indicates whether it is present (1) or
   * not present (0)
   *
   * @return a list of {@link BooleanColumn}
   */
  public List<BooleanColumn> getDummies() {
    return backingColumn.getDummies();
  }

  /**
   * Returns a new Column containing all the unique values in this column
   *
   * @return a column with unique values.
   */
  @Override
  public StringColumn unique() {
    return new StringColumn(name(), (BackingStringColumn) backingColumn.unique());
  }

  public DoubleColumn asDoubleColumn() {
    return DoubleColumn.create(this.name(), asDoubleArray());
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn copy() {
    StringColumn newCol = create(name(), (AbstractStringColumn<?>) backingColumn.copy());
    int r = 0;
    for (String string : this) {
      newCol.set(r, string);
      r++;
    }
    newCol.setPrintFormatter(getPrintFormatter());
    return newCol;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn append(Column<String> column) {
    checkArgument(
        column.type() == TEXT || column.type().equals(STRING),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    final int size = column.size();
    for (int i = 0; i < size; i++) {
      append(column.getString(i));
    }
    return this;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    return backingColumn.countMissing();
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn removeMissing() {
    StringColumn noMissing = emptyCopy();
    for (String v : this) {
      if (!StringColumnType.valueIsMissing(v)) {
        noMissing.append(v);
      }
    }
    return noMissing;
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<String> iterator() {
    return backingColumn.iterator();
  }

  public Set<String> asSet() {
    return backingColumn.asSet();
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    return backingColumn.asBytes(rowNumber);
  }

  @Override
  public double getDouble(int i) {
    return backingColumn.getDouble(i);
  }

  public double[] asDoubleArray() {
    return backingColumn.asDoubleArray();
  }

  /** Added for naming consistency with all other columns */
  @Override
  public StringColumn append(String value) {
    backingColumn.append(value);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (!(obj instanceof String)) {
      throw new IllegalArgumentException(
          "Cannot append " + obj.getClass().getName() + " to StringColumn");
    }
    return append((String) obj);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isIn(String... strings) {
    return backingColumn.isIn(strings);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isIn(Collection<String> strings) {
    return backingColumn.isIn(strings);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotIn(String... strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotIn(Collection<String> strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  public int firstIndexOf(String value) {
    return backingColumn.firstIndexOf(value);
  }

  public int countOccurrences(String value) {
    return backingColumn.countOccurrences(value);
  }

  /** {@inheritDoc} */
  @Override
  public String[] asObjectArray() {
    return backingColumn.asObjectArray();
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn asStringColumn() {
    return copy();
  }

  public TextColumn asTextColumn() {
    TextColumn textColumn = TextColumn.create(name(), size());
    for (int i = 0; i < size(); i++) {
      textColumn.set(i, get(i));
    }
    return textColumn;
  }

  /**
   * For tablesaw internal use Note: This method returns null if the backingColumn is a text column
   */
  public @Nullable DictionaryMap getDictionary() {
    return backingColumn.getDictionary();
  }
}
