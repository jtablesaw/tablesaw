package tech.tablesaw.io.saw;

import static tech.tablesaw.io.saw.StorageManager.*;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.iq80.snappy.SnappyFramedOutputStream;
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
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.DictionaryMap;
import tech.tablesaw.columns.strings.IntDictionaryMap;
import tech.tablesaw.columns.strings.ShortDictionaryMap;
import tech.tablesaw.table.Relation;

@SuppressWarnings("WeakerAccess")
public class SawWriter {

  private static final int FLUSH_AFTER_ITERATIONS = 10_000;
  private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");
  private static final String FILE_EXTENSION = "saw";
  private static final Pattern SEPARATOR_PATTERN = Pattern.compile(Pattern.quote(separator()));

  /**
   * Saves the data from the given table in the location specified by folderName. Within that folder
   * each table has its own sub-folder, whose name is based on the name of the table.
   *
   * <p>NOTE: If you store a table with the same name in the same folder. The data in that folder
   * will be over-written.
   *
   * <p>The storage format is the tablesaw compressed column-oriented format, which consists of a
   * set of file in a folder. The name of the folder is based on the name of the table.
   *
   * @param folderName The location of the table (for example: "mytables")
   * @param table The table to be saved
   * @return The path and name of the table
   * @throws RuntimeException wrapping IOException if the file can not be read
   */
  public static String saveTable(String folderName, Relation table) {

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CompletionService<Void> writerCompletionService =
        new ExecutorCompletionService<>(executorService);

    String name = table.name();
    name =
        WHITE_SPACE_PATTERN.matcher(name).replaceAll(""); // remove whitespace from the table name
    name =
        SEPARATOR_PATTERN
            .matcher(name)
            .replaceAll("_"); // remove path separators from the table name

    String storageFolder = folderName + separator() + name + '.' + FILE_EXTENSION;

    Path path = Paths.get(storageFolder);

    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      TableMetadata tableMetadata = new TableMetadata(table);
      writeTableMetadata(path.toString() + separator() + "Metadata.json", tableMetadata);

      List<Column<?>> columns = table.columns();
      for (int i = 0; i < columns.size(); i++) {
        Column column = columns.get(i);
        String pathString = tableMetadata.getColumnMetadataList().get(i).getId();
        writerCompletionService.submit(
            () -> {
              Path columnPath = path.resolve(pathString);
              writeColumn(columnPath.toString(), column);
              return null;
            });
      }
      for (int i = 0; i < table.columnCount(); i++) {
        Future<Void> future = writerCompletionService.take();
        future.get();
      }
    } catch (InterruptedException | ExecutionException | IOException e) {
      throw new RuntimeException(e);
    }
    executorService.shutdown();
    return storageFolder;
  }

  private static void writeColumn(String fileName, Column column) {
    try {
      final String typeName = column.type().name();
      switch (typeName) {
        case FLOAT:
          writeColumn(fileName, (FloatColumn) column);
          break;
        case DOUBLE:
          writeColumn(fileName, (DoubleColumn) column);
          break;
        case INTEGER:
          writeColumn(fileName, (IntColumn) column);
          break;
        case BOOLEAN:
          writeColumn(fileName, (BooleanColumn) column);
          break;
        case LOCAL_DATE:
          writeColumn(fileName, (DateColumn) column);
          break;
        case LOCAL_TIME:
          writeColumn(fileName, (TimeColumn) column);
          break;
        case LOCAL_DATE_TIME:
          writeColumn(fileName, (DateTimeColumn) column);
          break;
        case STRING:
          writeColumn(fileName, (StringColumn) column);
          break;
        case TEXT:
          writeColumn(fileName, (TextColumn) column);
          break;
        case INSTANT:
          writeColumn(fileName, (InstantColumn) column);
          break;
        case SHORT:
          writeColumn(fileName, (ShortColumn) column);
          break;
        case LONG:
          writeColumn(fileName, (LongColumn) column);
          break;
        default:
          throw new RuntimeException("Unhandled column type writing columns");
      }
    } catch (IOException ex) {
      throw new RuntimeException("IOException writing to file");
    }
  }

  private static void writeColumn(String fileName, FloatColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (float d : column) {
        dos.writeFloat(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  private static void writeColumn(String fileName, DoubleColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (double d : column) {
        dos.writeDouble(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  /**
   * Writes out the values of the String column encoded as ints to minimize the time required for
   * subsequent reads
   *
   * <p>The files are written Strings first, then the ints that encode them so they can be read in
   * the opposite order
   *
   * @throws IOException IOException if the file can not be read
   */
  private static void writeColumn(String fileName, StringColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {

      // write the number of unique strings
      dos.writeInt(column.countUnique());

      // write the strings
      DictionaryMap lookupTable = column.unsafeGetLookupTable();

      if (lookupTable instanceof IntDictionaryMap) {
        IntDictionaryMap dictionary = (IntDictionaryMap) lookupTable;
        ObjectSet<Int2ObjectMap.Entry<String>> entries = dictionary.getKeyValueEntries();

        for (Int2ObjectMap.Entry<String> entry : entries) {
          dos.writeInt(entry.getIntKey());
          dos.writeUTF(entry.getValue());
        }
        for (int d : dictionary.values()) {
          dos.writeInt(d);
        }
      } else if (lookupTable instanceof ShortDictionaryMap) {
        ShortDictionaryMap dictionary = (ShortDictionaryMap) lookupTable;
        ObjectSet<Short2ObjectMap.Entry<String>> entries = dictionary.getKeyValueEntries();

        for (Short2ObjectMap.Entry<String> entry : entries) {
          dos.writeShort(entry.getShortKey());
          dos.writeUTF(entry.getValue());
        }
        for (short d : dictionary.values()) {
          dos.writeShort(d);
        }
      } else if (lookupTable instanceof ByteDictionaryMap) {
        ByteDictionaryMap dictionary = (ByteDictionaryMap) lookupTable;
        ObjectSet<Byte2ObjectMap.Entry<String>> entries = dictionary.getKeyValueEntries();

        for (Byte2ObjectMap.Entry<String> entry : entries) {
          dos.writeByte(entry.getByteKey());
          dos.writeUTF(entry.getValue());
        }
        for (byte d : dictionary.values()) {
          dos.writeByte(d);
        }
      }
      dos.flush();
    }
  }

  /**
   * Writes out the values of the TextColumn
   *
   * <p>
   *
   * @throws IOException IOException if the file can not be written
   */
  private static void writeColumn(String fileName, TextColumn column) throws IOException {

    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {

      for (String str : column) {
        dos.writeUTF(str);
      }
      dos.flush();
    }
  }

  // TODO(lwhite): saveTable the column using integer compression
  private static void writeColumn(String fileName, IntColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (int d : column.data()) {
        dos.writeInt(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) { // TODO does this break the pipelining?
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  private static void writeColumn(String fileName, ShortColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (short d : column) {
        dos.writeShort(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  private static void writeColumn(String fileName, LongColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (long d : column) {
        dos.writeLong(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  // TODO(lwhite): saveTable the column using integer compression
  private static void writeColumn(String fileName, DateColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (int d : column.data()) {
        dos.writeInt(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  private static void writeColumn(String fileName, DateTimeColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (long d : column.data()) {
        dos.writeLong(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  private static void writeColumn(String fileName, InstantColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (long d : column.data()) {
        dos.writeLong(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  // TODO(lwhite): saveTable the column using integer compression
  private static void writeColumn(String fileName, TimeColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (int d : column.data()) {
        dos.writeInt(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  // TODO(lwhite): saveTable the column using compressed bitmap
  private static void writeColumn(String fileName, BooleanColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
        SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(sos)) {
      for (int i = 0; i < column.size(); i++) {
        boolean value = column.get(i);
        dos.writeBoolean(value);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
      }
      dos.flush();
    }
  }

  /**
   * Writes out a json-formatted representation of the given {@code table}'s metadata to the given
   * {@code file}
   *
   * @param fileName Expected to be fully specified
   * @throws IOException if the file can not be read
   */
  private static void writeTableMetadata(String fileName, TableMetadata metadata)
      throws IOException {
    File myFile = Paths.get(fileName).toFile();
    myFile.createNewFile();
    try (FileOutputStream fOut = new FileOutputStream(myFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut)) {
      myOutWriter.append(metadata.toJson());
    }
  }
}
