package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;

public class WriteOptions {

  private static final int DEFAULT_POOL_SIZE = 10;

  private int threadPoolSize = DEFAULT_POOL_SIZE;
  private CompressionType compressionType = CompressionType.SNAPPY;

  public static WriteOptions defaultOptions() {
    return new WriteOptions();
  }

  public WriteOptions threadPoolSize(int size) {
    Preconditions.checkArgument(size > 0);
    this.threadPoolSize = size;
    return this;
  }

  public WriteOptions CompressionType(CompressionType compressionType) {
    this.compressionType = compressionType;
    return this;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }
}
