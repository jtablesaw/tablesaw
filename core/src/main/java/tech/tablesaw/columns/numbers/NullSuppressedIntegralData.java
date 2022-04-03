package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.roaringbitmap.RoaringBitmap;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.selection.Selection;

public class NullSuppressedIntegralData implements IntegralData {

  private final RoaringBitmap nullMap;

  private final IntArrayList data;

  public NullSuppressedIntegralData(IntArrayList data, RoaringBitmap nullMap) {
    this.data = data;
    this.nullMap = nullMap;
  }

  @Override
  public int size() {
    return data.size() + nullMap.getCardinality();
  }

  @Override
  public void clear() {
    data.clear();
    nullMap.clear();
  }

  @Override
  public int[] asIntArray() {
    return new int[0];
  }

  @Override
  public void append(int i) {
    if (i == IntColumnType.missingValueIndicator()) {
      nullMap.add(size());
    } else {
      data.add(i);
    }
  }

  @Override
  public void appendMissing() {
    nullMap.add();
  }

  @Override
  public IntegralData copy() {

    return new NullSuppressedIntegralData(data.clone(), nullMap.clone());
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
