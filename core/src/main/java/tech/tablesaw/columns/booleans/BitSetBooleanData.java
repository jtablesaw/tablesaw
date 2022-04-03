package tech.tablesaw.columns.booleans;

import static tech.tablesaw.columns.booleans.BooleanColumnType.*;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.BitSet;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * An implementation of BooleanData where the underlying representation uses the Java BitSet class
 */
public class BitSetBooleanData implements BooleanData {

  private BitSet trueValues = new BitSet();
  private BitSet falseValues = new BitSet();
  private BitSet missingValues = new BitSet();

  public BitSetBooleanData(BitSet trueValues, BitSet falseValues, BitSet missingValues) {
    this.trueValues = trueValues;
    this.falseValues = falseValues;
    this.missingValues = missingValues;
  }

  public BitSetBooleanData(ByteArrayList values) {
    for (int i = 0; i < values.size(); i++) {
      if (i == BYTE_TRUE) trueValues.set(i);
      else if (i == BYTE_FALSE) falseValues.set(i);
      else if (i == MISSING_VALUE) missingValues.set(i);
    }
  }

  public BitSetBooleanData() {}

  @Override
  public int size() {
    return Math.max(Math.max(trueValues.length(), falseValues.length()), missingValues.length());
  }

  @Override
  public void add(byte b) {
    int size = size();
    if (b == BYTE_TRUE) {
      trueValues.set(size);
    } else if (b == BooleanColumnType.BYTE_FALSE) {
      falseValues.set(size);
    } else if (b == BooleanColumnType.MISSING_VALUE) {
      missingValues.set(size);
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
    int t = trueValues.cardinality();
    int f = falseValues.cardinality();
    int m = missingValues.cardinality();
    trueValues.clear();
    falseValues.clear();
    missingValues.clear();
    missingValues.set(0, m);
    falseValues.set(m, m + f);
    trueValues.set(m + f, m + f + t);
  }

  @Override
  public void sortDescending() {
    int t = trueValues.cardinality();
    int f = falseValues.cardinality();
    int m = missingValues.cardinality();
    trueValues.clear();
    falseValues.clear();
    missingValues.clear();
    trueValues.set(0, t);
    falseValues.set(t, t + f);
    missingValues.set(t + f, m + f + t);
  }

  @Override
  public BooleanData copy() {
    return new BitSetBooleanData(
        (BitSet) trueValues.clone(), (BitSet) falseValues.clone(), (BitSet) missingValues.clone());
  }

  @Override
  public byte getByte(int i) {
    if (trueValues.get(i)) return BYTE_TRUE;
    if (falseValues.get(i)) return BooleanColumnType.BYTE_FALSE;
    return BooleanColumnType.MISSING_VALUE;
  }

  @Override
  public int countFalse() {
    return falseValues.cardinality();
  }

  @Override
  public int countTrue() {
    return trueValues.cardinality();
  }

  @Override
  public int countMissing() {
    return missingValues.cardinality();
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
    /*
        System.out.println("False: " + falseValues.toString());
        byte[] encoded = falseValues.toByteArray();
        System.out.println(Arrays.toString(encoded));
        BitSet newFalseValues = BitSet.valueOf(encoded);
        System.out.println(newFalseValues);
        System.out.println("True: " + trueValues.toString());
        byte[] encoded2 = trueValues.toByteArray();
        System.out.println(Arrays.toString(encoded2));
        BitSet newTrueValues = BitSet.valueOf(encoded2);
        System.out.println(newTrueValues);
    */
    ByteArrayList arrayList = new ByteArrayList(new byte[size()]);
    for (int i = 0; i < size(); i++) {
      if (trueValues.get(i)) arrayList.set(i, BYTE_TRUE);
      else if (missingValues.get(i)) arrayList.set(i, MISSING_VALUE);
    }
    return arrayList;
  }

  @Override
  public void set(int i, byte b) {
    if (b == BYTE_TRUE) {
      trueValues.set(i);
      falseValues.clear(i);
      missingValues.clear(i);
    } else if (b == BooleanColumnType.BYTE_FALSE) {
      trueValues.clear(i);
      falseValues.set(i);
      missingValues.clear(i);
    } else if (b == BooleanColumnType.MISSING_VALUE) {
      trueValues.clear(i);
      falseValues.clear(i);
      missingValues.set(i);
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
    for (byte next : this) {
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
    for (byte next : this) {
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
    for (byte next : this) {
      if (next == BooleanColumnType.BYTE_FALSE) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + "{ size: "
        + size()
        + ", true: "
        + trueValues.cardinality()
        + ", false: "
        + falseValues.cardinality()
        + ", missing: "
        + missingValues.cardinality()
        + "}";
  }

  @Override
  public ByteIterator iterator() {
    return new BitSetByteIterator(this);
  }

  static class BitSetByteIterator implements ByteIterator {

    final BitSetBooleanData data;

    int current = -1;

    BitSetByteIterator(BitSetBooleanData data) {
      this.data = data;
    }

    @Override
    public byte nextByte() {
      current++;
      if (data.trueValues.get(current)) return BYTE_TRUE;
      if (data.falseValues.get(current)) return BYTE_FALSE;
      return MISSING_VALUE;
    }

    @Override
    public boolean hasNext() {
      return current < data.size() - 1;
    }
  }
}
