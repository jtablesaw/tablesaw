package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.selection.Selection;

public class IntArrayIntegralData implements IntegralData {
  @Override
  public int size() {
    return 0;
  }

  @Override
  public void clear() {}

  @Override
  public int[] asIntArray() {
    return new int[0];
  }

  @Override
  public void append(int i) {}

  @Override
  public void appendMissing() {}

  @Override
  public IntegralData copy() {
    return null;
  }

  @Override
  public int countUnique() {
    return 0;
  }

  @Override
  public int getInt(int row) {
    return 0;
  }

  @Override
  public boolean isMissing(int row) {
    return false;
  }

  @Override
  public void sortAscending() {}

  @Override
  public void sortDescending() {}

  @Override
  public Selection isIn(int... numbers) {
    return null;
  }

  @Override
  public void set(int i, int value) {}

  @Override
  public void setMissing(int row) {}

  @Override
  public IntColumn removeMissing() {
    return null;
  }
}
