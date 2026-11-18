package tech.tablesaw.api;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.StringListColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** Tablesaw column storing nullable list-of-string values without serializing them. */
public final class StringListColumn extends AbstractColumn<StringListColumn, List<String>> {
  private static final String VALUE_SEPARATOR = "\u001f";
  private final List<List<String>> data;

  private StringListColumn(String name, int initialSize) {
    super(StringListColumnType.instance(), name, new Parser(StringListColumnType.instance()));
    this.data = new ArrayList<>(initialSize);
    for (int i = 0; i < initialSize; i++) {
      data.add(null);
    }
  }

  public static StringListColumn create(String name) {
    return new StringListColumn(name, 0);
  }

  public static StringListColumn create(String name, List<List<String>> values) {
    StringListColumn column = create(name);
    values.forEach(column::append);
    return column;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public int countMissing() {
    int count = 0;
    for (List<String> value : data) {
      if (value == null) {
        count++;
      }
    }
    return count;
  }

  @Override
  public List<String> get(int row) {
    List<String> value = data.get(row);
    return value == null ? null : copyValue(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String>[] asObjectArray() {
    return data.toArray(new List[0]);
  }

  @Override
  public String getString(int row) {
    List<String> value = data.get(row);
    return value == null ? "" : String.join(VALUE_SEPARATOR, value);
  }

  @Override
  public String getUnformattedString(int row) {
    return getString(row);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public StringListColumn append(List<String> value) {
    data.add(value == null ? null : copyValue(value));
    return this;
  }

  @Override
  public StringListColumn append(Column<List<String>> column) {
    for (List<String> value : column) {
      append(value);
    }
    return this;
  }

  @Override
  public StringListColumn append(Column<List<String>> column, int row) {
    return append(column.get(row));
  }

  @Override
  public StringListColumn appendObj(Object value) {
    if (value == null) {
      return appendMissing();
    }
    if (!(value instanceof List<?>)) {
      throw new IllegalArgumentException("Expected List<String>, got " + value.getClass());
    }
    List<?> values = (List<?>) value;
    List<String> strings = new ArrayList<>(values.size());
    for (Object item : values) {
      if (!(item instanceof String)) {
        throw new IllegalArgumentException("Expected List<String> element, got " + item);
      }
      strings.add((String) item);
    }
    return append(strings);
  }

  @Override
  public StringListColumn appendCell(String value) {
    return append(parser().parse(value));
  }

  @Override
  @SuppressWarnings("unchecked")
  public StringListColumn appendCell(String value, AbstractColumnParser<?> parser) {
    return append((List<String>) parser.parse(value));
  }

  @Override
  public StringListColumn appendMissing() {
    return append((List<String>) null);
  }

  @Override
  public StringListColumn set(int row, List<String> value) {
    data.set(row, value == null ? null : copyValue(value));
    return this;
  }

  @Override
  public Iterator<List<String>> iterator() {
    return data.stream().map(value -> value == null ? null : copyValue(value)).iterator();
  }

  @Override
  public StringListColumn set(int row, Column<List<String>> column, int sourceRow) {
    return set(row, column.get(sourceRow));
  }

  @Override
  public StringListColumn setMissing(int row) {
    return set(row, null);
  }

  @Override
  public boolean isMissing(int row) {
    return data.get(row) == null;
  }

  @Override
  public Selection isMissing() {
    Selection selection = new BitmapBackedSelection();
    for (int row = 0; row < size(); row++) {
      if (isMissing(row)) {
        selection.add(row);
      }
    }
    return selection;
  }

  @Override
  public Selection isNotMissing() {
    Selection selection = new BitmapBackedSelection();
    for (int row = 0; row < size(); row++) {
      if (!isMissing(row)) {
        selection.add(row);
      }
    }
    return selection;
  }

  @Override
  public StringListColumn where(Selection selection) {
    StringListColumn result = create(name());
    for (int row : selection) {
      result.append(data.get(row));
    }
    return result;
  }

  @Override
  public StringListColumn removeMissing() {
    StringListColumn result = create(name());
    for (List<String> value : data) {
      if (value != null) {
        result.append(value);
      }
    }
    return result;
  }

  @Override
  public StringListColumn unique() {
    StringListColumn result = create(name());
    Set<List<String>> unique = new LinkedHashSet<>();
    for (List<String> value : data) {
      if (unique.add(value)) {
        result.append(value);
      }
    }
    return result;
  }

  @Override
  public StringListColumn emptyCopy() {
    return create(name());
  }

  @Override
  public StringListColumn emptyCopy(int rowCount) {
    return new StringListColumn(name(), rowCount);
  }

  @Override
  public StringListColumn copy() {
    return create(name(), data);
  }

  @Override
  public StringListColumn lag(int n) {
    StringListColumn result = create(name());
    for (int row = 0; row < size(); row++) {
      int source = row - n;
      result.append(source < 0 || source >= size() ? null : data.get(source));
    }
    return result;
  }

  @Override
  public void sortAscending() {
    data.sort(StringListColumn::compareValues);
  }

  @Override
  public void sortDescending() {
    data.sort(StringListColumn::compareValues);
    java.util.Collections.reverse(data);
  }

  @Override
  public IntComparator rowComparator() {
    return (left, right) -> compareValues(data.get(left), data.get(right));
  }

  @Override
  public int compare(List<String> left, List<String> right) {
    return compareValues(left, right);
  }

  @Override
  public int valueHash(int row) {
    return Objects.hashCode(data.get(row));
  }

  @Override
  public boolean equals(int left, int right) {
    return Objects.equals(data.get(left), data.get(right));
  }

  @Override
  public int byteSize() {
    return -1;
  }

  @Override
  public byte[] asBytes(int row) {
    return getString(row).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public Set<List<String>> asSet() {
    return new LinkedHashSet<>(data);
  }

  @Override
  public int indexOf(Object value) {
    return data.indexOf(value);
  }

  @Override
  public int lastIndexOf(Object value) {
    return data.lastIndexOf(value);
  }

  @Override
  public StringColumn asStringColumn() {
    StringColumn result = StringColumn.create(name());
    for (int row = 0; row < size(); row++) {
      result.append(isMissing(row) ? null : getString(row));
    }
    return result;
  }

  @Override
  public Table summary() {
    return Table.create(name(), asStringColumn());
  }

  private static int compareValues(List<String> left, List<String> right) {
    if (left == null) {
      return 1;
    }
    if (right == null) {
      return -1;
    }
    int size = Math.min(left.size(), right.size());
    for (int i = 0; i < size; i++) {
      String leftValue = left.get(i);
      String rightValue = right.get(i);
      int comparison;
      if (leftValue == null && rightValue == null) {
        comparison = 0;
      } else if (leftValue == null) {
        comparison = 1;
      } else if (rightValue == null) {
        comparison = -1;
      } else {
        comparison = leftValue.compareTo(rightValue);
      }
      if (comparison != 0) {
        return comparison;
      }
    }
    return Integer.compare(left.size(), right.size());
  }

  private static List<String> copyValue(List<String> value) {
    return Collections.unmodifiableList(new ArrayList<>(value));
  }

  public static final class Parser extends AbstractColumnParser<List<String>> {
    public Parser(tech.tablesaw.api.ColumnType type) {
      super(type);
    }

    @Override
    public boolean canParse(String value) {
      return value != null;
    }

    @Override
    public List<String> parse(String value) {
      return value == null || value.isEmpty()
          ? List.of()
          : List.of(value.split(VALUE_SEPARATOR, -1));
    }
  }
}
