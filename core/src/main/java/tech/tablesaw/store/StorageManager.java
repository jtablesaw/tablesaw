/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.store;

import com.google.common.annotations.VisibleForTesting;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * A controller for reading and writing data in Tablesaw's own compressed, column-oriented file format
 */
public class StorageManager {

    private static final int FLUSH_AFTER_ITERATIONS = 10_000;

    private static final String FILE_EXTENSION = "saw";
    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern SEPARATOR_PATTERN = Pattern.compile(Pattern.quote(separator()));

    private static final int READER_POOL_SIZE = 4;

    static String separator() {
        FileSystem fileSystem = FileSystems.getDefault();
        return fileSystem.getSeparator();
    }

    /**
     * Reads a tablesaw table into memory
     *
     * @param path The location of the table. It is interpreted as relative to the working directory if not fully
     *             specified. The path will typically end in ".saw", as in "mytables/nasdaq-2015.saw"
     * @throws IOException if the file cannot be read
     */
    public static Table readTable(String path) throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(READER_POOL_SIZE);
        CompletionService<Void> readerCompletionService = new ExecutorCompletionService<>(executorService);

        TableMetadata tableMetadata = readTableMetadata(path + separator() + "Metadata.json");
        List<ColumnMetadata> columnMetadata = tableMetadata.getColumnMetadataList();
        Table table = Table.create(tableMetadata);

        // NB: We do some extra work with the hash map to ensure that the columns are added to the table in original
        // order
        // TODO(lwhite): Not using CPU efficiently. Need to prevent waiting for other threads until all columns are read
        // TODO - continued : Problem seems to be mostly with category columns rebuilding the encoding dictionary
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
                columns.put(c.id(), c);
            }

            for (ColumnMetadata metadata : columnMetadata) {
                String id = metadata.getId();
                table.addColumn(columns.get(id));
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        return table;
    }

    private static Column readColumn(String fileName, ColumnMetadata columnMetadata)
            throws IOException {

        switch (columnMetadata.getType()) {
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
            case CATEGORY:
                return readCategoryColumn(fileName, columnMetadata);
            case SHORT_INT:
                return readShortColumn(fileName, columnMetadata);
            case LONG_INT:
                return readLongColumn(fileName, columnMetadata);
            default:
                throw new IllegalStateException("Unhandled column type writing columns");
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
                    floats.append(cell);
                } catch (EOFException e) {
                    EOF = true;
                }
            }
        }
        return floats;
    }

    private static DoubleColumn readDoubleColumn(String fileName, ColumnMetadata metadata) throws IOException {
      DoubleColumn doubles = new DoubleColumn(metadata);
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
        IntColumn ints = new IntColumn(metadata);
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
        ShortColumn ints = new ShortColumn(metadata);
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
        LongColumn ints = new LongColumn(metadata);
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
        DateColumn dates = new DateColumn(metadata);
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
                    dates.appendInternal(cell);
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
                    times.appendInternal(cell);
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
                    bools.append(cell);
                } catch (EOFException e) {
                    EOF = true;
                }
            }
        }
        return bools;
    }

    /**
     * Saves the data from the given table in the location specified by folderName. Within that folder each table has
     * its own sub-folder, whose name is based on the name of the table.
     * <p>
     * NOTE: If you store a table with the same name in the same folder. The data in that folder will be over-written.
     * <p>
     * The storage format is the tablesaw compressed column-oriented format, which consists of a set of file in a
     * folder.
     * The name of the folder is based on the name of the table.
     *
     * @param folderName The location of the table (for example: "mytables")
     * @param table      The table to be saved
     * @return The path and name of the table
     * @throws IOException IOException if the file can not be read
     */
    public static String saveTable(String folderName, Relation table) throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletionService<Void> writerCompletionService = new ExecutorCompletionService<>(executorService);

        String name = table.name();
        name = WHITE_SPACE_PATTERN.matcher(name).replaceAll(""); // remove whitespace from the table name
        name = SEPARATOR_PATTERN.matcher(name).replaceAll("_"); // remove path separators from the table name

        String storageFolder = folderName + separator() + name + '.' + FILE_EXTENSION;

        Path path = Paths.get(storageFolder);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeTableMetadata(path.toString() + separator() + "Metadata.json", table);

        try {
            for (Column column : table.columns()) {
                writerCompletionService.submit(() -> {
                    Path columnPath = path.resolve(column.id());
                    writeColumn(columnPath.toString(), column);
                    return null;
                });
            }
            for (int i = 0; i < table.columnCount(); i++) {
                Future<Void> future = writerCompletionService.take();
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        return storageFolder;
    }

    private static void writeColumn(String fileName, Column column) {
        try {
            switch (column.type()) {
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
            throw new RuntimeException("IOException writing to file");
        }
    }

    @VisibleForTesting
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

    static void writeColumn(String fileName, DoubleColumn column) throws IOException {
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
     * Writes out the values of the category column encoded as ints to minimize the time required for subsequent reads
     * <p>
     * The files are written Strings first, then the ints that encode them so they can be read in the opposite order
     *
     * @throws IOException IOException if the file can not be read
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
        File myFile = Paths.get(fileName).toFile();
        myFile.createNewFile();
        try (FileOutputStream fOut = new FileOutputStream(myFile);
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
    private static TableMetadata readTableMetadata(String fileName) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return TableMetadata.fromJson(new String(encoded, StandardCharsets.UTF_8));
    }
}
