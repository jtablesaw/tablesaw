package tech.tablesaw.columns.booleans;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
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

  void sort(ByteComparator comparator);

  BooleanData copy();

  /** Returns the value at i as a byte, where -1 represents missing values */
  byte getByte(int i);

  int countFalse();

  int countTrue();

  int countMissing();

  int countUnique();

  void sortAscending();

  void sortDescending();

  byte[] toByteArray();

  ByteArrayList toByteArrayList();

  void set(int i, byte b);

  boolean isEmpty();

  boolean contains(byte b);

  Selection asSelection();

  Selection isFalse();

  Selection isTrue();

  Selection isMissing();

  byte[] falseBytes();

  byte[] trueBytes();

  byte[] missingBytes();

  void setTrueBytes(byte[] bytes);

  void setFalseBytes(byte[] bytes);

  void setMissingBytes(byte[] bytes);
}
