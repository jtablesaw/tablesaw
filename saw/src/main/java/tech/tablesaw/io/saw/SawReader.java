package tech.tablesaw.io.saw;

import static tech.tablesaw.io.saw.SawUtils.BOOLEAN;
import static tech.tablesaw.io.saw.SawUtils.DOUBLE;
import static tech.tablesaw.io.saw.SawUtils.FLOAT;
import static tech.tablesaw.io.saw.SawUtils.INSTANT;
import static tech.tablesaw.io.saw.SawUtils.INTEGER;
import static tech.tablesaw.io.saw.SawUtils.LOCAL_DATE;
import static tech.tablesaw.io.saw.SawUtils.LOCAL_DATE_TIME;
import static tech.tablesaw.io.saw.SawUtils.LOCAL_TIME;
import static tech.tablesaw.io.saw.SawUtils.LONG;
import static tech.tablesaw.io.saw.SawUtils.SHORT;
import static tech.tablesaw.io.saw.SawUtils.STRING;
import static tech.tablesaw.io.saw.SawUtils.TEXT;
import static tech.tablesaw.io.saw.TableMetadata.METADATA_FILE_NAME;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.iq80.snappy.SnappyFramedInputStream;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.LookupTableWrapper;

@SuppressWarnings("WeakerAccess")
@Beta
public class SawReader {

  private static final int READER_POOL_SIZE = 10;

  public static Table readTable(String path) {
    Path sawPath = Paths.get(path);
    return readTable(sawPath.toFile());
  }

  public static Table readTable(String path, int threadPoolSize) {
    Path sawPath = Paths.get(path);
    return readTable(sawPath.toFile(), threadPoolSize);
  }

  /**
   * Reads a tablesaw table into memory
   *
   * @param file The location of the table data. If not fully specified, it is interpreted as
   *     relative to the working directory. The path will typically end in ".saw", as in
   *     "mytables/nasdaq-2015.saw"
   * @throws UncheckedIOException wrapping an IOException if the file cannot be read
   */
  public static Table readTable(File file) {
    return readTable(file, READER_POOL_SIZE);
  }

  /**
   * Reads a tablesaw table into memory
   *
   * @param file The location of the table data. If not fully specified, it is interpreted as
   *     relative to the working directory. The path will typically end in ".saw", as in
   *     "mytables/nasdaq-2015.saw"
   * @param threadPoolSize The size of the the thread-pool allocated to reading. Each column is read
   *     in own thread
   * @throws UncheckedIOException wrapping an IOException if the file cannot be read
   */
  public static Table readTable(File file, int threadPoolSize) {

    // final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
    // final ExecutorService executor = Executors.newCachedThreadPool();
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    final TableMetadata tableMetadata;
    final Path sawPath = file.toPath();

    try {
      tableMetadata = readTableMetadata(sawPath.resolve(METADATA_FILE_NAME));
    } catch (IOException e) {
      throw new UncheckedIOException("Error attempting to load saw data", e);
    }

    final List<ColumnMetadata> columnMetadata =
        ImmutableList.copyOf(tableMetadata.getColumnMetadataList());
    final Table table = Table.create(tableMetadata.name());

    // Note: We do some extra work with the hash map to ensure that the columns are returned
    // to the table in original order
    List<Callable<Column<?>>> callables = new ArrayList<>();
    Map<String, Column<?>> columns = new ConcurrentHashMap<>();
    try {
      for (ColumnMetadata column : columnMetadata) {
        callables.add(
            () -> {
              Path columnPath = sawPath.resolve(column.getId());
              return readColumn(columnPath.toString(), tableMetadata, column);
            });
      }
      List<Future<Column<?>>> futures = executor.invokeAll(callables);
      for (Future<Column<?>> future : futures) {
        Column<?> column = future.get();
        columns.put(column.name(), column);
      }
      for (ColumnMetadata metadata : columnMetadata) {
        table.addColumns(columns.get(metadata.getName()));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    } catch (ExecutionException e) {
      throw new IllegalStateException(e);
    } finally {
      executor.shutdown();
    }
    return table;
  }

  private static Column<?> readColumn(
      String fileName, TableMetadata tableMetadata, ColumnMetadata columnMetadata)
      throws IOException {

    final String typeString = columnMetadata.getType();

    switch (typeString) {
      case FLOAT:
        return readFloatColumn(fileName, columnMetadata);
      case DOUBLE:
        return readDoubleColumn(fileName, columnMetadata);
      case INTEGER:
        return readIntColumn(fileName, columnMetadata);
      case BOOLEAN:
        return readBooleanColumn(fileName, columnMetadata);
      case LOCAL_DATE:
        return readLocalDateColumn(fileName, columnMetadata);
      case LOCAL_TIME:
        return readLocalTimeColumn(fileName, columnMetadata);
      case LOCAL_DATE_TIME:
        return readLocalDateTimeColumn(fileName, columnMetadata);
      case INSTANT:
        return readInstantColumn(fileName, columnMetadata);
      case STRING:
        return readStringColumn(fileName, tableMetadata, columnMetadata);
      case TEXT:
        return readTextColumn(fileName, tableMetadata, columnMetadata);
      case SHORT:
        return readShortColumn(fileName, columnMetadata);
      case LONG:
        return readLongColumn(fileName, columnMetadata);
      default:
        throw new IllegalStateException("Unhandled column type writing columns: " + typeString);
    }
  }

  private static FloatColumn readFloatColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    FloatColumn floats = FloatColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          float cell = dis.readFloat();
          floats.append(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return floats;
  }

  private static DoubleColumn readDoubleColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    DoubleColumn doubles = DoubleColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          double cell = dis.readDouble();
          doubles.append(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return doubles;
  }

  private static IntColumn readIntColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    IntColumn ints = IntColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          ints.append(dis.readInt());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return ints;
  }

  private static ShortColumn readShortColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    ShortColumn ints = ShortColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          ints.append(dis.readShort());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return ints;
  }

  private static LongColumn readLongColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    LongColumn ints = LongColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          ints.append(dis.readLong());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return ints;
  }

  private static DateColumn readLocalDateColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    DateColumn column = DateColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          int cell = dis.readInt();
          column.appendInternal(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return column;
  }

  private static DateTimeColumn readLocalDateTimeColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    DateTimeColumn dates = DateTimeColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          long cell = dis.readLong();
          dates.appendInternal(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return dates;
  }

  private static InstantColumn readInstantColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    InstantColumn instants = InstantColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          long cell = dis.readLong();
          instants.appendInternal(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return instants;
  }

  private static TimeColumn readLocalTimeColumn(String fileName, ColumnMetadata metadata)
      throws IOException {
    TimeColumn column = TimeColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          int cell = dis.readInt();
          column.appendInternal(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return column;
  }

  /**
   * Reads the encoded StringColumn from the given file and stuffs it into a new StringColumn,
   * saving time by updating the dictionary directly and just writing ints to the column's data
   */
  private static StringColumn readStringColumn(
      String fileName, TableMetadata tableMetadata, ColumnMetadata columnMetadata)
      throws IOException {

    try (DataInputStream dis =
        new DataInputStream(new SnappyFramedInputStream(new FileInputStream(fileName), true))) {

      return new LookupTableWrapper()
          .readFromStream(
              dis,
              columnMetadata.getName(),
              columnMetadata.getStringColumnKeySize(),
              tableMetadata.rowCount());
    }
  }

  /** Reads the TextColumn data from the given file and stuffs it into a new TextColumn */
  private static TextColumn readTextColumn(
      String fileName, TableMetadata tableMetadata, ColumnMetadata columnMetadata)
      throws IOException {

    TextColumn textColumn = TextColumn.create(columnMetadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {

      int j = 0;
      while (j < tableMetadata.getRowCount()) {
        textColumn.append(dis.readUTF());
        j++;
      }
    }
    return textColumn;
  }

  private static BooleanColumn readBooleanColumn(String fileName, ColumnMetadata metadata)
      throws IOException {

    BooleanColumn bools = BooleanColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          byte cell = dis.readByte();
          bools.append(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return bools;
  }

  /**
   * Reads in a json-formatted file and creates a TableMetadata instance from it. Files are expected
   * to be in the format provided by TableMetadata}
   *
   * @param filePath The path
   * @throws IOException if the file can not be read
   */
  private static TableMetadata readTableMetadata(Path filePath) throws IOException {

    byte[] encoded = Files.readAllBytes(filePath);
    return TableMetadata.fromJson(new String(encoded, StandardCharsets.UTF_8));
  }
}
