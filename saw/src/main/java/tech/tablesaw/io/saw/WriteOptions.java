package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;

public class WriteOptions {

  int threadPoolSize;

  public WriteOptions threadPoolSize(int size) {
    Preconditions.checkArgument(size > 0);
    this.threadPoolSize = size;
    return this;
  }
}
