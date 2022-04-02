package tech.tablesaw.io.arrow;

import static org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE;
import static org.apache.arrow.vector.types.FloatingPointPrecision.SINGLE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.types.DateUnit;
import org.apache.arrow.vector.types.TimeUnit;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

public class ArrowWriter implements DataWriter<ArrowWriteOptions> {

  private static final ArrowWriter INSTANCE = new ArrowWriter();

  private static final int CHUNK_SIZE = 20_000;

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerExtension("arrow", INSTANCE);
    registry.registerOptions(ArrowWriteOptions.class, INSTANCE);
  }

  private Schema tableSchema(Table table) {
    List<Field> fields = new ArrayList<>();
    for (Column<?> column : table.columns()) {
      final String typeName = column.type().name();
      switch (typeName) {
        case "STRING":
          fields.add(new Field(column.name(), FieldType.nullable(new ArrowType.Utf8()), null));
          break;
        case "TEXT":
          fields.add(new Field(column.name(), FieldType.nullable(new ArrowType.LargeUtf8()), null));
          break;
        case "LONG":
          fields.add(
              new Field(column.name(), FieldType.nullable(new ArrowType.Int(64, false)), null));
          break;
        case "INTEGER":
          fields.add(
              new Field(column.name(), FieldType.nullable(new ArrowType.Int(32, false)), null));
          break;
        case "SHORT":
          fields.add(
              new Field(column.name(), FieldType.nullable(new ArrowType.Int(16, false)), null));
          break;
        case "DATE":
          fields.add(
              new Field(
                  column.name(), FieldType.notNullable(new ArrowType.Date(DateUnit.DAY)), null));
          break;
        case "DATE_TIME":
          fields.add(
              new Field(
                  column.name(),
                  FieldType.notNullable(new ArrowType.Date(DateUnit.MILLISECOND)),
                  null));
          break;
        case "TIME":
          fields.add(
              new Field(
                  column.name(),
                  FieldType.notNullable(new ArrowType.Time(TimeUnit.MILLISECOND, 32)),
                  null));
          break;
        case "INSTANT":
          fields.add(
              new Field(
                  column.name(),
                  FieldType.notNullable(new ArrowType.Time(TimeUnit.MILLISECOND, 64)),
                  null));
          break;
        case "BOOLEAN":
          fields.add(
              new Field(column.name(), FieldType.notNullable(new ArrowType.Int(16, false)), null));
          break;
        case "FLOAT":
          fields.add(
              new Field(
                  column.name(), FieldType.notNullable(new ArrowType.FloatingPoint(SINGLE)), null));
          break;
        case "DOUBLE":
          fields.add(
              new Field(
                  column.name(), FieldType.notNullable(new ArrowType.FloatingPoint(DOUBLE)), null));
          break;
      }
    }
    return new Schema(fields);
  }

  private List<FieldVector> vectorizeTable(Row row, VectorSchemaRoot schemaRoot) {
    List<String> columnNames = row.columnNames();
    for (String colName : columnNames) {
      ColumnType type = row.getColumnType(colName);
      setBytes(schemaRoot, colName, type, row);
    }
    return new ArrayList<>();
  }

  private void setBytes(VectorSchemaRoot schemaRoot, String columnName, ColumnType type, Row row) {
    final String typeName = type.name();
    switch (typeName) {
      case "STRING":
        ((VarCharVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getString(columnName).getBytes());
        break;
      case "TEXT":
        ((VarCharVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getText(columnName).getBytes());
        break;
      case "LONG":
        ((UInt8Vector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getLong(columnName));
        break;
      case "INTEGER":
        ((UInt4Vector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getInt(columnName));
        break;
      case "SHORT":
        ((UInt2Vector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getShort(columnName));
        break;
      case "DATE":
        ((DateDayVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), (int) row.getDate(columnName).toEpochDay());
        break;
      case "DATE_TIME":
        ((TimeStampNanoVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getDateTime(columnName).getNano());
        break;
      case "TIME":
        ((TimeMilliVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), (int) (row.getTime(columnName).toNanoOfDay()) / 1_000_000);
        break;
      case "INSTANT":
        ((TimeStampMilliVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getInstant(columnName).toEpochMilli());
        break;
      case "BOOLEAN":
        ((BitVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getBooleanAsByte(columnName));
        break;
      case "FLOAT":
        ((Float4Vector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getFloat(columnName));
        break;
      case "DOUBLE":
        ((Float8Vector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getDouble(columnName));
        break;
    }
  }

  @Override
  public void write(Table table, Destination dest) {

    // new ChunkedWriter<>(CHUNK_SIZE, this::vectorizeTable).write(new File("people.arrow"), table);
    new ChunkedWriter<>(CHUNK_SIZE).write(new File("people.arrow"), table, schema);
  }

  // private <T> void vectorizeTable(T t, int i, VectorSchemaRoot vectorSchemaRoot) {}

  @Override
  public void write(Table table, ArrowWriteOptions options) {}
}
