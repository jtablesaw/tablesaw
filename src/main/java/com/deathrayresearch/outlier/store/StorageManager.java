package com.deathrayresearch.outlier.store;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.BooleanColumn;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.LocalDateTimeColumn;
import com.deathrayresearch.outlier.columns.LocalTimeColumn;
import com.deathrayresearch.outlier.columns.PeriodColumn;
import com.deathrayresearch.outlier.columns.ShortColumn;
import com.deathrayresearch.outlier.columns.TextColumn;
import org.iq80.snappy.SnappyFramedInputStream;
import org.iq80.snappy.SnappyFramedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

/**
 *
 */
public class StorageManager {

  private static final ExecutorService READER_SERVICE = Executors.newFixedThreadPool(10);

  private static final CompletionService READER_COMPLETION_SERVICE =
      new ExecutorCompletionService<>(READER_SERVICE);

  private static final ExecutorService WRITER_SERVICE = Executors.newFixedThreadPool(10);

  private static final CompletionService WRITER_COMPLETION_SERVICE =
      new ExecutorCompletionService<>(WRITER_SERVICE);

  public static final int FLUSH_AFTER_ITERATIONS = 10_000;

  /**
   * @param fileName Expected to be fully specified
   * @throws IOException
   */
  public static Table readTable(String fileName) throws IOException {

    TableMetadata tableMetadata = readTableMetadata(fileName + File.separator + "Metadata.json");
    List<ColumnMetadata> columnMetadata = tableMetadata.getColumnMetadataList();
    Table table = new Table(tableMetadata);
    try {
      for (ColumnMetadata column : columnMetadata) {
        READER_COMPLETION_SERVICE.submit(() -> {
          Column c = readColumn(fileName + File.separator + column.getId(), column);
          table.addColumn(c);
          return null;
        });
      }
      for (int i = 0; i < columnMetadata.size(); i++) {
        Future future = READER_COMPLETION_SERVICE.take();
        future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
    return table;
  }

  private static Column readColumn(String fileName, ColumnMetadata columnMetadata)
      throws IOException {

    switch (columnMetadata.getType()) {
      case FLOAT:
        return readFloatColumn(fileName, columnMetadata);
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
      case PERIOD:
        return readPeriodColumn(fileName, columnMetadata);
      case TEXT:
        return readTextColumn(fileName, columnMetadata);
      case CAT:
        return readCategoryColumn(fileName, columnMetadata);
      case SHORT_INT:
        return readShortColumn(fileName, columnMetadata);
      default:
        throw new RuntimeException("Unhandled column type writing columns");
    }
  }

  public static FloatColumn readFloatColumn(String fileName, ColumnMetadata metadata) throws IOException {
    FloatColumn floats = new FloatColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          float cell = dis.readFloat();
          floats.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return floats;
  }

  public static IntColumn readIntColumn(String fileName, ColumnMetadata metadata) throws IOException {
    IntColumn ints = new IntColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          ints.add(dis.readInt());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return ints;
  }

  public static ShortColumn readShortColumn(String fileName, ColumnMetadata metadata) throws IOException {
    ShortColumn ints = new ShortColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          ints.add(dis.readShort());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return ints;
  }

  public static LocalDateColumn readLocalDateColumn(String fileName, ColumnMetadata metadata) throws IOException {
    LocalDateColumn dates = new LocalDateColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          int cell = dis.readInt();
          dates.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return dates;
  }

  public static LocalDateTimeColumn readLocalDateTimeColumn(String fileName, ColumnMetadata metadata) throws IOException {
    LocalDateTimeColumn dates = new LocalDateTimeColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          long cell = dis.readLong();
          dates.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return dates;
  }

  public static LocalTimeColumn readLocalTimeColumn(String fileName, ColumnMetadata metadata) throws IOException {
    LocalTimeColumn times = new LocalTimeColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          int cell = dis.readInt();
          times.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return times;
  }

  public static PeriodColumn readPeriodColumn(String fileName, ColumnMetadata metadata) throws IOException {
    PeriodColumn packedPeriods = new PeriodColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          int cell = dis.readInt();
          packedPeriods.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return packedPeriods;
  }

  public static TextColumn readTextColumn(String fileName, ColumnMetadata metadata) throws IOException {
    TextColumn stringColumn = new TextColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          String cell = dis.readUTF();
          stringColumn.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return stringColumn;
  }

  public static CategoryColumn readCategoryColumn(String fileName,ColumnMetadata metadata) throws IOException {
    CategoryColumn stringColumn = new CategoryColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          stringColumn.add(dis.readUTF());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return stringColumn;
  }

  public static BooleanColumn readBooleanColumn(String fileName, ColumnMetadata metadata) throws IOException {
    BooleanColumn bools = new BooleanColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          boolean cell = dis.readBoolean();
          bools.add(cell);
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return bools;
  }

  public static void saveTable(String pathName, Relation table) throws IOException {

    Path path = Paths.get(pathName + File.separator + table.id());

    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    writeTableMetadata(path.toString() + File.separator + "Metadata.json", table);

    try {
      for (Column column : table.columns()) {
        WRITER_COMPLETION_SERVICE.submit(() -> {
          Path columnPath = path.resolve(column.id());
          writeColumn(columnPath.toString(), column);
          return null;
        });
      }
      for (int i = 0; i < table.columnCount(); i++) {
        Future future = WRITER_COMPLETION_SERVICE.take();
        future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
    WRITER_SERVICE.shutdown();
  }

  private static void writeColumn(String fileName, Column column) {
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
          writeColumn(fileName, (LocalDateColumn) column);
          break;
        case LOCAL_TIME:
          writeColumn(fileName, (LocalTimeColumn) column);
          break;
        case LOCAL_DATE_TIME:
          writeColumn(fileName, (LocalDateTimeColumn) column);
          break;
        case PERIOD:
          writeColumn(fileName, (PeriodColumn) column);
          break;
        case TEXT:
          writeColumn(fileName, (TextColumn) column);
          break;
        case CAT:
          writeColumn(fileName, (CategoryColumn) column);
          break;
        case SHORT_INT:
          writeColumn(fileName, (ShortColumn) column);
          break;
        default:
          throw new RuntimeException("Unhandled column type writing columns");
      }
    } catch (IOException ex) {
      throw new RuntimeException("IOException writing to file");
    }
  }

  public static void writeColumn(String fileName, FloatColumn column) throws IOException {
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

  public static void writeColumn(String fileName, TextColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (String d : column) {
        dos.writeUTF(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  //TODO(lwhite): saveTable the column using dictionary encoding (and integer compression)
  public static void writeColumn(String fileName, CategoryColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      int i = 0;
      for (String d : column) {
        dos.writeUTF(d);
        if (i % FLUSH_AFTER_ITERATIONS == 0) {
          dos.flush();
        }
        i++;
      }
      dos.flush();
    }
  }

  //TODO(lwhite): saveTable the column using integer compression
  public static void writeColumn(String fileName, IntColumn column) throws IOException {
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

  public static void writeColumn(String fileName, ShortColumn column) throws IOException {
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

  //TODO(lwhite): saveTable the column using integer compression
  public static void writeColumn(String fileName, LocalDateColumn column) throws IOException {
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

  public static void writeColumn(String fileName, LocalDateTimeColumn column) throws IOException {
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
  public static void writeColumn(String fileName, LocalTimeColumn column) throws IOException {
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

  //TODO(lwhite): saveTable the column using integer compression
  public static void writeColumn(String fileName, PeriodColumn column) throws IOException {
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
  public static void writeColumn(String fileName, BooleanColumn column) throws IOException {
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
  public static void writeTableMetadata(String fileName, Relation table) throws IOException {
    File myFile = Paths.get(fileName).toFile();
    myFile.createNewFile();
    try (
        FileOutputStream fOut = new FileOutputStream(myFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut)) {
      myOutWriter.append(new TableMetadata(table).toJson());
    }
  }

  /**
   * Reads in a json-formatted file and creates a TableMetadata instance from it. Files are expected to be in
   * the format provided by TableMetadata}
   *
   * @param fileName Expected to be fully specified
   * @throws IOException if the file can not be read
   */
  public static TableMetadata readTableMetadata(String fileName) throws IOException {

    byte[] encoded = Files.readAllBytes(Paths.get(fileName));
    return TableMetadata.fromJson(new String(encoded, StandardCharsets.UTF_8));
  }
}
