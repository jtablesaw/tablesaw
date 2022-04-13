package tech.tablesaw.conversion.smile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataType;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

/**
 * A tool that con be used to convert a Relation to a format suitable for use with the Smile machine
 * learning library.
 */
public class SmileConverter {

  private final Relation table;

  public SmileConverter(Relation table) {
    this.table = table;
  }

  public DataFrame toDataFrame() {
    StructType schema =
        DataTypes.struct(
            table.columns().stream()
                .map(col -> new StructField(col.name(), toSmileType(col.type())))
                .collect(Collectors.toList()));
    return toDataFrame(schema);
  }

  public DataFrame toDataFrame(StructType schema) {
    List<Tuple> rows = new ArrayList<>();
    int colCount = table.columnCount();
    for (int rowIndex = 0; rowIndex < table.rowCount(); rowIndex++) {
      Object[] row = new Object[colCount];
      for (int colIndex = 0; colIndex < colCount; colIndex++) {
        Column<?> col = table.column(colIndex);
        if (!col.isMissing(rowIndex)) {
          row[colIndex] =
              col.type().equals(ColumnType.INSTANT)
                  ? LocalDateTime.ofInstant(((InstantColumn) col).get(rowIndex), ZoneOffset.UTC)
                  : col.get(rowIndex);
        }
      }
      rows.add(Tuple.of(row, schema));
    }

    return DataFrame.of(rows, schema.boxed(rows));
  }

  private DataType toSmileType(ColumnType type) {
    if (type.equals(ColumnType.BOOLEAN)) {
      return DataTypes.BooleanType;
    } else if (type.equals(ColumnType.DOUBLE)) {
      return DataTypes.DoubleType;
    } else if (type.equals(ColumnType.FLOAT)) {
      return DataTypes.FloatType;
    } else if (type.equals(ColumnType.INSTANT)) {
      return DataTypes.DateTimeType;
    } else if (type.equals(ColumnType.INTEGER)) {
      return DataTypes.IntegerType;
    } else if (type.equals(ColumnType.LOCAL_DATE)) {
      return DataTypes.DateType;
    } else if (type.equals(ColumnType.LOCAL_DATE_TIME)) {
      return DataTypes.DateTimeType;
    } else if (type.equals(ColumnType.LOCAL_TIME)) {
      return DataTypes.TimeType;
    } else if (type.equals(ColumnType.LONG)) {
      return DataTypes.LongType;
    } else if (type.equals(ColumnType.SHORT)) {
      return DataTypes.ShortType;
    } else if (type.equals(ColumnType.STRING)) {
      return DataTypes.StringType;
    }
    throw new IllegalStateException("Unsupported column type " + type);
  }
}
