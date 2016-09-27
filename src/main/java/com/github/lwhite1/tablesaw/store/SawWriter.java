package com.github.lwhite1.tablesaw.store;

import com.github.lwhite1.tablesaw.api.*;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.table.Relation;
import org.iq80.snappy.SnappyFramedOutputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.*;

public final class SawWriter {
  private static final int FLUSH_AFTER_ITERATIONS = 10_000;
  private static final String FILE_EXTENSION = "saw";
  private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");
  private static final Pattern SEPARATOR_PATTERN = Pattern.compile(File.separator);

  private SawWriter() { }

  static String saveTable(String folderName, Relation table) throws IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CompletionService writerCompletionService = new ExecutorCompletionService<>(executorService);

    String name = table.name();
    name = WHITE_SPACE_PATTERN.matcher(name).replaceAll(""); // remove whitespace from the table name
    name = SEPARATOR_PATTERN.matcher(name).replaceAll("_"); // remove path separators from the table name

    String storageFolder = folderName + File.separator + name + '.' + FILE_EXTENSION;

    Path path = Paths.get(storageFolder);

    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    writeTableMetadata(path.toString() + File.separator + TableMetadata.fileName, table);

    try {
      for (Column column : table.columns()) {
        writerCompletionService.submit(() -> {
          Path columnPath = path.resolve(column.id());
          writeColumn(columnPath.toString(), column);
          return null;
        });
      }
      for (int i = 0; i < table.columnCount(); i++) {
        Future future = writerCompletionService.take();
        future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
    executorService.shutdown();

    return storageFolder;
  }

  public static void writeColumn(String fileName, Column column) {
    try {
      switch (column.type()) {
        case FLOAT:
          writeColumn(fileName, (FloatColumn) column);
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
        case CATEGORY:
          writeColumn(fileName, (CategoryColumn) column);
          break;
        case SHORT_INT:
          writeColumn(fileName, (ShortColumn) column);
          break;
        case LONG_INT:
          writeColumn(fileName, (LongColumn) column);
          break;
        default:
          throw new RuntimeException("Unhandled column type writing columns");
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      throw new RuntimeException("IOException writing to file");
    }
  }

  static void writeColumn(String fileName, FloatColumn column) throws IOException {
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

  /**
   * Writes out the values of the category column encoded as ints to minimize the time required for subsequent reads
   * <p>
   * The files are written Strings first, then the ints that encode them so they can be read in the opposite order
   *
   * @throws IOException
   */
  static void writeColumn(String fileName, CategoryColumn column) throws IOException {
    int categoryCount = column.dictionaryMap().size();
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {

      dos.writeInt(categoryCount);
      // write the strings
      SortedSet<Integer> keys = new TreeSet<>(column.dictionaryMap().keyToValueMap().keySet());
      for (int key : keys) {
        dos.writeUTF(column.dictionaryMap().get(key));
      }
      dos.flush();

      // write the integer values that represent the strings
      int i = 0;
      for (int d : column.data()) {
        dos.writeInt(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
    }
  }

  //TODO(lwhite): saveTable the column using integer compression
  static void writeColumn(String fileName, IntColumn column) throws IOException {
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

  static void writeColumn(String fileName, ShortColumn column) throws IOException {
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

  static void writeColumn(String fileName, LongColumn column) throws IOException {
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

  //TODO(lwhite): saveTable the column using integer compression
  static void writeColumn(String fileName, DateColumn column) throws IOException {
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

  static void writeColumn(String fileName, DateTimeColumn column) throws IOException {
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

  //TODO(lwhite): saveTable the column using integer compression
  static void writeColumn(String fileName, TimeColumn column) throws IOException {
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

  //TODO(lwhite): saveTable the column using compressed bitmap
  static void writeColumn(String fileName, BooleanColumn column) throws IOException {
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
   * Writes out a json-formatted representation of the given {@code table}'s metadata to the given {@code file}
   *
   * @param fileName Expected to be fully specified
   * @throws IOException if the file can not be read
   */
  private static void writeTableMetadata(String fileName, Relation table) throws IOException {
    String json = new TableMetadata(table).toJson();
    Path path = Paths.get(fileName);
    Files.write(path, json.getBytes(StandardCharsets.UTF_8), CREATE, WRITE, TRUNCATE_EXISTING);
  }
}
