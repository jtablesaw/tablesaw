package tech.tablesaw.io.arrow;

import static org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE;
import static org.apache.arrow.vector.types.FloatingPointPrecision.SINGLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.DateUnit;
import org.apache.arrow.vector.types.TimeUnit;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.RuntimeIOException;

/** Writer for persisting a Tablesaw table in Apache Arrow Streaming Format. */
public class ArrowWriter {

  /**
   * Returns an arrow Schema objects containing fields for each of the columns in the given Table
   */
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
              new Field(column.name(), FieldType.nullable(new ArrowType.Int(64, true)), null));
          break;
        case "INTEGER":
          fields.add(
              new Field(column.name(), FieldType.nullable(new ArrowType.Int(32, true)), null));
          break;
        case "SHORT":
          fields.add(
              new Field(column.name(), FieldType.nullable(new ArrowType.Int(16, true)), null));
          break;
        case "LOCAL_DATE":
          fields.add(
              new Field(
                  column.name(), FieldType.notNullable(new ArrowType.Date(DateUnit.DAY)), null));
          break;
        case "LOCAL_DATE_TIME":
          fields.add(
              new Field(
                  column.name(),
                  FieldType.notNullable(new ArrowType.Timestamp(TimeUnit.MILLISECOND, null)),
                  null));
          break;
        case "LOCAL_TIME":
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
              new Field(column.name(), FieldType.nullable(Types.MinorType.BIT.getType()), null));
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
        default:
          throw new IllegalArgumentException(
              "Unhandled Column type " + typeName + " in exported data");
      }
    }
    return new Schema(fields);
  }

  /**
   * Writes the data from the given column into the corresponding vector in the given
   * VectorSchemaRoot
   */
  private void setBytes(VectorSchemaRoot schemaRoot, Column<?> column) {

    final String typeName = column.type().name();
    switch (typeName) {
      case "STRING":
        VarCharVector sv = ((VarCharVector) schemaRoot.getVector(column.name()));
        StringColumn sc = (StringColumn) column;
        for (int i = 0; i < sc.size(); i++) {
          sv.setSafe(i, sc.get(i).getBytes(StandardCharsets.UTF_8));
        }
        sv.setValueCount(sc.size());
        break;
      case "LONG":
        BigIntVector lv = ((BigIntVector) schemaRoot.getVector(column.name()));
        LongColumn lc = (LongColumn) column;
        for (int i = 0; i < lc.size(); i++) {
          lv.setSafe(i, lc.getLong(i));
        }
        lv.setValueCount(lc.size());
        break;
      case "INTEGER":
        IntVector iv = ((IntVector) schemaRoot.getVector(column.name()));
        IntColumn ic = (IntColumn) column;
        for (int i = 0; i < ic.size(); i++) {
          iv.setSafe(i, ic.getInt(i));
        }
        iv.setValueCount(ic.size());
        break;
      case "SHORT":
        SmallIntVector shortv = ((SmallIntVector) schemaRoot.getVector(column.name()));
        ShortColumn shortc = (ShortColumn) column;
        for (int i = 0; i < shortc.size(); i++) {
          shortv.setSafe(i, shortc.getInt(i));
        }
        shortv.setValueCount(shortc.size());
        break;
      case "LOCAL_DATE":
        DateDayVector dv = ((DateDayVector) schemaRoot.getVector(column.name()));
        DateColumn dc = (DateColumn) column;
        for (int i = 0; i < dc.size(); i++) {
          dv.setSafe(i, (int) dc.get(i).toEpochDay());
        }
        dv.setValueCount(dc.size());
        break;

      case "LOCAL_DATE_TIME":
        TimeStampMilliVector dtv = ((TimeStampMilliVector) schemaRoot.getVector(column.name()));
        DateTimeColumn dtc = (DateTimeColumn) column;
        for (int i = 0; i < dtc.size(); i++) {
          dtv.setSafe(i, dtc.get(i).toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        dtv.setValueCount(dtc.size());
        break;
      case "LOCAL_TIME":
        TimeMilliVector tv = ((TimeMilliVector) schemaRoot.getVector(column.name()));
        TimeColumn tc = (TimeColumn) column;
        for (int i = 0; i < tc.size(); i++) {
          tv.setSafe(i, (int) ((tc.get(i).toNanoOfDay()) / 1_000_000));
        }
        tv.setValueCount(tc.size());
        break;
      case "INSTANT":
        TimeStampMilliTZVector instv =
            ((TimeStampMilliTZVector) schemaRoot.getVector(column.name()));
        InstantColumn instc = (InstantColumn) column;
        for (int i = 0; i < instc.size(); i++) {
          instv.setSafe(i, instc.get(i).toEpochMilli());
        }
        instv.setValueCount(instc.size());
        break;
      case "BOOLEAN":
        BitVector bv = ((BitVector) schemaRoot.getVector(column.name()));
        BooleanColumn bc = (BooleanColumn) column;
        for (int i = 0; i < bc.size(); i++) {
          bv.setSafe(i, bc.getByte(i));
        }
        bv.setValueCount(bc.size());
        break;
      case "FLOAT":
        Float4Vector fv = ((Float4Vector) schemaRoot.getVector(column.name()));
        FloatColumn fc = (FloatColumn) column;
        for (int i = 0; i < fc.size(); i++) {
          fv.setSafe(i, fc.getFloat(i));
        }
        fv.setValueCount(fc.size());
        break;
      case "DOUBLE":
        Float8Vector f8v = ((Float8Vector) schemaRoot.getVector(column.name()));
        DoubleColumn f8c = (DoubleColumn) column;
        for (int i = 0; i < f8c.size(); i++) {
          f8v.setSafe(i, f8c.getDouble(i));
        }
        f8v.setValueCount(f8c.size());
        break;
      default:
        throw new IllegalArgumentException(
            "Unhandled Column type " + typeName + " in exported data");
    }
  }

  /**
   * Writes table to arrow-formatted file. The Arrow Stream format is used, meaning that there is no
   * sparse index into the individual data blocks, and only sequential access is supported.
   *
   * <p>Note that for Arrow Streaming Format files, the extension ".arrows" is recommended. The
   * ".arrow" extension is intended for use by Arrow File Format, which provides random access to
   * the individual blocks
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

    Schema schema = tableSchema(table);
    List<FieldVector> fieldVectors = createFieldVectors(schema, allocator);
    VectorSchemaRoot schemaRoot = new VectorSchemaRoot(fieldVectors);

    try (FileOutputStream out = new FileOutputStream(file);
        ArrowStreamWriter writer =
            new ArrowStreamWriter(
                schemaRoot, /*DictionaryProvider=*/ null, Channels.newChannel(out))) {
      writer.start();
      for (FieldVector v : schemaRoot.getFieldVectors()) {
        v.reset();
      }
      for (Column<?> column : table.columns()) {
        setBytes(schemaRoot, column);
      }
      schemaRoot.setRowCount(table.rowCount());
      writer.writeBatch();
      writer.end();
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
