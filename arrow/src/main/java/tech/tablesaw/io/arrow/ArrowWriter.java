package tech.tablesaw.io.arrow;

import static org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE;
import static org.apache.arrow.vector.types.FloatingPointPrecision.SINGLE;

import java.util.ArrayList;
import java.util.List;
import org.apache.arrow.vector.types.DateUnit;
import org.apache.arrow.vector.types.TimeUnit;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class ArrowWriter {

  private static Schema tableSchema(Table table) {
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

  public void write(Table table) {}
}
