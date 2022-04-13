package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.SkipColumnType;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.io.ReadOptions;

/**
 * Specifies the type of data held by a column and a small number of methods specialized for each
 * type
 */
public interface ColumnType {

  final Map<String, ColumnType> values = new HashMap<>();

  // standard column types
  ShortColumnType SHORT = ShortColumnType.instance();
  IntColumnType INTEGER = IntColumnType.instance();
  LongColumnType LONG = LongColumnType.instance();
  FloatColumnType FLOAT = FloatColumnType.instance();
  BooleanColumnType BOOLEAN = BooleanColumnType.instance();
  StringColumnType STRING = StringColumnType.instance();
  DoubleColumnType DOUBLE = DoubleColumnType.instance();
  DateColumnType LOCAL_DATE = DateColumnType.instance();
  TimeColumnType LOCAL_TIME = TimeColumnType.instance();
  DateTimeColumnType LOCAL_DATE_TIME = DateTimeColumnType.instance();
  InstantColumnType INSTANT = InstantColumnType.instance();
  SkipColumnType SKIP = SkipColumnType.instance();

  /** Registers the given ColumnType, identifying it as supported */
  static void register(ColumnType type) {
    values.put(type.name(), type);
  }

  /** Returns an array containing all supported ColumnTypes */
  static ColumnType[] values() {
    return values.values().toArray(new ColumnType[0]);
  }

  /**
   * Returns the columnType named by the argument
   *
   * @param name a valid column type name
   * @return the ColumnType with that name
   */
  static ColumnType valueOf(String name) {
    Preconditions.checkNotNull(name);

    ColumnType result = values.get(name);
    if (result == null) {
      throw new IllegalArgumentException(name + " is not a registered column type.");
    }
    return result;
  }

  /** Returns a column of this type with the given name */
  Column<?> create(String name);

  /** Returns the name of this ColumnType */
  String name();

  /** Returns the size in bytes of a single element held in columns of this type */
  int byteSize();

  /** Returns a printer-friendly version of this ColumnType's name */
  String getPrinterFriendlyName();

  /** TODO: Research this method to provide a good comment */
  AbstractColumnParser<?> customParser(ReadOptions options);

  /** TODO: Research this method to provide a good comment */
  default boolean compare(int rowNumber, Column<?> temp, Column<?> original) {
    Object o1 = original.get(rowNumber);
    Object o2 = temp.get(temp.size() - 1);
    return o1 == null ? o2 == null : o1.equals(o2);
  }

  /**
   * Returns true if the value at the specified index in column1 is equal to the value at the
   * specified index in column 2
   */
  default boolean compare(int col1Row, Column<?> col1, int col2Row, Column<?> col2) {
    Object o1 = col1.get(col1Row);
    Object o2 = col2.get(col2Row);
    return o1 == null ? o2 == null : o1.equals(o2);
  }
}
