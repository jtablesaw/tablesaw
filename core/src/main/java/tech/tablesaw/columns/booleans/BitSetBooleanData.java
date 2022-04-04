package tech.tablesaw.columns.booleans;

import static tech.tablesaw.columns.booleans.BooleanColumnType.*;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.BitSet;
import tech.tablesaw.selection.BitSetBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * An implementation of BooleanData where the underlying representation uses the Java BitSet class
 */
public class BitSetBooleanData implements BooleanData {

  /** A BitSet indicating the position of the true values in the data */
  private BitSet trueValues = new BitSet();

  /** A BitSet indicating the position of the false values in the data */
  private BitSet falseValues = new BitSet();

  /** A BitSet indicating the position of the missing values in the data */
  private BitSet missingValues = new BitSet();

  /** Constructs a BitSetBoolean data from three BitSets, one for each possible value */
  public BitSetBooleanData(BitSet trueValues, BitSet falseValues, BitSet missingValues) {
    this.trueValues = trueValues;
    this.falseValues = falseValues;
    this.missingValues = missingValues;
  }

  /**
   * Constructs a BitSetBoolean data from the given ByteArrayList
   *
   * @param values The values must be encoded as 0, 1, or -128 (for missing)
   */
  public BitSetBooleanData(ByteArrayList values) {
    for (int i = 0; i < values.size(); i++) {
      if (i == BYTE_TRUE) trueValues.set(i);
      else if (i == BYTE_FALSE) falseValues.set(i);
      else if (i == MISSING_VALUE) missingValues.set(i);
    }
  }

  public BitSetBooleanData() {}

  /** {@inheritDoc} */
  @Override
  public int size() {
    return Math.max(Math.max(trueValues.length(), falseValues.length()), missingValues.length());
  }

  /** {@inheritDoc} */
  @Override
  public void add(byte b) {
    int size = size();
    if (b == BYTE_TRUE) {
      trueValues.set(size);
    } else if (b == BYTE_FALSE) {
      falseValues.set(size);
    } else if (b == MISSING_VALUE) {
      missingValues.set(size);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    trueValues.clear();
    falseValues.clear();
    missingValues.clear();
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public BooleanData copy() {
    return new BitSetBooleanData(
        (BitSet) trueValues.clone(), (BitSet) falseValues.clone(), (BitSet) missingValues.clone());
  }

  /** {@inheritDoc} */
  @Override
  public byte getByte(int i) {
    if (trueValues.get(i)) return BYTE_TRUE;
    if (falseValues.get(i)) return BYTE_FALSE;
    return MISSING_VALUE;
  }

  /** {@inheritDoc} */
  @Override
  public int countFalse() {
    return falseValues.cardinality();
  }

  /** {@inheritDoc} */
  @Override
  public int countTrue() {
    return trueValues.cardinality();
  }

  /** {@inheritDoc} */
  @Override
  public int countMissing() {
    return missingValues.cardinality();
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public byte[] toByteArray() {
    return toByteArrayList().toByteArray();
  }

  @Override
  public ByteArrayList toByteArrayList() {
    ByteArrayList arrayList = new ByteArrayList(new byte[size()]);
    for (int i = 0; i < size(); i++) {
      if (trueValues.get(i)) arrayList.set(i, BYTE_TRUE);
      else if (missingValues.get(i)) arrayList.set(i, MISSING_VALUE);
    }
    return arrayList;
  }

  /** {@inheritDoc} */
  @Override
  public void set(int i, byte b) {
    if (b == BYTE_TRUE) {
      trueValues.set(i);
      falseValues.clear(i);
      missingValues.clear(i);
    } else if (b == BYTE_FALSE) {
      trueValues.clear(i);
      falseValues.set(i);
      missingValues.clear(i);
    } else if (b == MISSING_VALUE) {
      trueValues.clear(i);
      falseValues.clear(i);
      missingValues.set(i);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {
    return trueValues.isEmpty() && falseValues.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public boolean contains(byte b) {
    if (b == BYTE_TRUE) {
      return !trueValues.isEmpty();
    }
    if (b == BYTE_FALSE) {
      return !falseValues.isEmpty();
    }
    if (b == MISSING_VALUE) {
      return !missingValues.isEmpty();
    }
    throw new IllegalArgumentException(
        "The value "
            + b
            + " is not a legal byte representation of a boolean value. Only 0, 1, and -1 are valid.");
  }

  /** {@inheritDoc} */
  @Override
  public Selection asSelection() {
    return isTrue();
  }

  /** {@inheritDoc} */
  @Override
  public Selection isFalse() {
    return new BitSetBackedSelection((BitSet) falseValues.clone());
  }

  /** {@inheritDoc} */
  @Override
  public Selection isTrue() {
    return new BitSetBackedSelection((BitSet) trueValues.clone());
  }

  /** {@inheritDoc} */
  @Override
  public Selection isMissing() {
    return new BitSetBackedSelection((BitSet) missingValues.clone());
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

  /** {@inheritDoc} */
  @Override
  public byte[] falseBytes() {
    return falseValues.toByteArray();
  }

  /** {@inheritDoc} */
  @Override
  public byte[] trueBytes() {
    return trueValues.toByteArray();
  }

  /** {@inheritDoc} */
  @Override
  public byte[] missingBytes() {
    return missingValues.toByteArray();
  }

  /** {@inheritDoc} */
  @Override
  public void setTrueBytes(byte[] bytes) {
    trueValues = BitSet.valueOf(bytes);
  }

  /** {@inheritDoc} */
  @Override
  public void setFalseBytes(byte[] bytes) {
    falseValues = BitSet.valueOf(bytes);
  }

  /** {@inheritDoc} */
  @Override
  public void setMissingBytes(byte[] bytes) {
    missingValues = BitSet.valueOf(bytes);
  }

  /** {@inheritDoc} */
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
