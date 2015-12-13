package com.deathrayresearch.outlier.store;

import com.deathrayresearch.outlier.*;
import org.iq80.snappy.SnappyFramedInputStream;
import org.iq80.snappy.SnappyFramedOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 *
 */
public class StorageManager {

  private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

  private static final CompletionService completionService =
      new ExecutorCompletionService<>(executorService);

  /**
   *
   * @param fileName Expected to be fully specified
   * @throws IOException
   */
  public static void readTable(String fileName) throws IOException {

    Table table = null;

    for (Column column : table.getColumns()) {
      readColumn(fileName, column);
    }
  }

  private static void readColumn(String fileName, Column column) {

  }

  public static FloatColumn readFloatColumn(String fileName, String column) throws IOException {
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
      for (Column column : table.getColumns()) {
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
      while(column.hasNext()) {
        float cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  public static void writeColumn(String fileName, IntColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName);
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      while(column.hasNext()) {
        int cell = column.next();
        dos.writeFloat(cell);
      }
      column.reset();
      dos.flush();
    }
  }

  public static void writeColumn(String fileName, BooleanColumn column) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(fileName + "_" + column.name());
         SnappyFramedOutputStream sos = new SnappyFramedOutputStream(fos);
         DataOutputStream dos = new DataOutputStream(sos)) {
      for(int i = 0; i < column.size(); i++) {
        boolean value = column.get(i);
        dos.writeBoolean(value);
      }
      dos.flush();
    }
  }
}
