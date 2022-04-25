package tech.tablesaw.columns.booleans;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteIterable;
import tech.tablesaw.selection.Selection;

/** A container for boolean data */
public interface BooleanData extends ByteIterable {

  /** Returns the number of values in the data, including missing values */
  int size();

  /** Adds a boolean value represented as a byte, where 0=false, 1=true, and -1=missing */
  void add(byte booleanValue);

  /** Removes all data values */
  void clear();

  /** Returns a copy of this BooleanData object */
  BooleanData copy();

  /** Returns the value at i as a byte, where -1 represents missing values */
  byte getByte(int i);

  /** Returns the number of false values in the data * */
  int countFalse();

  /** Returns the number of true values in the data * */
  int countTrue();

  /** Returns the number of missing values in the data * */
  int countMissing();

  /**
   * Returns the number of unique values in the data. There can only be 3 (true, false, and missing)
   * *
   */
  int countUnique();

  /** Sorts the data in-place in ascending order, with missing values first */
  void sortAscending();

  /** Sorts the data in-place in descending order, with missing values last */
  void sortDescending();

  /** Returns the data as a byte[] containing 0 and 1, with any missing values encoded as -128 */
  byte[] toByteArray();

  /**
   * Returns the data as a ByteArrayList containing 0 and 1, with any missing values encoded as -128
   */
  ByteArrayList toByteArrayList();

  /**
   * Sets the value at position i to byte b
   *
   * @param i the 0-based index of the element in the data
   * @param b the value to set, should be 0, 1, or -128 only
   */
  void set(int i, byte b);

  /**
   * Returns true if the data is empty, and false otherwise. Empty here means only missing values
   */
  boolean isEmpty();

  /** Returns true if the value at b is true. */
  boolean contains(byte b);

  /** Returns a selection matching all the true values in the data */
  Selection asSelection();

  /** Returns a selection matching all the false values in the data */
  Selection isFalse();

  /** Returns a selection matching all the true values in the data */
  Selection isTrue();

  /** Returns a selection matching all the missing values in the data */
  Selection isMissing();

  /**
   * Returns a byte representation of the true values, encoded in the format specified in {@link
   * java.util.BitSet#toByteArray()}
   */
  byte[] falseBytes();

  /**
   * Returns a byte representation of the false values, encoded in the format specified in {@link
   * java.util.BitSet#toByteArray()}
   */
  byte[] trueBytes();

  /**
   * Returns a byte representation of the missing values, encoded in the format specified in {@link
   * java.util.BitSet#toByteArray()}
   */
  byte[] missingBytes();

  /**
   * Sets the true values in the data from a byte[] encoding
   *
   * @param bytes The true values encoded in the format specified in {@link java.util.BitSet}
   */
  void setTrueBytes(byte[] bytes);

  /**
   * Sets the false values in the data from a byte[] encoding
   *
   * @param bytes The false values encoded in the format specified in {@link java.util.BitSet}
   */
  void setFalseBytes(byte[] bytes);

  /**
   * Sets the missing values in the data from a byte[] encoding
   *
   * @param bytes The missing values encoded in the format specified in {@link java.util.BitSet}
   */
  void setMissingBytes(byte[] bytes);
}
