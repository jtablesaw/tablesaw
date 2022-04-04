package tech.tablesaw.columns.booleans;

import it.unimi.dsi.fastutil.bytes.*;
import java.util.function.IntConsumer;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

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
  public Selection asSelection() {
    Selection selection = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      byte value = getByte(i);
      if (value == 1) {
        selection.add(i);
      }
    }
    return selection;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isFalse() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (byte next : data) {
      if (next == BooleanColumnType.BYTE_FALSE) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isTrue() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (byte next : data) {
      if (next == BooleanColumnType.BYTE_FALSE) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isMissing() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (byte next : data) {
      if (next == BooleanColumnType.BYTE_FALSE) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public byte[] falseBytes() {
    return new byte[0];
  }

  @Override
  public byte[] trueBytes() {
    return new byte[0];
  }

  @Override
  public byte[] missingBytes() {
    return new byte[0];
  }

  @Override
  public void setTrueBytes(byte[] bytes) {}

  @Override
  public void setFalseBytes(byte[] bytes) {}

  @Override
  public void setMissingBytes(byte[] bytes) {}

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
