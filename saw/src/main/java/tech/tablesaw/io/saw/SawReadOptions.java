package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifies options for reading a table from Saw storage
 *
 * <p>Note: There is no CompressionType option for reading. The CompressionType is read from the Saw
 * metadata file as it must match what was used when the data was written
 */
public class SawReadOptions {

  private static final int READER_POOL_SIZE = 8;

  private List<String> selectedColumns = new ArrayList<>();
  private int threadPoolSize = READER_POOL_SIZE;

  public static SawReadOptions defaultOptions() {
    return new SawReadOptions();
  }

  public SawReadOptions threadPoolSize(int size) {
    Preconditions.checkArgument(size > 0);
    this.threadPoolSize = size;
    return this;
  }

  public SawReadOptions selectedColumns(String... columnNames) {
    this.selectedColumns = Lists.newArrayList(columnNames);
    return this;
  }

  public SawReadOptions selectedColumns(List<String> columnNames) {
    this.selectedColumns = columnNames;
    return this;
  }

  public List<String> getSelectedColumns() {
    return selectedColumns;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }
}
