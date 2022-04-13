package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;

public class SawWriteOptions {

  private static final int DEFAULT_POOL_SIZE = 10;

  private int threadPoolSize = DEFAULT_POOL_SIZE;
  private CompressionType compressionType = CompressionType.SNAPPY;
  private EncryptionType encryptionType = EncryptionType.NONE;

  public static SawWriteOptions defaultOptions() {
    return new SawWriteOptions();
  }

  public SawWriteOptions threadPoolSize(int size) {
    Preconditions.checkArgument(size > 0);
    this.threadPoolSize = size;
    return this;
  }

  public SawWriteOptions compressionType(CompressionType compressionType) {
    this.compressionType = compressionType;
    return this;
  }

  /**
   * Sets the encryption type for the storage NOTE: This is a placeholder-only, to allow for adding
   * encryption support later without changing the format of the metadata file
   */
  public SawWriteOptions encryptionType(EncryptionType type) {
    this.encryptionType = type;
    return this;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }

  public EncryptionType getEncryptionType() {
    return encryptionType;
  }
}
