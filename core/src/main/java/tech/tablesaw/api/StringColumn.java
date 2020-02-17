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
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.AbstractStringColumn;
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.DictionaryMap;
import tech.tablesaw.columns.strings.LookupTableWrapper;
import tech.tablesaw.columns.strings.NoKeysAvailableException;
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

  // a bidirectional map of keys to backing string values.
  private DictionaryMap lookupTable = new ByteDictionaryMap();

  private final IntComparator rowComparator =
      (i, i1) -> {
        String f1 = get(i);
        String f2 = get(i1);
        return f1.compareTo(f2);
      };

  public static boolean valueIsMissing(String string) {
    return StringColumnType.valueIsMissing(string);
  }

  @Override
  public StringColumn appendMissing() {
    lookupTable.appendMissing();
    return this;
  }

  public static StringColumn create(String name) {
    return new StringColumn(name);
  }

  public static StringColumn create(String name, String... strings) {
    return new StringColumn(name, strings);
  }

  public static StringColumn create(String name, Collection<String> strings) {
    return new StringColumn(name, strings);
  }

  public static StringColumn createInternal(String name, DictionaryMap map) {
    return new StringColumn(name, map);
  }

  public static StringColumn create(String name, int size) {
    StringColumn column = new StringColumn(name, new ArrayList<>(size));
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
    super(StringColumnType.instance(), name);
    for (String string : strings) {
      append(string);
    }
  }

  private StringColumn(String name, DictionaryMap map) {
    super(StringColumnType.instance(), name);
    lookupTable = map;
  }

  private StringColumn(String name) {
    super(StringColumnType.instance(), name);
  }

  private StringColumn(String name, String[] strings) {
    super(StringColumnType.instance(), name);
    for (String string : strings) {
      append(string);
    }
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return lookupTable.isMissing(rowNumber);
  }

  @Override
  public StringColumn emptyCopy() {
    return create(name());
  }

  @Override
  public StringColumn emptyCopy(int rowSize) {
    return create(name(), rowSize);
  }

  @Override
  public void sortAscending() {
    lookupTable.sortAscending();
  }

  @Override
  public void sortDescending() {
    lookupTable.sortDescending();
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   *
   * @return size as int
   */
  @Override
  public int size() {
    return lookupTable.size();
  }

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @param rowIndex index of the row
   * @return value as String
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  public String get(int rowIndex) {
    return lookupTable.getValueForIndex(rowIndex);
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

  /** */
  @Override
  public Table countByCategory() {
    return lookupTable.countByCategory(name());
  }

  @Override
  public void clear() {
    lookupTable.clear();
  }

  @Override
  public StringColumn lead(int n) {
    StringColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

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
        copy.appendCell(get(i));
      }
    } else {
      for (int i = -n; i < size(); i++) {
        copy.appendCell(get(i));
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

  @Override
  public StringColumn set(int rowIndex, String stringValue) {
    try {
      lookupTable.set(rowIndex, stringValue);
    } catch (NoKeysAvailableException ex) {
      lookupTable = lookupTable.promoteYourself();
      try {
        lookupTable.set(rowIndex, stringValue);
      } catch (NoKeysAvailableException e) {
        // this can't happen
        throw new IllegalStateException(e);
      }
    }
    return this;
  }

  @Override
  public int countUnique() {
    return lookupTable.countUnique();
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

  @Override
  public Column<String> setMissing(int i) {
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

  @Override
  public StringColumn appendCell(String object) {
    return appendCell(object, StringColumnType.DEFAULT_PARSER);
  }

  @Override
  public StringColumn appendCell(String object, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(object));
  }

  @Override
  public IntComparator rowComparator() {
    return rowComparator;
  }

  @Override
  public boolean isEmpty() {
    return lookupTable.size() == 0;
  }

  @Override
  public Selection isEqualTo(String string) {
    return lookupTable.isEqualTo(string);
  }

  @Override
  public Selection isNotEqualTo(String string) {
    return lookupTable.isNotEqualTo(string);
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
    return lookupTable.getDummies();
  }

  /**
   * Returns a new Column containing all the unique values in this column
   *
   * @return a column with unique values.
   */
  @Override
  public StringColumn unique() {
    List<String> strings = new ArrayList<>(lookupTable.asSet());
    return StringColumn.create(name() + " Unique values", strings);
  }

  public DoubleColumn asDoubleColumn() {
    return DoubleColumn.create(this.name(), asDoubleArray());
  }

  public StringColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  @Override
  public StringColumn copy() {
    StringColumn newCol = create(name(), size());
    int r = 0;
    for (String string : this) {
      newCol.set(r, string);
      r++;
    }
    return newCol;
  }

  @Override
  public StringColumn append(Column<String> column) {
    Preconditions.checkArgument(column.type() == this.type());
    StringColumn source = (StringColumn) column;
    final int size = source.size();
    for (int i = 0; i < size; i++) {
      append(source.getString(i));
    }
    return this;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    return lookupTable.countMissing();
  }

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

  @Override
  public Iterator<String> iterator() {
    return lookupTable.iterator();
  }

  public Set<String> asSet() {
    return lookupTable.asSet();
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    return lookupTable.asBytes(rowNumber);
  }

  public double getDouble(int i) {
    return (double)
            lookupTable.uniqueValuesAt(lookupTable.firstIndexOf(lookupTable.getValueForIndex(i)))
        - 1;
  }

  public double[] asDoubleArray() {
    return Arrays.stream(lookupTable.asIntArray()).asDoubleStream().toArray();
  }

  /** Added for naming consistency with all other columns */
  public StringColumn append(String value) {
    try {
      lookupTable.append(value);
    } catch (NoKeysAvailableException ex) {
      lookupTable = lookupTable.promoteYourself();
      try {
        lookupTable.append(value);
      } catch (NoKeysAvailableException e) {
        // this can't happen
        throw new IllegalStateException(e);
      }
    }
    return this;
  }

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

  @Override
  public Selection isIn(String... strings) {
    return lookupTable.selectIsIn(strings);
  }

  @Override
  public Selection isIn(Collection<String> strings) {
    return lookupTable.selectIsIn(strings);
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
    return lookupTable.firstIndexOf(value);
  }

  public int countOccurrences(String value) {
    return lookupTable.countOccurrences(value);
  }

  @Override
  public String[] asObjectArray() {
    return lookupTable.asObjectArray();
  }

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

  /** For tablesaw internal use only */
  public LookupTableWrapper getLookupTable() {
    return new LookupTableWrapper(lookupTable);
  }
}
