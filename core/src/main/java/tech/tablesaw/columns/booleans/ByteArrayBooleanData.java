package tech.tablesaw.columns.booleans;

import it.unimi.dsi.fastutil.bytes.*;
import java.util.function.IntConsumer;

public class ByteArrayBooleanData implements BooleanData {

  private final ByteArrayList data;

  public ByteArrayBooleanData(ByteArrayList data) {
    this.data = data;
  }

  public ByteArrayBooleanData() {
    this.data = new ByteArrayList();
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public void add(byte booleanValue) {
    data.add(booleanValue);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public void sort(ByteComparator comparator) {
    data.sort(comparator);
  }

  @Override
  public BooleanData copy() {
    return new ByteArrayBooleanData(data.clone());
  }

  @Override
  public byte getByte(int i) {
    return data.getByte(i);
  }

  @Override
  public void sortAscending() {
    data.sort(ByteComparators.NATURAL_COMPARATOR);
  }

  @Override
  public int countFalse() {
    int count = 0;
    for (byte b : data) {
      if (b == BooleanColumnType.BYTE_FALSE) {
        count++;
      }
    }
    return count;
  }

  @Override
  public int countTrue() {
    int count = 0;
    for (byte b : data) {
      if (b == BooleanColumnType.BYTE_TRUE) {
        count++;
      }
    }
    return count;
  }

  @Override
  public int countMissing() {
    int count = 0;
    for (byte b : data) {
      if (b == BooleanColumnType.MISSING_VALUE) {
        count++;
      }
    }
    return count;
  }

  @Override
  public int countUnique() {
    ByteSet count = new ByteOpenHashSet(3);
    for (byte next : data) {
      count.add(next);
    }
    return count.size();
  }

  @Override
  public void sortDescending() {
    data.sort(ByteComparators.OPPOSITE_COMPARATOR);
  }

  @Override
  public byte[] toByteArray() {
    return data.toByteArray();
  }

  @Override
  public ByteArrayList toByteArrayList() {
    return data.clone();
  }

  @Override
  public void set(int i, byte b) {
    data.set(i, b);
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public boolean contains(byte b) {
    return data.contains(b);
  }

  @Override
  public ByteIterator iterator() {
    return data.iterator();
  }

  @Override
  public void forEach(IntConsumer action) {
    BooleanData.super.forEach(action);
  }
}
