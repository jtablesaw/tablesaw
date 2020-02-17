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

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.AbstractStringColumn;
import tech.tablesaw.columns.strings.TextColumnType;
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
public class TextColumn extends AbstractStringColumn<TextColumn> {

  // holds each element in the column.
  private List<String> values;

  private final IntComparator rowComparator =
      (i, i1) -> {
        String f1 = get(i);
        String f2 = get(i1);
        return f1.compareTo(f2);
      };

  private final Comparator<String> descendingStringComparator = Comparator.reverseOrder();

  private TextColumn(String name, Collection<String> strings) {
    super(TextColumnType.instance(), name);
    values = new ArrayList<>(strings.size());
    for (String string : strings) {
      append(string);
    }
  }

  private TextColumn(String name) {
    super(TextColumnType.instance(), name);
    values = new ArrayList<>(DEFAULT_ARRAY_SIZE);
  }

  private TextColumn(String name, String[] strings) {
    super(TextColumnType.instance(), name);
    values = new ArrayList<>(strings.length);
    for (String string : strings) {
      append(string);
    }
  }

  public static boolean valueIsMissing(String string) {
    return TextColumnType.valueIsMissing(string);
  }

  @Override
  public TextColumn appendMissing() {
    append(TextColumnType.missingValueIndicator());
    return this;
  }

  public static TextColumn create(String name) {
    return new TextColumn(name);
  }

  public static TextColumn create(String name, String... strings) {
    return new TextColumn(name, strings);
  }

  public static TextColumn create(String name, Collection<String> strings) {
    return new TextColumn(name, strings);
  }

  public static TextColumn create(String name, int size) {
    ArrayList<String> strings = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      strings.add(TextColumnType.missingValueIndicator());
    }
    return new TextColumn(name, strings);
  }

  public static TextColumn create(String name, Stream<String> stream) {
    TextColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return get(rowNumber).equals(TextColumnType.missingValueIndicator());
  }

  @Override
  public TextColumn emptyCopy() {
    return create(name());
  }

  @Override
  public TextColumn emptyCopy(int rowSize) {
    return create(name(), rowSize);
  }

  @Override
  public void sortAscending() {
    values.sort(String::compareTo);
  }

  @Override
  public void sortDescending() {
    values.sort(descendingStringComparator);
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   *
   * @return size as int
   */
  @Override
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
  @Override
  public List<String> asList() {
    return new ArrayList<>(values);
  }

  @Override
  public Table summary() {
    Table table = Table.create("Column: " + name());
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

  @Override
  public void clear() {
    values.clear();
  }

  @Override
  public TextColumn lead(int n) {
    TextColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  @Override
  public TextColumn lag(int n) {

    TextColumn copy = emptyCopy();
    copy.setName(name() + " lag(" + n + ")");

    if (n >= 0) {
      for (int m = 0; m < n; m++) {
        copy.appendCell(TextColumnType.missingValueIndicator());
      }
      for (int i = 0; i < size(); i++) {
        if (i + n >= size()) {
          break;
        }
        copy.appendCell(get(i));
      }
    } else {
      for (int i = -n; i < size(); i++) {
        copy.appendCell(get(i));
      }
      for (int m = 0; m > n; m--) {
        copy.appendCell(TextColumnType.missingValueIndicator());
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
  public TextColumn set(Selection rowSelection, String newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  @Override
  public TextColumn set(int rowIndex, String stringValue) {
    String str = TextColumnType.missingValueIndicator();
    if (stringValue != null) {
      str = stringValue;
    }
    values.set(rowIndex, str);
    return this;
  }

  @Override
  public int countUnique() {
    return asSet().size();
  }

  /**
   * Returns true if this column contains a cell with the given string, and false otherwise
   *
   * @param aString the value to look for
   * @return true if contains, false otherwise
   */
  @Override
  public boolean contains(String aString) {
    return values.contains(aString);
  }

  @Override
  public Column<String> setMissing(int i) {
    return set(i, TextColumnType.missingValueIndicator());
  }

  /**
   * Add all the strings in the list to this column
   *
   * @param stringValues a list of values
   */
  public TextColumn addAll(List<String> stringValues) {
    for (String stringValue : stringValues) {
      append(stringValue);
    }
    return this;
  }

  @Override
  public TextColumn appendCell(String object) {
    values.add(TextColumnType.DEFAULT_PARSER.parse(object));
    return this;
  }

  @Override
  public TextColumn appendCell(String object, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(object));
  }

  @Override
  public IntComparator rowComparator() {
    return rowComparator;
  }

  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns a new Column containing all the unique values in this column
   *
   * @return a column with unique values.
   */
  @Override
  public TextColumn unique() {
    List<String> strings = new ArrayList<>(asSet());
    return TextColumn.create(name() + " Unique values", strings);
  }

  public TextColumn where(Selection selection) {
    return (TextColumn) subset(selection.toArray());
  }

  // TODO (lwhite): This could avoid the append and do a list copy
  @Override
  public TextColumn copy() {
    TextColumn newCol = create(name(), size());
    int r = 0;
    for (String string : this) {
      newCol.set(r, string);
      r++;
    }
    return newCol;
  }

  @Override
  public TextColumn append(Column<String> column) {
    Preconditions.checkArgument(column.type() == this.type());
    TextColumn source = (TextColumn) column;
    final int size = source.size();
    for (int i = 0; i < size; i++) {
      append(source.getString(i));
    }
    return this;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (TextColumnType.missingValueIndicator().equals(get(i))) {
        count++;
      }
    }
    return count;
  }

  @Override
  public TextColumn removeMissing() {
    TextColumn noMissing = emptyCopy();
    for (String v : this) {
      if (!TextColumnType.valueIsMissing(v)) {
        noMissing.append(v);
      }
    }
    return noMissing;
  }

  @Override
  public Iterator<String> iterator() {
    return values.iterator();
  }

  public Set<String> asSet() {
    return new HashSet<>(values);
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    return new byte[0];
    // TODO (lwhite): FIX ME:  return
    // ByteBuffer.allocate(byteSize()).putInt(getInt(rowNumber)).array();
  }

  /** Added for naming consistency with all other columns */
  public TextColumn append(String value) {
    appendCell(value);
    return this;
  }

  @Override
  public TextColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (!(obj instanceof String)) {
      throw new IllegalArgumentException(
          "Cannot append " + obj.getClass().getName() + " to TextColumn");
    }
    return append((String) obj);
  }

  @Override
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

  @Override
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

  @Override
  public Selection isNotIn(String... strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  @Override
  public Selection isNotIn(Collection<String> strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  public int firstIndexOf(String value) {
    return values.indexOf(value);
  }

  @Override
  public String[] asObjectArray() {
    final String[] output = new String[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  @Override
  public StringColumn asStringColumn() {
    StringColumn textColumn = StringColumn.create(name(), size());
    for (int i = 0; i < size(); i++) {
      textColumn.set(i, get(i));
    }
    return textColumn;
  }
}
