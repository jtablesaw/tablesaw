package com.deathrayresearch.outlier.store;

import com.deathrayresearch.outlier.columns.BooleanColumn;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.LocalDateTimeColumn;
import com.deathrayresearch.outlier.columns.LocalTimeColumn;
import com.deathrayresearch.outlier.columns.PeriodColumn;
import com.deathrayresearch.outlier.columns.TextColumn;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.iq80.snappy.SnappyFramedInputStream;
import org.iq80.snappy.SnappyFramedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

  private static final CompletionService completionService =
      new ExecutorCompletionService<>(executorService);

  /**
   * @param fileName Expected to be fully specified
   * @throws IOException
   */
  public static void readTable(String fileName) throws IOException {

    Table table = null;

    for (Column column : table.columns()) {
      readColumn(fileName, column.name(), column.type());
    }
  }

  private static void readColumn(String fileName, String columnName, ColumnType columnType)
      throws IOException {

    switch (columnType) {
      case FLOAT:
        readFloatColumn(fileName, columnName);
        break;
      case INTEGER:
        readIntColumn(fileName, columnName);
        break;
      case BOOLEAN:
        readBooleanColumn(fileName, columnName);
        break;
      case LOCAL_DATE:
        readLocalDateColumn(fileName, columnName);
        break;
      case LOCAL_TIME:
        readLocalTimeColumn(fileName, columnName);
        break;
      case PERIOD:
        readPeriodColumn(fileName, columnName);
        break;
      case TEXT:
        readTextColumn(fileName, columnName);
        break;
      case CAT:
        readCategoryColumn(fileName, columnName);
        break;
      default:
        throw new RuntimeException("Unhandled column type writing columns");
    }
  }

  public static FloatColumn readFloatColumn(String fileName, String columnName) throws IOException {
    FloatColumn floats = new FloatColumn(fileName);
    try (FileInputStream fis = new FileInputStream(fileName + "_" + columnName);
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
    floats.compact();
    return floats;
  }

  public static FloatColumn readIntColumn(String fileName, String column) throws IOException {
    FloatColumn floats = new FloatColumn(fileName);
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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
    floats.compact();
    return floats;
  }

  public static LocalDateColumn readLocalDateColumn(String fileName, String column) throws IOException {
    IntArrayList dates = new IntArrayList();
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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
    return LocalDateColumn.create(fileName, dates);
  }

  public static LocalDateTimeColumn readLocalDateTimeColumn(String fileName, String column) throws IOException {
    LongArrayList dates = new LongArrayList();
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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
    return LocalDateTimeColumn.create(fileName, dates);
  }

  public static LocalTimeColumn readLocalTimeColumn(String fileName, String column) throws IOException {
    IntArrayList times = new IntArrayList();
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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
    return LocalTimeColumn.create(fileName, times);
  }

  public static PeriodColumn readPeriodColumn(String fileName, String column) throws IOException {
    IntArrayList packedPeriods = new IntArrayList();
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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
    return PeriodColumn.create(fileName, packedPeriods);
  }

  public static TextColumn readTextColumn(String fileName, String column) throws IOException {
    TextColumn stringColumn = TextColumn.create(column);
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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

  public static CategoryColumn readCategoryColumn(String fileName, String column) throws IOException {
    CategoryColumn stringColumn = CategoryColumn.create(column);
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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

  public static void write(String pathName, Relation table) throws IOException {

    Path path = Paths.get(pathName + File.separator + table.id());
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      for (Column column : table.columns()) {
        completionService.submit(() -> {
          Path columnPath = path.resolve(column.id());
          StorageManager.writeColumn(columnPath.toString(), column);
          return null;
        });
      }

      for (int i = 0; i < table.columnCount(); i++) {
        Future future = completionService.take();
        future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
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
        default:
          throw new RuntimeException("Unhandled column type writing columns");
      }
    } catch (IOException ex) {
      throw new RuntimeException("IOException writing to file");
    }
  }

  public static void writeColumn(String fileName, FloatColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName + "_" + column.name());
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while (column.hasNext()) {
        float cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  public static void writeColumn(String fileName, TextColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos)) {
      while (column.hasNext()) {
        String cell = column.next();
        sos.write(cell.getBytes(StandardCharsets.UTF_8));
      }
      column.reset();
      sos.flush();
    }
  }

  //TODO(lwhite): write the column using dictionary encoding (and integer compression)
  public static void writeColumn(String fileName, CategoryColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos)) {
      while (column.hasNext()) {
        String cell = column.next();
        sos.write(cell.getBytes(StandardCharsets.UTF_8));
      }
      column.reset();
      sos.flush();
    }
  }

  //TODO(lwhite): write the column using integer compression
  public static void writeColumn(String fileName, IntColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while (column.hasNext()) {
        int cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  //TODO(lwhite): write the column using integer compression
  public static void writeColumn(String fileName, LocalDateColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while (column.hasNext()) {
        int cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  public static void writeColumn(String fileName, LocalDateTimeColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while (column.hasNext()) {
        long cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  //TODO(lwhite): write the column using integer compression
  public static void writeColumn(String fileName, LocalTimeColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while (column.hasNext()) {
        int cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  //TODO(lwhite): write the column using integer compression
  public static void writeColumn(String fileName, PeriodColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while (column.hasNext()) {
        int cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  //TODO(lwhite): write the column using compressed bitmap
  public static void writeColumn(String fileName, BooleanColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName + "_" + column.name());
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      for (int i = 0; i < column.size(); i++) {
        boolean value = column.get(i);
        dos.writeBoolean(value);
      }
      dos.flush();
    }
  }

  public static BooleanColumn readBooleanColumn(String fileName, String column) throws IOException {
    BooleanArrayList bools = new BooleanArrayList();
    try (FileInputStream fis = new FileInputStream(fileName + "_" + column);
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
    return BooleanColumn.create(fileName, bools);
  }

  /**
   * Writes out a json-formatted representation of the given {@code table}'s metadata to the given {@code file}
   *
   * @param fileName Expected to be fully specified
   * @throws IOException if the file can not be read
   */
  public static void writeTableMetadata(String fileName, Table table) throws IOException {
    Files.write(Paths.get(fileName), new TableMetadata(table).toString().getBytes());
  }

  /**
   * Reads in a json-formatted file and creates a TableMetadata instance from it. Files are expected to be in
   * the format provided by TableMetadata}
   *
   * @param fileName Expected to be fully specified
   * @throws IOException if the file can not be read
   */
  public static TableMetadata readTableMetadata(String fileName) throws IOException {

    try (FileReader fileReader = new FileReader(fileName)) {
      return TableMetadata.fromJson(fileReader.toString());
    }
  }
}
