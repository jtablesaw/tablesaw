package tech.tablesaw.columns.booleans;

import static tech.tablesaw.columns.booleans.BooleanColumnType.*;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import org.roaringbitmap.RoaringBitmap;

/** An implementation of BooleanData where the underlying representation uses a RoaringBitmap */
public class BitMapBooleanData implements BooleanData {

  private RoaringBitmap trueValues = new RoaringBitmap();
  private RoaringBitmap falseValues = new RoaringBitmap();
  private RoaringBitmap missingValues = new RoaringBitmap();

  public BitMapBooleanData(
      RoaringBitmap trueValues, RoaringBitmap falseValues, RoaringBitmap missingValues) {
    this.trueValues = trueValues;
    this.falseValues = falseValues;
    this.missingValues = missingValues;
  }

  public BitMapBooleanData(ByteArrayList values) {
    for (int i = 0; i < values.size(); i++) {
      if (i == BYTE_TRUE) trueValues.add(i);
      else if (i == BYTE_FALSE) falseValues.add(i);
      else if (i == MISSING_VALUE) missingValues.add(i);
    }
  }

  public BitMapBooleanData() {}

  @Override
  public int size() {
    return trueValues.getCardinality()
        + falseValues.getCardinality()
        + missingValues.getCardinality();
  }

  @Override
  public void add(byte b) {
    int size = size();
    if (b == BYTE_TRUE) {
      trueValues.add(size);
    } else if (b == BooleanColumnType.BYTE_FALSE) {
      falseValues.add(size);
    } else if (b == BooleanColumnType.MISSING_VALUE) {
      missingValues.add(size);
    }
  }

  @Override
  public void clear() {
    trueValues.clear();
    falseValues.clear();
    missingValues.clear();
  }

  @Override
  public void sort(ByteComparator comparator) {}

  @Override
  public void sortAscending() {
    int t = trueValues.getCardinality();
    int f = falseValues.getCardinality();
    int m = missingValues.getCardinality();
    trueValues.clear();
    falseValues.clear();
    missingValues.clear();
    missingValues.add((long) 0, m);
    falseValues.add((long) m, m + f);
    trueValues.add((long) m + f, m + f + t);
  }

  @Override
  public void sortDescending() {
    int t = trueValues.getCardinality();
    int f = falseValues.getCardinality();
    int m = missingValues.getCardinality();
    trueValues.clear();
    falseValues.clear();
    missingValues.clear();
    trueValues.add(0, t);
    falseValues.add(t, t + f);
    missingValues.add(t + f, m + f + t);
  }

  @Override
  public BooleanData copy() {
    return new BitMapBooleanData(trueValues.clone(), falseValues.clone(), missingValues.clone());
  }

  @Override
  public byte getByte(int i) {
    if (trueValues.contains(i)) return BYTE_TRUE;
    if (falseValues.contains(i)) return BooleanColumnType.BYTE_FALSE;
    return BooleanColumnType.MISSING_VALUE;
  }

  @Override
  public int countFalse() {
    return falseValues.getCardinality();
  }

  @Override
  public int countTrue() {
    return trueValues.getCardinality();
  }

  @Override
  public int countMissing() {
    return missingValues.getCardinality();
  }

  @Override
  public int countUnique() {
    boolean t = !trueValues.isEmpty();
    boolean f = !falseValues.isEmpty();
    boolean m = !missingValues.isEmpty();
    if (m && f && t) return 3;
    if ((m && f) || (m && t) || (t && f)) return 2;
    if (m || f || t) return 1;
    return 0;
  }

  @Override
  public byte[] toByteArray() {
    return toByteArrayList().toByteArray();
  }

  @Override
  public ByteArrayList toByteArrayList() {
    ByteArrayList arrayList = new ByteArrayList(size());
    for (int i = 0; i < size(); i++) {
      if (trueValues.contains(i)) arrayList.set(i, BYTE_TRUE);
      if (missingValues.contains(i)) arrayList.set(i, MISSING_VALUE);
    }
    return arrayList;
  }

  @Override
  public void set(int i, byte b) {
    if (b == BYTE_TRUE) {
      trueValues.add(i);
      falseValues.remove(i);
      missingValues.remove(i);
    } else if (b == BooleanColumnType.BYTE_FALSE) {
      trueValues.remove(i);
      falseValues.add(i);
      missingValues.remove(i);
    } else if (b == BooleanColumnType.MISSING_VALUE) {
      trueValues.remove(i);
      falseValues.remove(i);
      missingValues.add(i);
    }
  }

  @Override
  public boolean isEmpty() {
    return trueValues.isEmpty() && falseValues.isEmpty();
  }

  @Override
  public boolean contains(byte b) {
    if (b == BYTE_TRUE) {
      return !trueValues.isEmpty();
    }
    if (b == BooleanColumnType.BYTE_FALSE) {
      return !falseValues.isEmpty();
    }
    if (b == BooleanColumnType.MISSING_VALUE) {
      return !missingValues.isEmpty();
    }
    throw new IllegalArgumentException(
        "The value "
            + b
            + " is not a legal byte representation of a boolean value. Only 0, 1, and -1 are valid.");
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + "{ size: "
        + size()
        + ", true: "
        + trueValues.getCardinality()
        + ", false: "
        + falseValues.getCardinality()
        + ", missing: "
        + missingValues.getCardinality()
        + "}";
  }

  @Override
  public ByteIterator iterator() {
    return new BitSetByteIterator(this);
  }

  static class BitSetByteIterator implements ByteIterator {

    final BitMapBooleanData data;

    int current = -1;

    BitSetByteIterator(BitMapBooleanData data) {
      this.data = data;
    }

    @Override
    public byte nextByte() {
      current++;
      if (data.trueValues.contains(current)) return BYTE_TRUE;
      if (data.falseValues.contains(current)) return BYTE_FALSE;
      return MISSING_VALUE;
    }

    @Override
    public boolean hasNext() {
      return current < data.size() - 1;
    }
  }
}
