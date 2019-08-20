package tech.tablesaw.io.saw;

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
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static tech.tablesaw.io.saw.StorageManager.*;

@SuppressWarnings("WeakerAccess")
public class SawReader {

    private static final int READER_POOL_SIZE = 4;

    /**
     * Reads a tablesaw table into memory
     *
     * @param path The location of the table. It is interpreted as relative to the working directory if not fully
     *             specified. The path will typically end in ".saw", as in "mytables/nasdaq-2015.saw"
     * @throws RuntimeException wrapping an IOException if the file cannot be read
     */
    public static Table readTable(String path) {

        ExecutorService executorService = Executors.newFixedThreadPool(READER_POOL_SIZE);
        CompletionService<Void> readerCompletionService = new ExecutorCompletionService<>(executorService);

        TableMetadata tableMetadata;
        try {
         tableMetadata= readTableMetadata(path + separator() + "Metadata.json");
        } catch (IOException e) {
            throw new RuntimeException("Error attempting to load saw data", e);
        }

        List<ColumnMetadata> columnMetadata = tableMetadata.getColumnMetadataList();
        Table table = Table.create(tableMetadata.getName());


        // NB: We do some extra work with the hash map to ensure that the columns are returned
        // to the table in original order

        // TODO(lwhite): Not using CPU efficiently. Need to prevent waiting for other threads until all columns are read
        //      Problem seems to be mostly with string columns rebuilding the encoding dictionary

        ConcurrentLinkedQueue<Column> columnList = new ConcurrentLinkedQueue<>();
        Map<String, Column> columns = new HashMap<>();
        try {
            for (ColumnMetadata column : columnMetadata) {
                readerCompletionService.submit(() -> {
                    columnList.add(readColumn(path + separator() + column.getId(), column));
                    return null;
                });
            }
            for (int i = 0; i < columnMetadata.size(); i++) {
                Future<Void> future = readerCompletionService.take();
                future.get();
            }
            for (Column c : columnList) {
                columns.put(c.name(), c);
            }

            for (ColumnMetadata metadata : columnMetadata) {
                table.addColumns(columns.get(metadata.getName()));
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        return table;
    }

    private static Column readColumn(String fileName, ColumnMetadata columnMetadata)
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
//            case STRING:
//                return readStringColumn(fileName, columnMetadata);
            case SHORT:
                return readShortColumn(fileName, columnMetadata);
            case LONG:
                return readLongColumn(fileName, columnMetadata);
            default:
                throw new IllegalStateException("Unhandled column type writing columns: " + typeString);
        }
    }

    private static FloatColumn readFloatColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

    private static DoubleColumn readDoubleColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

    private static IntColumn readIntColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

    private static ShortColumn readShortColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

    private static LongColumn readLongColumn(String fileName, ColumnMetadata metadata) throws IOException {
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

    private static DateColumn readLocalDateColumn(String fileName, ColumnMetadata metadata) throws IOException {
        DateColumn dates = DateColumn.create(metadata.getName());
        try (FileInputStream fis = new FileInputStream(fileName);
             SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
             DataInputStream dis = new DataInputStream(sis)) {
            boolean EOF = false;
            while (!EOF) {
                try {
                    int cell = dis.readInt();
                    dates.appendInternal(cell);
                } catch (EOFException e) {
                    EOF = true;
                }
            }
        }
        return dates;
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

    private static TimeColumn readLocalTimeColumn(String fileName, ColumnMetadata metadata) throws IOException {
        TimeColumn times = TimeColumn.create(metadata.getName());
        try (FileInputStream fis = new FileInputStream(fileName);
             SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
             DataInputStream dis = new DataInputStream(sis)) {
            boolean EOF = false;
            while (!EOF) {
                try {
                    int cell = dis.readInt();
                    times.appendInternal(cell);
                } catch (EOFException e) {
                    EOF = true;
                }
            }
        }
        return times;
    }

/*
    */
/**
     * Reads the encoded StringColumn from the given file and stuffs it into a new StringColumn,
     * saving time by updating the dictionary directly and just writing ints to the column's data
     *//*

    private static StringColumn readStringColumn(String fileName, ColumnMetadata metadata)
            throws IOException {

        StringColumn stringColumn = StringColumn.create(metadata.getName());
        try (FileInputStream fis = new FileInputStream(fileName);
             SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
             DataInputStream dis = new DataInputStream(sis)) {

            int stringCount = dis.readInt();

            int j = 0;
            while (j < stringCount) {
                stringColumn.UnsafePutToDictionary(j, dis.readUTF());
                j++;
            }

            int size = metadata.getSize();
            for (int i = 0; i < size; i++) {
                stringColumn.data().add(dis.readInt());
            }
        }
        return stringColumn;
    }
*/

    private static BooleanColumn readBooleanColumn(String fileName, ColumnMetadata metadata)
            throws IOException {

        BooleanColumn bools = BooleanColumn.create(metadata.getName());
        try (FileInputStream fis = new FileInputStream(fileName);
             SnappyFramedInputStream sis = new SnappyFramedInputStream(fis, true);
             DataInputStream dis = new DataInputStream(sis)) {
            boolean EOF = false;
            while (!EOF) {
                try {
                    boolean cell = dis.readBoolean();
                    bools.append(cell);
                } catch (EOFException e) {
                    EOF = true;
                }
            }
        }
        return bools;
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
        return TableMetadata.fromJson(new String(encoded, StandardCharsets.UTF_8));
    }
}
