package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;

public class WriteOptions {

  private static final int DEFAULT_POOL_SIZE = 10;

  int threadPoolSize = DEFAULT_POOL_SIZE;

  public WriteOptions threadPoolSize(int size) {
    Preconditions.checkArgument(size > 0);
    this.threadPoolSize = size;
    return this;
  }
}
