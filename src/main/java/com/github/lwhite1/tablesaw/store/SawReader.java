package com.github.lwhite1.tablesaw.store;

import com.github.lwhite1.tablesaw.api.*;
import com.github.lwhite1.tablesaw.columns.Column;
import org.iq80.snappy.SnappyFramedInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

final class SawReader {
  private static final int READER_POOL_SIZE = 4;

  private SawReader() { }

  public static Table readTable(String path) throws IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(READER_POOL_SIZE);

    TableMetadata tableMetadata = readTableMetadata(path + File.separator + TableMetadata.fileName);
    Table table = Table.create(tableMetadata);

    // NB: We do some extra work with the hash map to ensure that the columns are added to the table in original order
    // TODO(lwhite): Not using CPU efficiently. Need to prevent waiting for other threads until all columns are read
    // TODO - continued : Problem seems to be mostly with category columns rebuilding the encoding dictionary

    AtomicReference<Throwable> atomicThrow = new AtomicReference<>();
    List<ColumnMetadata> columnMetadata = tableMetadata.getColumnMetadataList();
    CountDownLatch latch = new CountDownLatch(columnMetadata.size());
    Map<String, Column> columns = new ConcurrentHashMap<>();

    try {
      tableMetadata.getColumnMetadataList()
          .stream()
          .map(cMeta -> {
            String fileName = path + File.separator + cMeta.getId();
            return (Runnable) () -> {
              try {
                Column column = readColumn(fileName, cMeta);
                columns.put(cMeta.getId(), column);
                latch.countDown();
              } catch (Throwable e) {
                e.printStackTrace();

                // capture the first error and fail ASAP
                if (atomicThrow.get() == null) {
                  atomicThrow.set(e);
                  // clear latch and fail fast
                  while (latch.getCount() > 0) latch.countDown();
                }
              }
            };
          }).forEach(executorService::submit);

      latch.await();

      if (atomicThrow.get() != null) throw new RuntimeException(atomicThrow.get());

      for (ColumnMetadata metadata : columnMetadata)
        table.addColumn(columns.get(metadata.getId()));

    } catch (InterruptedException t) {
      throw new RuntimeException(t);
    } finally {
      executorService.shutdownNow();
    }

    return table;
  }

  /**
   * Reads in a json-formatted file and creates a TableMetadata instance from it. Files are expected to be in
   * the format provided by TableMetadata}
   *
   * @param fileName Expected to be fully specified
   * @throws IOException if the file can not be read
   */
  private static TableMetadata readTableMetadata(String fileName) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(fileName));
    String json = new String(encoded, StandardCharsets.UTF_8);
    return TableMetadata.fromJson(json);
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
      case CATEGORY:
        return readCategoryColumn(fileName, columnMetadata);
      case SHORT_INT:
        return readShortColumn(fileName, columnMetadata);
      case LONG_INT:
        return readLongColumn(fileName, columnMetadata);
      default:
        throw new RuntimeException("Unhandled column type writing columns");
    }
  }

  private static FloatColumn readFloatColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

  private static IntColumn readIntColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

  private static ShortColumn readShortColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

  private static LongColumn readLongColumn(String fileName, ColumnMetadata metadata) throws IOException {
    LongColumn ints = new LongColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {
      boolean EOF = false;
      while (!EOF) {
        try {
          ints.add(dis.readLong());
        } catch (EOFException e) {
          EOF = true;
        }
      }
    }
    return ints;
  }

  private static DateColumn readLocalDateColumn(String fileName, ColumnMetadata metadata) throws IOException {
    DateColumn dates = new DateColumn(metadata);
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

  private static DateTimeColumn readLocalDateTimeColumn(String fileName, ColumnMetadata metadata) throws
      IOException {
    DateTimeColumn dates = new DateTimeColumn(metadata);
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

  private static TimeColumn readLocalTimeColumn(String fileName, ColumnMetadata metadata) throws IOException {
    TimeColumn times = new TimeColumn(metadata);
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

  static CategoryColumn readCategoryColumn(String fileName, ColumnMetadata metadata) throws IOException {
    CategoryColumn stringColumn = new CategoryColumn(metadata);
    try (FileInputStream fis = new FileInputStream(fileName);
         SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
         DataInputStream dis = new DataInputStream(sis)) {

      int stringCount = dis.readInt();

      int j = 0;
      while (j < stringCount) {
        stringColumn.dictionaryMap().put(j, dis.readUTF());
        j++;
      }

      int size = metadata.getSize();
      for (int i = 0; i < size; i++) {
        stringColumn.data().add(dis.readInt());
      }
    }
    return stringColumn;
  }

  private static BooleanColumn readBooleanColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

}
