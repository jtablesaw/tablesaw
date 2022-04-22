package tech.tablesaw.io.arrow;

import static org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE;
import static org.apache.arrow.vector.types.FloatingPointPrecision.SINGLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
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
import tech.tablesaw.io.RuntimeIOException;

public class ArrowWriter {

  private Schema tableSchema(Table table) {
    List<Field> fields = new ArrayList<>();
    for (Column<?> column : table.columns()) {
      final String typeName = column.type().name();
      switch (typeName) {
        case "STRING":
          fields.add(new Field(column.name(), FieldType.nullable(new ArrowType.Utf8()), null));
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
                  FieldType.notNullable(new ArrowType.Timestamp(TimeUnit.MILLISECOND, null)),
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
                  FieldType.notNullable(new ArrowType.Timestamp(TimeUnit.MILLISECOND, "UTC")),
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

  private void setBytes(VectorSchemaRoot schemaRoot, String columnName, ColumnType type, Row row) {
    final String typeName = type.name();
    switch (typeName) {
      case "STRING":
        ((VarCharVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), row.getString(columnName).getBytes());
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
        ((TimeStampMilliVector) schemaRoot.getVector(columnName))
            .setSafe(
                row.getRowNumber(),
                row.getDateTime(columnName).toInstant(ZoneOffset.UTC).toEpochMilli());
        break;
      case "TIME":
        ((TimeMilliVector) schemaRoot.getVector(columnName))
            .setSafe(row.getRowNumber(), (int) (row.getTime(columnName).toNanoOfDay()) / 1_000_000);
        break;
      case "INSTANT":
        ((TimeStampMilliTZVector) schemaRoot.getVector(columnName))
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

  /**
   * Writes table to arrow-formatted file
   *
   * <p>The arrow format specifies writing tables in record batches, along with any
   * DictionaryProviders that will be used in encoding the data.
   *
   * <p>The process for writing record batches is as follows: - create a VectorSchemaRoot (VSR) -
   * populate the vectors in the VSR with the first batch of rows from the table, using some
   * arbitrary number of rows for the batch size - write the batch to the output stream - reset the
   * vectors in the VSR - repopulate the vectors the next batch
   *
   * <p>The cycle of reset, repopulate, and write is continued until all the data has been written
   *
   * @param table The table to write
   * @param file The file we're writing to
   */
  public void write(Table table, File file) {

    // Create an RootAllocator to allocate memory for our vectors
    BufferAllocator allocator = new RootAllocator();

    // How many records to write in one batch
    // for a first pass, we'll write them all
    final int batchSize = table.rowCount();

    Schema schema = tableSchema(table);

    List<FieldVector> fieldVectors = createFieldVectors(schema, allocator);

    VectorSchemaRoot schemaRoot = new VectorSchemaRoot(fieldVectors);

    try (FileOutputStream out = new FileOutputStream(file);
        ArrowFileWriter writer =
            new ArrowFileWriter(
                schemaRoot, /*DictionaryProvider=*/ null, Channels.newChannel(out))) {
      for (Row row : table) {
        for (Column<?> column : table.columns()) {
          setBytes(schemaRoot, column.name(), column.type(), row);
        }
        writer.writeBatch();
        writer.end();
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  private List<FieldVector> createFieldVectors(Schema schema, BufferAllocator allocator) {
    return schema.getFields().stream()
        .map(field -> field.createVector(allocator))
        .collect(Collectors.toList());
  }
}
