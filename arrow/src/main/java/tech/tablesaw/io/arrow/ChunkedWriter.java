package tech.tablesaw.io.arrow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.pojo.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkedWriter<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChunkedWriter.class);

  private final int chunkSize;
  private final Vectorizer<T> vectorizer;

  public ChunkedWriter(int chunkSize, Vectorizer<T> vectorizer) {
    this.chunkSize = chunkSize;
    this.vectorizer = vectorizer;
  }

  public void write(File file, T[] values, Schema schema) throws IOException {
    DictionaryProvider.MapDictionaryProvider dictProvider =
        new DictionaryProvider.MapDictionaryProvider();

    try (RootAllocator allocator = new RootAllocator();
        VectorSchemaRoot schemaRoot = VectorSchemaRoot.create(schema, allocator);
        FileOutputStream fd = new FileOutputStream(file);
        ArrowFileWriter fileWriter =
            new ArrowFileWriter(schemaRoot, dictProvider, fd.getChannel())) {

      LOGGER.info("Start writing");
      fileWriter.start();

      int index = 0;
      while (index < values.length) {
        schemaRoot.allocateNew();
        int chunkIndex = 0;
        while (chunkIndex < chunkSize && index + chunkIndex < values.length) {
          vectorizer.vectorize(values[index + chunkIndex], chunkIndex, schemaRoot);
          chunkIndex++;
        }
        schemaRoot.setRowCount(chunkIndex);
        LOGGER.info("Filled chunk with {} items; {} items written", chunkIndex, index + chunkIndex);
        fileWriter.writeBatch();
        LOGGER.info("Chunk written");

        index += chunkIndex;
        schemaRoot.clear();
      }

      LOGGER.info("Writing done");
      fileWriter.end();
    }
  }

  @FunctionalInterface
  public interface Vectorizer<T> {
    void vectorize(T value, int index, VectorSchemaRoot batch);
  }
}
