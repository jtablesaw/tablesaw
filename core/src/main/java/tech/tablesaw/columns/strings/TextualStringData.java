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

package tech.tablesaw.columns.strings;

import static tech.tablesaw.columns.AbstractColumn.DEFAULT_ARRAY_SIZE;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.*;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A column that contains String values. They are assumed to be free-form text. For categorical
 * data, use stringColumn
 *
 * <p>This is the default column type for SQL longvarchar and longnvarchar types
 *
 * <p>Because the MISSING_VALUE for this column type is an empty string, there is little or no need
 * for special handling of missing values in this class's methods.
 */
public class TextualStringData implements StringData {

  // holds each element in the column.
  protected List<String> values;

  private final IntComparator rowComparator =
      (i, i1) -> {
        String f1 = get(i);
        String f2 = get(i1);
        return f1.compareTo(f2);
      };

  private final Comparator<String> descendingStringComparator = Comparator.reverseOrder();

  public int valueHash(int rowNumber) {
    return get(rowNumber).hashCode();
  }

  /** {@inheritDoc} */
  public boolean equals(int rowNumber1, int rowNumber2) {
    return get(rowNumber1).equals(get(rowNumber2));
  }

  private TextualStringData(Collection<String> strings) {
    values = new ArrayList<>(strings.size());
    for (String string : strings) {
      append(string);
    }
  }

  private TextualStringData() {
    values = new ArrayList<>(DEFAULT_ARRAY_SIZE);
  }

  private TextualStringData(String[] strings) {
    values = new ArrayList<>(strings.length);
    for (String string : strings) {
      append(string);
    }
  }

  public static boolean valueIsMissing(String string) {
    return StringColumnType.valueIsMissing(string);
  }

  public TextualStringData appendMissing() {
    append(StringColumnType.missingValueIndicator());
    return this;
  }

  public static TextualStringData create() {
    return new TextualStringData();
  }

  public static TextualStringData create(String... strings) {
    return new TextualStringData(strings);
  }

  public static TextualStringData create(Collection<String> strings) {
    return new TextualStringData(strings);
  }

  public static TextualStringData create(int size) {
    ArrayList<String> strings = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      strings.add(StringColumnType.missingValueIndicator());
    }
    return new TextualStringData(strings);
  }

  public static TextualStringData create(Stream<String> stream) {
    TextualStringData column = create();
    stream.forEach(column::append);
    return column;
  }

  /** {@inheritDoc} */
  public boolean isMissing(int rowNumber) {
    return get(rowNumber).equals(StringColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  public TextualStringData emptyCopy() {
    return create();
  }

  /** {@inheritDoc} */
  public TextualStringData emptyCopy(int rowSize) {
    return create(rowSize);
  }

  /** {@inheritDoc} */
  public void sortAscending() {
    values.sort(String::compareTo);
  }

  /** {@inheritDoc} */
  public void sortDescending() {
    values.sort(descendingStringComparator);
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   *
   * @return size as int
   */
  public int size() {
    return values.size();
  }

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @param rowIndex index of the row
   * @return value as String
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  public String get(int rowIndex) {
    return values.get(rowIndex);
  }

  /**
   * Returns a List&lt;String&gt; representation of all the values in this column
   *
   * <p>NOTE: Unless you really need a string consider using the column itself for large datasets as
   * it uses much less memory
   *
   * @return values as a list of String.
   */
  public List<String> asList() {
    return new ArrayList<>(values);
  }

  @Override
  public Table countByCategory(String columnName) {
    throw new UnsupportedOperationException();
    // TODO: fix me
    // return asCategoricalStringData().countByCategory(columnName);
  }

  /** {@inheritDoc} */
  public Table summary() {
    // Table table = Table.create("Column: " + name());
    Table table = Table.create();
    StringColumn measure = StringColumn.create("Measure");
    StringColumn value = StringColumn.create("Value");
    table.addColumns(measure);
    table.addColumns(value);

    measure.append("Count");
    value.append(String.valueOf(size()));

    measure.append("Missing");
    value.append(String.valueOf(countMissing()));
    return table;
  }

  /** {@inheritDoc} */
  public void clear() {
    values.clear();
  }

  /** {@inheritDoc} */
  public TextualStringData lead(int n) {
    return lag(-n);
  }

  /** {@inheritDoc} */
  public TextualStringData lag(int n) {

    TextualStringData copy = emptyCopy();

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
  public TextualStringData set(Selection rowSelection, String newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /** {@inheritDoc} */
  public TextualStringData set(int rowIndex, String stringValue) {
    if (stringValue == null) {
      return setMissing(rowIndex);
    }
    values.set(rowIndex, stringValue);
    return this;
  }

  /** {@inheritDoc} */
  public int countUnique() {
    return asSet().size();
  }

  /**
   * Returns true if this column contains a cell with the given string, and false otherwise
   *
   * @param aString the value to look for
   * @return true if contains, false otherwise
   */
  public boolean contains(String aString) {
    return values.contains(aString);
  }

  /** {@inheritDoc} */
  public TextualStringData setMissing(int i) {
    return set(i, StringColumnType.missingValueIndicator());
  }

  /**
   * Add all the strings in the list to this column
   *
   * @param stringValues a list of values
   */
  public TextualStringData addAll(List<String> stringValues) {
    for (String stringValue : stringValues) {
      append(stringValue);
    }
    return this;
  }

  /** {@inheritDoc} */
  public IntComparator rowComparator() {
    return rowComparator;
  }

  /** {@inheritDoc} */
  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns a new Column containing all the unique values in this column
   *
   * @return a column with unique values.
   */
  public TextualStringData unique() {
    List<String> strings = new ArrayList<>(asSet());
    return TextualStringData.create(strings);
  }

  /** {@inheritDoc} */
  public TextualStringData where(Selection selection) {
    return (TextualStringData) subset(selection.toArray());
  }

  // TODO (lwhite): This could avoid the append and do a list copy
  /** {@inheritDoc} */
  public TextualStringData copy() {
    TextualStringData newCol = create(size());
    int r = 0;
    for (String string : this) {
      newCol.set(r, string);
      r++;
    }
    return newCol;
  }

  /** {@inheritDoc} */
  public void append(Column<String> column) {
    final int size = column.size();
    for (int i = 0; i < size; i++) {
      append(column.getString(i));
    }
  }

  /** Returns the count of missing values in this column */
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (StringColumnType.missingValueIndicator().equals(get(i))) {
        count++;
      }
    }
    return count;
  }

  /** {@inheritDoc} */
  public TextualStringData removeMissing() {
    TextualStringData noMissing = emptyCopy();
    for (String v : this) {
      if (!StringColumnType.valueIsMissing(v)) {
        noMissing.append(v);
      }
    }
    return noMissing;
  }

  /** {@inheritDoc} */
  public Iterator<String> iterator() {
    return values.iterator();
  }

  /** {@inheritDoc} */
  public Set<String> asSet() {
    return new HashSet<>(values);
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  public byte[] asBytes(int rowNumber) {
    String value = get(rowNumber);
    return value.getBytes();
  }

  /** Added for naming consistency with all other columns */
  public TextualStringData append(String value) {
    values.add(value);
    return this;
  }

  /** {@inheritDoc} */
  public TextualStringData appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (!(obj instanceof String)) {
      throw new IllegalArgumentException(
          "Cannot append " + obj.getClass().getName() + " to TextColumn");
    }
    return append((String) obj);
  }

  /** {@inheritDoc} */
  public Selection isIn(String... strings) {
    Set<String> stringSet = Sets.newHashSet(strings);

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (stringSet.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  /** {@inheritDoc} */
  public Selection isIn(Collection<String> strings) {
    Set<String> stringSet = Sets.newHashSet(strings);

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (stringSet.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  /** {@inheritDoc} */
  public Selection isNotIn(String... strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  /** {@inheritDoc} */
  public Selection isNotIn(Collection<String> strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  public int firstIndexOf(String value) {
    return values.indexOf(value);
  }

  /** {@inheritDoc} */
  public String[] asObjectArray() {
    final String[] output = new String[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  /**
   * Returns a double that can stand in for the string at index i in some ML applications
   *
   * <p>TODO: Evaluate use of hashCode() here for uniqueness
   *
   * @param i The index in this column
   */
  public double getDouble(int i) {
    return values.get(i).hashCode();
  }

  public double[] asDoubleArray() {
    double[] result = new double[this.size()];
    for (int i = 0; i < size(); i++) {
      result[i] = getDouble(i);
    }
    return result;
  }

  public int countOccurrences(String value) {
    return isEqualTo(value).size();
  }

  /**
   * {@inheritDoc} Unsupported Operation This can't be used on a text column as the number of
   * BooleanColumns would likely be excessive
   */
  public List<BooleanColumn> getDummies() {
    throw new UnsupportedOperationException(
        "StringColumns containing arbitary, non-categorical strings do not support the getDummies() method for performance reasons");
  }

  /** Returns null, as this Column is not backed by a dictionaryMap */
  public @Nullable DictionaryMap getDictionary() {
    return null;
  }
}
