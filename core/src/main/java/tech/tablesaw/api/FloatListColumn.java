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
import tech.tablesaw.columns.numbers.FloatListColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** Tablesaw column storing nullable list-of-float values without serializing them. */
public final class FloatListColumn extends AbstractColumn<FloatListColumn, List<Float>> {
  private static final String VALUE_SEPARATOR = "\u001f";
  private final List<List<Float>> data;

  private FloatListColumn(String name, int initialSize) {
    super(FloatListColumnType.instance(), name, new Parser(FloatListColumnType.instance()));
    this.data = new ArrayList<>(initialSize);
    for (int i = 0; i < initialSize; i++) {
      data.add(null);
    }
  }

  public static FloatListColumn create(String name) {
    return new FloatListColumn(name, 0);
  }

  public static FloatListColumn create(String name, List<List<Float>> values) {
    FloatListColumn column = create(name);
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
    for (List<Float> value : data) {
      if (value == null) {
        count++;
      }
    }
    return count;
  }

  @Override
  public List<Float> get(int row) {
    List<Float> value = data.get(row);
    return value == null ? null : copyValue(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Float>[] asObjectArray() {
    return data.toArray(new List[0]);
  }

  @Override
  public String getString(int row) {
    List<Float> value = data.get(row);
    return value == null ? "" : join(value);
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
  public Iterator<List<Float>> iterator() {
    return data.stream().map(value -> value == null ? null : copyValue(value)).iterator();
  }

  @Override
  public FloatListColumn append(List<Float> value) {
    data.add(value == null ? null : copyValue(value));
    return this;
  }

  @Override
  public FloatListColumn append(Column<List<Float>> column) {
    for (List<Float> value : column) {
      append(value);
    }
    return this;
  }

  @Override
  public FloatListColumn append(Column<List<Float>> column, int row) {
    return append(column.get(row));
  }

  @Override
  public FloatListColumn appendObj(Object value) {
    if (value == null) {
      return appendMissing();
    }
    if (!(value instanceof List<?>)) {
      throw new IllegalArgumentException("Expected List<Float>, got " + value.getClass());
    }
    List<?> values = (List<?>) value;
    List<Float> floats = new ArrayList<>(values.size());
    for (Object item : values) {
      if (!(item instanceof Number)) {
        throw new IllegalArgumentException("Expected List<Float> element, got " + item);
      }
      floats.add(((Number) item).floatValue());
    }
    return append(floats);
  }

  @Override
  public FloatListColumn appendCell(String value) {
    return append(parser().parse(value));
  }

  @Override
  @SuppressWarnings("unchecked")
  public FloatListColumn appendCell(String value, AbstractColumnParser<?> parser) {
    return append((List<Float>) parser.parse(value));
  }

  @Override
  public FloatListColumn appendMissing() {
    return append((List<Float>) null);
  }

  @Override
  public FloatListColumn set(int row, List<Float> value) {
    data.set(row, value == null ? null : copyValue(value));
    return this;
  }

  @Override
  public FloatListColumn set(int row, Column<List<Float>> column, int sourceRow) {
    return set(row, column.get(sourceRow));
  }

  @Override
  public FloatListColumn setMissing(int row) {
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
  public FloatListColumn where(Selection selection) {
    FloatListColumn result = create(name());
    for (int row : selection) {
      result.append(data.get(row));
    }
    return result;
  }

  @Override
  public FloatListColumn removeMissing() {
    FloatListColumn result = create(name());
    for (List<Float> value : data) {
      if (value != null) {
        result.append(value);
      }
    }
    return result;
  }

  @Override
  public FloatListColumn unique() {
    FloatListColumn result = create(name());
    Set<List<Float>> unique = new LinkedHashSet<>();
    for (List<Float> value : data) {
      if (unique.add(value)) {
        result.append(value);
      }
    }
    return result;
  }

  @Override
  public FloatListColumn emptyCopy() {
    return create(name());
  }

  @Override
  public FloatListColumn emptyCopy(int rowCount) {
    return new FloatListColumn(name(), rowCount);
  }

  @Override
  public FloatListColumn copy() {
    return create(name(), data);
  }

  @Override
  public FloatListColumn lag(int n) {
    FloatListColumn result = create(name());
    for (int row = 0; row < size(); row++) {
      int source = row - n;
      result.append(source < 0 || source >= size() ? null : data.get(source));
    }
    return result;
  }

  @Override
  public void sortAscending() {
    data.sort(FloatListColumn::compareValues);
  }

  @Override
  public void sortDescending() {
    data.sort(FloatListColumn::compareValues);
    Collections.reverse(data);
  }

  @Override
  public IntComparator rowComparator() {
    return (left, right) -> compareValues(data.get(left), data.get(right));
  }

  @Override
  public int compare(List<Float> left, List<Float> right) {
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
  public Set<List<Float>> asSet() {
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

  private static int compareValues(List<Float> left, List<Float> right) {
    if (left == null) {
      return right == null ? 0 : 1;
    }
    if (right == null) {
      return -1;
    }
    int size = Math.min(left.size(), right.size());
    for (int i = 0; i < size; i++) {
      Float leftValue = left.get(i);
      Float rightValue = right.get(i);
      if (leftValue == null && rightValue == null) {
        continue;
      }
      if (leftValue == null) {
        return 1;
      }
      if (rightValue == null) {
        return -1;
      }
      int comparison = Float.compare(leftValue, rightValue);
      if (comparison != 0) {
        return comparison;
      }
    }
    return Integer.compare(left.size(), right.size());
  }

  private static List<Float> copyValue(List<Float> value) {
    return Collections.unmodifiableList(new ArrayList<>(value));
  }

  private static String join(List<Float> value) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < value.size(); i++) {
      if (i > 0) {
        result.append(VALUE_SEPARATOR);
      }
      result.append(value.get(i));
    }
    return result.toString();
  }

  public static final class Parser extends AbstractColumnParser<List<Float>> {
    public Parser(tech.tablesaw.api.ColumnType type) {
      super(type);
    }

    @Override
    public boolean canParse(String value) {
      return value != null;
    }

    @Override
    public List<Float> parse(String value) {
      if (value == null || value.isEmpty()) {
        return List.of();
      }
      List<Float> result = new ArrayList<>();
      for (String part : value.split(VALUE_SEPARATOR, -1)) {
        result.add(Float.valueOf(part));
      }
      return result;
    }
  }
}
