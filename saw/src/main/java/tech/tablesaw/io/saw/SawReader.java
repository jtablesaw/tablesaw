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
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.io.DataInputStream;
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
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.IntDictionaryMap;
import tech.tablesaw.columns.strings.ShortDictionaryMap;

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

    final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

    final TableMetadata tableMetadata;
    final Path sawPath = file.toPath();

    try {
      tableMetadata = readTableMetadata(sawPath.resolve(METADATA_FILE_NAME));
    } catch (IOException e) {
      throw new UncheckedIOException("Error attempting to load saw data", e);
    }

    final List<ColumnMetadata> columnMetadata =
        ImmutableList.copyOf(tableMetadata.getColumnMetadataList());
    final Table table = Table.create(tableMetadata.getName());

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
        table.internalAddWithoutValidation(columns.get(metadata.getName()));
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
    final int rowcount = tableMetadata.getRowCount();
    switch (typeString) {
      case FLOAT:
        return readFloatColumn(fileName, columnMetadata, rowcount);
      case DOUBLE:
        return readDoubleColumn(fileName, columnMetadata, rowcount);
      case INTEGER:
        return readIntColumn(fileName, columnMetadata, rowcount);
      case BOOLEAN:
        return readBooleanColumn(fileName, columnMetadata, rowcount);
      case LOCAL_DATE:
        return readLocalDateColumn(fileName, columnMetadata, rowcount);
      case LOCAL_TIME:
        return readLocalTimeColumn(fileName, columnMetadata, rowcount);
      case LOCAL_DATE_TIME:
        return readLocalDateTimeColumn(fileName, columnMetadata, rowcount);
      case INSTANT:
        return readInstantColumn(fileName, columnMetadata, rowcount);
      case STRING:
        return readStringColumn(fileName, columnMetadata, rowcount);
      case TEXT:
        return readTextColumn(fileName, columnMetadata, rowcount);
      case SHORT:
        return readShortColumn(fileName, columnMetadata, rowcount);
      case LONG:
        return readLongColumn(fileName, columnMetadata, rowcount);
      default:
        throw new IllegalStateException("Unhandled column type writing columns: " + typeString);
    }
  }

  private static FloatColumn readFloatColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    float[] data = new float[rowcount];
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readFloat();
      }
    }
    return FloatColumn.create(metadata.getName(), data);
  }

  private static DoubleColumn readDoubleColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {
    double[] data = new double[rowcount];
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readDouble();
      }
    }
    return DoubleColumn.create(metadata.getName(), data);
  }

  private static IntColumn readIntColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return IntColumn.create(metadata.getName(), readIntValues(fileName, rowcount));
  }

  private static ShortColumn readShortColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    short[] data = new short[rowcount];
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readShort();
      }
    }
    return ShortColumn.create(metadata.getName(), data);
  }

  private static LongColumn readLongColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return LongColumn.create(metadata.getName(), readLongValues(fileName, rowcount));
  }

  private static DateColumn readLocalDateColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {
    return DateColumn.createInternal(metadata.getName(), readIntValues(fileName, rowcount));
  }

  private static int[] readIntValues(String fileName, int rowcount) throws IOException {
    int[] data = new int[rowcount];
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readInt();
      }
    }
    return data;
  }

  private static DateTimeColumn readLocalDateTimeColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {
    long[] data = readLongValues(fileName, rowcount);
    return DateTimeColumn.createInternal(metadata.getName(), data);
  }

  private static long[] readLongValues(String fileName, int rowcount) throws IOException {
    long[] data = new long[rowcount];
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readLong();
      }
    }
    return data;
  }

  private static InstantColumn readInstantColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {
    return InstantColumn.createInternal(metadata.getName(), readLongValues(fileName, rowcount));
  }

  private static TimeColumn readLocalTimeColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {
    return TimeColumn.createInternal(metadata.getName(), readIntValues(fileName, rowcount));
  }

  /**
   * Reads the encoded StringColumn from the given file and stuffs it into a new StringColumn,
   * saving time by updating the dictionary directly and just writing ints to the column's data
   */
  private static StringColumn readStringColumn(
      String fileName, ColumnMetadata columnMetadata, int rowcount) throws IOException {

    try (DataInputStream dis =
        new DataInputStream(new SnappyFramedInputStream(new FileInputStream(fileName), true))) {

      if (columnMetadata.getStringColumnKeySize().equals(Byte.class.getSimpleName())) {
        return StringColumn.createInternal(
            columnMetadata.getName(), getByteMap(dis, columnMetadata, rowcount));
      }
      if (columnMetadata.getStringColumnKeySize().equals(Integer.class.getSimpleName())) {
        return StringColumn.createInternal(
            columnMetadata.getName(), getIntMap(dis, columnMetadata, rowcount));
      }
      return StringColumn.createInternal(
          columnMetadata.getName(), getShortMap(dis, columnMetadata, rowcount));
    }
  }

  private static ByteDictionaryMap getByteMap(
      DataInputStream dis, ColumnMetadata metaData, int rowcount) throws IOException {

    int cardinality = metaData.getCardinality();

    byte[] data = new byte[rowcount];

    byte[] keys = new byte[cardinality];
    byte[] countKeys = new byte[cardinality];
    String[] values = new String[cardinality];
    int[] counts = new int[cardinality];

    // process the data
    // first we read the keys and values for the maps
    for (int k = 0; k < cardinality; k++) {
      keys[k] = dis.readByte();
    }
    for (int k = 0; k < cardinality; k++) {
      values[k] = dis.readUTF();
    }
    for (int k = 0; k < cardinality; k++) {
      countKeys[k] = dis.readByte();
    }
    for (int k = 0; k < cardinality; k++) {
      counts[k] = dis.readInt();
    }

    // get the column entries
    for (int i = 0; i < rowcount; i++) {
      data[i] = dis.readByte();
    }

    Object2ByteOpenHashMap<String> valueToKey = new Object2ByteOpenHashMap<>(values, keys);
    Byte2ObjectMap<String> keyToValue = new Byte2ObjectOpenHashMap<>(keys, values);
    Byte2IntOpenHashMap keyToCount = new Byte2IntOpenHashMap(countKeys, counts);

    return new ByteDictionaryMap.ByteDictionaryBuilder()
        .setValues(data)
        .setValueToKey(valueToKey)
        .setKeyToValue(keyToValue)
        .setKeyToCount(keyToCount)
        .setNextIndex(metaData.getNextStringKey())
        .build();
  }

  private static ShortDictionaryMap getShortMap(
      DataInputStream dis, ColumnMetadata metaData, int rowcount) throws IOException {

    int cardinality = metaData.getCardinality();

    short[] data = new short[rowcount];

    short[] keys = new short[cardinality];
    short[] countKeys = new short[cardinality];
    String[] values = new String[cardinality];
    int[] counts = new int[cardinality];

    // process the data
    // first we read the keys and values for the maps
    for (int k = 0; k < cardinality; k++) {
      keys[k] = dis.readShort();
    }
    for (int k = 0; k < cardinality; k++) {
      values[k] = dis.readUTF();
    }
    for (int k = 0; k < cardinality; k++) {
      countKeys[k] = dis.readShort();
    }
    for (int k = 0; k < cardinality; k++) {
      counts[k] = dis.readInt();
    }

    // get the column entries
    for (int i = 0; i < rowcount; i++) {
      data[i] = dis.readShort();
    }

    Object2ShortOpenHashMap<String> valueToKey = new Object2ShortOpenHashMap<>(values, keys);
    Short2ObjectMap<String> keyToValue = new Short2ObjectOpenHashMap<>(keys, values);
    Short2IntOpenHashMap keyToCount = new Short2IntOpenHashMap(countKeys, counts);

    return new ShortDictionaryMap.ShortDictionaryBuilder()
        .setValues(data)
        .setValueToKey(valueToKey)
        .setKeyToValue(keyToValue)
        .setKeyToCount(keyToCount)
        .setNextIndex(metaData.getNextStringKey())
        .build();
  }

  private static IntDictionaryMap getIntMap(
      DataInputStream dis, ColumnMetadata metaData, int rowcount) throws IOException {

    int cardinality = metaData.getCardinality();

    int[] data = new int[rowcount];

    int[] keys = new int[cardinality];
    int[] countKeys = new int[cardinality];
    String[] values = new String[cardinality];
    int[] counts = new int[cardinality];

    // process the data
    // first we read the keys and values for the maps
    for (int k = 0; k < cardinality; k++) {
      keys[k] = dis.readInt();
    }
    for (int k = 0; k < cardinality; k++) {
      values[k] = dis.readUTF();
    }
    for (int k = 0; k < cardinality; k++) {
      countKeys[k] = dis.readInt();
    }
    for (int k = 0; k < cardinality; k++) {
      counts[k] = dis.readInt();
    }

    // get the column entries
    for (int i = 0; i < rowcount; i++) {
      data[i] = dis.readInt();
    }

    Object2IntOpenHashMap<String> valueToKey = new Object2IntOpenHashMap<>(values, keys);
    Int2ObjectMap<String> keyToValue = new Int2ObjectOpenHashMap<>(keys, values);
    Int2IntOpenHashMap keyToCount = new Int2IntOpenHashMap(countKeys, counts);

    return new IntDictionaryMap.IntDictionaryBuilder()
        .setValues(data)
        .setValueToKey(valueToKey)
        .setKeyToValue(keyToValue)
        .setKeyToCount(keyToCount)
        .setNextIndex(metaData.getNextStringKey())
        .build();
  }

  /** Reads the TextColumn data from the given file and stuffs it into a new TextColumn */
  private static TextColumn readTextColumn(
      String fileName, ColumnMetadata columnMetadata, int rowcount) throws IOException {

    TextColumn textColumn = TextColumn.create(columnMetadata.getName(), rowcount);
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {

      for (int j = 0; j < rowcount; j++) {
        textColumn.set(j, dis.readUTF());
      }
    }
    return textColumn;
  }

  private static BooleanColumn readBooleanColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {

    BooleanColumn column = BooleanColumn.create(metadata.getName());
    try (FileInputStream fis = new FileInputStream(fileName);
        SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
        DataInputStream dis = new DataInputStream(sis)) {
      for (int i = 0; i < rowcount; i++) {
        column.append(dis.readByte());
      }
    }
    return column;
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
