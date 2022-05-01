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

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import net.jpountz.lz4.*;
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
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.IntDictionaryMap;
import tech.tablesaw.columns.strings.ShortDictionaryMap;

@Beta
public class SawReader {

  private final Path sawPath;

  private final SawMetadata sawMetadata;

  private SawReadOptions sawReadOptions = SawReadOptions.defaultOptions();

  public SawReader(Path sawPath) {
    this.sawPath = sawPath;
    this.sawMetadata = SawMetadata.readMetadata(sawPath);
  }

  public SawReader(Path sawPath, SawReadOptions options) {
    this.sawPath = sawPath;
    this.sawReadOptions = options;
    this.sawMetadata = SawMetadata.readMetadata(sawPath);
  }

  public SawReader(File sawPathFile) {
    this.sawPath = sawPathFile.toPath();
    this.sawMetadata = SawMetadata.readMetadata(sawPath);
  }

  public SawReader(File sawPathFile, SawReadOptions options) {
    this.sawPath = sawPathFile.toPath();
    this.sawReadOptions = options;
    this.sawMetadata = SawMetadata.readMetadata(sawPath);
  }

  public SawReader(String sawPathName) {
    this.sawPath = setPath(sawPathName);
    this.sawMetadata = SawMetadata.readMetadata(sawPath);
  }

  public SawReader(String sawPathName, SawReadOptions options) {
    this.sawPath = setPath(sawPathName);
    this.sawReadOptions = options;
    this.sawMetadata = SawMetadata.readMetadata(sawPath);
  }

  private Path setPath(String parentFolderName) {
    Preconditions.checkArgument(
        parentFolderName != null, "The folder name for the saw output cannot be null");
    Preconditions.checkArgument(
        !parentFolderName.isEmpty(), "The folder name for the saw output cannot be empty");
    return Paths.get(parentFolderName);
  }

  public String shape() {
    return sawMetadata.shape();
  }

  public int columnCount() {
    return sawMetadata.columnCount();
  }

  public int rowCount() {
    return sawMetadata.getRowCount();
  }

  public List<String> columnNames() {
    return sawMetadata.columnNames();
  }

  public Table structure() {
    return sawMetadata.structure();
  }

  public Table read() {

    final ExecutorService executor =
        Executors.newFixedThreadPool(sawReadOptions.getThreadPoolSize());
    // The column names to filter for, if we don't want the whole table
    final Set<String> selectedColumns = new HashSet<>(sawReadOptions.getSelectedColumns());

    final List<ColumnMetadata> columnMetadata = getMetadata(selectedColumns);

    final Table table = Table.create(sawMetadata.getTableName());

    // Note: We do some extra work with the hash map to ensure that the columns are returned
    // to the table in original order
    List<Callable<Column<?>>> callables = new ArrayList<>();
    Map<String, Column<?>> columns = new ConcurrentHashMap<>();
    try {
      for (ColumnMetadata column : columnMetadata) {
        callables.add(
            () -> {
              Path columnPath = sawPath.resolve(column.getId());
              return readColumn(columnPath.toString(), sawMetadata, column);
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

  private List<ColumnMetadata> getMetadata(Set<String> selectedColumns) {
    if (selectedColumns.isEmpty()) {
      return ImmutableList.copyOf(sawMetadata.getColumnMetadataList());
    }
    return ImmutableList.copyOf(
        sawMetadata.getColumnMetadataList().stream()
            .filter(x -> selectedColumns.contains(x.getName()))
            .collect(Collectors.toList()));
  }

  private Column<?> readColumn(
      String fileName, SawMetadata sawMetadata, ColumnMetadata columnMetadata) throws IOException {

    final String typeString = columnMetadata.getType();
    final int rowcount = sawMetadata.getRowCount();
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
      case SHORT:
        return readShortColumn(fileName, columnMetadata, rowcount);
      case LONG:
        return readLongColumn(fileName, columnMetadata, rowcount);
      default:
        throw new IllegalStateException("Unhandled column type writing columns: " + typeString);
    }
  }

  /**
   * Returns a data input stream for reading from a file with the given name
   *
   * @throws IOException if anything goes wrong
   */
  private DataInputStream inputStream(String fileName) throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    if (sawMetadata.getCompressionType().equals(CompressionType.NONE)) {
      return new DataInputStream(fis);
    } else if (sawMetadata.getCompressionType().equals(CompressionType.LZ4)) {
      LZ4BlockInputStream lis = new LZ4BlockInputStream(fis);
      return new DataInputStream(lis);
    } else {
      SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
      return new DataInputStream(sis);
    }
  }

  private FloatColumn readFloatColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    float[] data = new float[rowcount];
    try (DataInputStream dis = inputStream(fileName)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readFloat();
      }
    }
    return FloatColumn.create(metadata.getName(), data);
  }

  private DoubleColumn readDoubleColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    double[] data = new double[rowcount];
    try (DataInputStream dis = inputStream(fileName)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readDouble();
      }
    }
    return DoubleColumn.create(metadata.getName(), data);
  }

  private IntColumn readIntColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return IntColumn.create(metadata.getName(), readIntValues(fileName, rowcount));
  }

  private ShortColumn readShortColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    short[] data = new short[rowcount];
    try (DataInputStream dis = inputStream(fileName)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readShort();
      }
    }
    return ShortColumn.create(metadata.getName(), data);
  }

  private LongColumn readLongColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return LongColumn.create(metadata.getName(), readLongValues(fileName, rowcount));
  }

  private DateColumn readLocalDateColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return DateColumn.createInternal(metadata.getName(), readIntValues(fileName, rowcount));
  }

  private int[] readIntValues(String fileName, int rowcount) throws IOException {
    int[] data = new int[rowcount];
    try (DataInputStream dis = inputStream(fileName)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readInt();
      }
    }
    return data;
  }

  private DateTimeColumn readLocalDateTimeColumn(
      String fileName, ColumnMetadata metadata, int rowcount) throws IOException {
    long[] data = readLongValues(fileName, rowcount);
    return DateTimeColumn.createInternal(metadata.getName(), data);
  }

  private long[] readLongValues(String fileName, int rowcount) throws IOException {
    long[] data = new long[rowcount];
    try (DataInputStream dis = inputStream(fileName)) {
      for (int i = 0; i < rowcount; i++) {
        data[i] = dis.readLong();
      }
    }
    return data;
  }

  private InstantColumn readInstantColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return InstantColumn.createInternal(metadata.getName(), readLongValues(fileName, rowcount));
  }

  private TimeColumn readLocalTimeColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {
    return TimeColumn.createInternal(metadata.getName(), readIntValues(fileName, rowcount));
  }

  /**
   * Reads the encoded StringColumn from the given file and stuffs it into a new StringColumn,
   * saving time by updating the dictionary directly and just writing ints to the column's data
   */
  private StringColumn readStringColumn(
      String fileName, ColumnMetadata columnMetadata, int rowcount) throws IOException {

    try (DataInputStream dis = inputStream(fileName)) {

      if (columnMetadata.getStringColumnKeySize().equals(Byte.class.getSimpleName())) {
        return StringColumn.createInternal(
            columnMetadata.getName(), getByteMap(dis, columnMetadata, rowcount));
      }
      if (columnMetadata.getStringColumnKeySize().equals(Integer.class.getSimpleName())) {
        IntDictionaryMap intMap = getIntMap(dis, columnMetadata, rowcount);
        return StringColumn.createInternal(columnMetadata.getName(), intMap);
      }
      ShortDictionaryMap shortMap = getShortMap(dis, columnMetadata, rowcount);
      return StringColumn.createInternal(columnMetadata.getName(), shortMap);
    }
  }

  private ByteDictionaryMap getByteMap(DataInputStream dis, ColumnMetadata metaData, int rowcount)
      throws IOException {

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

  private ShortDictionaryMap getShortMap(DataInputStream dis, ColumnMetadata metaData, int rowcount)
      throws IOException {

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
        .setCanPromoteToText(true) // TODO: read from metadata
        .setNextIndex(metaData.getNextStringKey())
        .build();
  }

  private IntDictionaryMap getIntMap(DataInputStream dis, ColumnMetadata metaData, int rowcount)
      throws IOException {

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

  private BooleanColumn readBooleanColumn(String fileName, ColumnMetadata metadata, int rowcount)
      throws IOException {

    BooleanColumn column = BooleanColumn.create(metadata.getName());
    int trueBytesLength = metadata.getTrueBytesLength();
    int falseBytesLength = metadata.getFalseBytesLength();
    int missingBytesLength = metadata.getMissingBytesLength();
    int trueAndFalseLength = trueBytesLength + falseBytesLength;
    int allBytesLength = trueAndFalseLength + missingBytesLength;
    byte[] trueBytes = new byte[trueBytesLength];
    byte[] falseBytes = new byte[falseBytesLength];
    byte[] missingBytes = new byte[missingBytesLength];
    try (DataInputStream dis = inputStream(fileName)) {
      for (int i = 0; i < trueBytesLength; i++) {
        trueBytes[i] = dis.readByte();
      }
      column.trueBytes(trueBytes);
      int j = 0;
      for (int i = trueBytesLength; i < trueAndFalseLength; i++) {
        falseBytes[j] = dis.readByte();
        j++;
      }
      column.falseBytes(falseBytes);
      int k = 0;
      for (int i = trueAndFalseLength; i < allBytesLength; i++) {
        missingBytes[k] = dis.readByte();
        k++;
      }
      column.missingBytes(missingBytes);
    }
    return column;
  }
}
