package tech.tablesaw.io.arrow;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Path;
import java.time.*;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.types.Types;
import tech.tablesaw.api.*;
import tech.tablesaw.io.RuntimeIOException;

/**
 * Reader for Apache Arrow Streaming Format files. This class is capable of reading Arrow-formatted
 * files that were written by Tablesaw {@link ArrowWriter}, but not necessarily other applications.
 *
 * <p>Note: only those vector types needed to support Tablesaw are implemented
 */
public class ArrowReader {

  private final File file;

  public ArrowReader(Path path) {
    this.file = path.toFile();
  }

  public ArrowReader(File file) {
    this.file = file;
  }

  public Table read() {
    Table table = Table.create(file.getName());
    // Create a RootAllocator to allocate memory for our vectors
    BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
    boolean eos = false;
    try (FileInputStream in = new FileInputStream(file);
        ArrowStreamReader reader = new ArrowStreamReader(Channels.newChannel(in), allocator)) {
      VectorSchemaRoot vsr = reader.getVectorSchemaRoot();
      while (!eos) {
        VectorSchemaRoot next = reader.getVectorSchemaRoot();
        eos = reader.loadNextBatch();
        next.setRowCount(vsr.getRowCount());
        for (FieldVector v : vsr.getFieldVectors()) {
          getBytes(v, table);
        }
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    return table;
  }

  private void getBytes(FieldVector v, Table table) {

    final Types.MinorType type = v.getMinorType();
    final String name = v.getName();
    switch (type) {
      case VARCHAR:
        StringColumn strCol = StringColumn.create(name);
        VarCharVector vcv = (VarCharVector) v;
        for (int i = 0; i < vcv.getValueCount(); i++) {
          strCol.append(new String(vcv.get(i)));
        }
        if (!table.containsColumn(name)) {
          table.addColumns(strCol);
        } else {
          table.stringColumn(strCol.name()).append(strCol);
        }
        break;
      case BIGINT:
        LongColumn longCol = LongColumn.create(name);
        BigIntVector bigIntVector = (BigIntVector) v;
        for (int i = 0; i < bigIntVector.getValueCount(); i++) {
          if (!bigIntVector.isNull(i)) {
            longCol.append(bigIntVector.get(i));
          } else {
            longCol.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(longCol);
        } else {
          table.longColumn(longCol.name()).append(longCol);
        }
        break;
      case INT:
        IntColumn intCol = IntColumn.create(name);
        IntVector intVector = (IntVector) v;
        for (int i = 0; i < intVector.getValueCount(); i++) {
          if (!intVector.isNull(i)) {
            intCol.append(intVector.get(i));
          } else {
            intCol.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(intCol);
        } else {
          table.intColumn(intCol.name()).append(intCol);
        }
        break;
      case SMALLINT:
        ShortColumn shortColumn = ShortColumn.create(name);
        SmallIntVector smallIntVector = (SmallIntVector) v;
        for (int i = 0; i < smallIntVector.getValueCount(); i++) {
          if (!smallIntVector.isNull(i)) {
            shortColumn.append(smallIntVector.get(i));
          } else {
            shortColumn.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(shortColumn);
        } else {
          table.shortColumn(shortColumn.name()).append(shortColumn);
        }
        break;
      case DATEDAY:
        DateColumn dateCol = DateColumn.create(name);
        DateDayVector dateDayVector = (DateDayVector) v;
        for (int i = 0; i < dateDayVector.getValueCount(); i++) {
          if (!dateDayVector.isNull(i)) {
            dateCol.append(LocalDate.ofEpochDay(dateDayVector.get(i)));
          } else {
            dateCol.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(dateCol);
        } else {
          table.dateColumn(dateCol.name()).append(dateCol);
        }
        break;
      case TIMESTAMPMILLI:
        DateTimeColumn dtCol = DateTimeColumn.create(name);
        TimeStampMilliVector dtVector = (TimeStampMilliVector) v;
        for (int i = 0; i < dtVector.getValueCount(); i++) {
          if (!dtVector.isNull(i)) {
            dtCol.append(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(dtVector.get(i)), ZoneOffset.UTC));
          } else {
            dtCol.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(dtCol);
        } else {
          table.dateTimeColumn(dtCol.name()).append(dtCol);
        }
        break;
      case TIMEMILLI:
        TimeColumn timeColumn = TimeColumn.create(name);
        TimeMilliVector timeVector = (TimeMilliVector) v;
        for (int i = 0; i < timeVector.getValueCount(); i++) {
          if (!timeVector.isNull(i)) {
            timeColumn.append(LocalTime.ofNanoOfDay(((long) timeVector.get(i)) * 1_000_000));
          } else {
            timeColumn.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(timeColumn);
        } else {
          table.timeColumn(timeColumn.name()).append(timeColumn);
        }
        break;
      case TIMESTAMPMILLITZ:
        InstantColumn instantColumn = InstantColumn.create(name);
        TimeStampMilliTZVector instantVector = (TimeStampMilliTZVector) v;
        for (int i = 0; i < instantVector.getValueCount(); i++) {
          if (!instantVector.isNull(i)) {
            instantColumn.append(Instant.ofEpochMilli(instantVector.get(i)));
          } else {
            instantColumn.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(instantColumn);
        } else {
          table.instantColumn(instantColumn.name()).append(instantColumn);
        }
        break;
      case BIT:
        BooleanColumn booleanColumn = BooleanColumn.create(name);
        BitVector booleanVector = (BitVector) v;
        for (int i = 0; i < booleanVector.getValueCount(); i++) {
          if (!booleanVector.isNull(i)) {
            booleanColumn.append((byte) booleanVector.get(i));
          } else {
            booleanColumn.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(booleanColumn);
        } else {
          table.booleanColumn(booleanColumn.name()).append(booleanColumn);
        }
        break;
      case FLOAT4:
        FloatColumn floatCol = FloatColumn.create(name);
        Float4Vector float4Vector = (Float4Vector) v;
        for (int i = 0; i < float4Vector.getValueCount(); i++) {
          if (!float4Vector.isNull(i)) {
            floatCol.append(float4Vector.get(i));
          } else {
            floatCol.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(floatCol);
        } else {
          table.floatColumn(floatCol.name()).append(floatCol);
        }
        break;
      case FLOAT8:
        DoubleColumn doubleCol = DoubleColumn.create(name);
        Float8Vector float8Vector = (Float8Vector) v;
        for (int i = 0; i < float8Vector.getValueCount(); i++) {
          if (!float8Vector.isNull(i)) {
            doubleCol.append(float8Vector.get(i));
          } else {
            doubleCol.appendMissing();
          }
        }
        if (!table.containsColumn(name)) {
          table.addColumns(doubleCol);
        } else {
          table.doubleColumn(doubleCol.name()).append(doubleCol);
        }
        break;
      default:
        throw new IllegalArgumentException(
            "Unhandled Column type " + type.name() + " in arrow data");
    }
  }
}
