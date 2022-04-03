package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.selection.Selection;

public interface IntegralData {

  int size();

  void clear();

  int[] asIntArray();

  void append(int i);

  void appendMissing();

  IntegralData copy();

  int countUnique();

  int getInt(int row);

  boolean isMissing(int row);

  void sortAscending();

  void sortDescending();

  Selection isIn(final int... numbers);

  void set(int i, int value);

  void setMissing(int row);

  /** TODO: Genericize the return type */
  IntColumn removeMissing();
}
