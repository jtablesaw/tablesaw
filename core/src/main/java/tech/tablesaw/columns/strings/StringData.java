package tech.tablesaw.columns.strings;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public interface StringData extends StringFilters, StringReduceUtils {

  StringData appendMissing();

  StringData append(String value);

  StringData emptyCopy();

  StringData emptyCopy(int rowSize);

  void sortAscending();

  void sortDescending();

  void clear();

  StringData unique();

  StringData where(Selection selection);

  StringData copy();

  StringData lead(int n);

  StringData lag(int n);

  StringData set(Selection rowSelection, String newValue);

  StringData set(int rowNumber, String value);

  boolean contains(String aString);

  StringData setMissing(int i);

  IntComparator rowComparator();

  StringData removeMissing();

  Set<String> asSet();

  byte[] asBytes(int rowNumber);

  String[] asObjectArray();

  boolean isEmpty();

  boolean isMissing(int rowNumber);

  int countUnique();

  void append(Column<String> column);

  int countMissing();

  List<String> asList();

  Table countByCategory(String columnName);

  List<BooleanColumn> getDummies();

  double getDouble(int i);

  double[] asDoubleArray();

  StringData appendObj(Object obj);

  int firstIndexOf(String value);

  int countOccurrences(String value);

  /**
   * Return a StringData of the same type containing just those elements whose indexes are included
   * in the given array
   */
  default StringData subset(int[] rows) {
    final StringData c = this.emptyCopy();
    for (final int row : rows) {
      c.appendObj(get(row));
    }
    return c;
  }

  @Nullable
  DictionaryMap getDictionary();
}
