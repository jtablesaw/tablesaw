package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** A request to load a Table from a Saw file */
public class SawReadRequest {

  private static final int READER_POOL_SIZE = 8;

  private final File sawFile;
  private final int threadPoolSize;
  private final List<String> selectedColumns;

  private SawReadRequest(Builder builder) {
    this.sawFile = builder.sawFile;
    this.threadPoolSize = builder.threadPoolSize;
    this.selectedColumns = builder.selectedColumns;
  }

  public File getSawFile() {
    return sawFile;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public List<String> getSelectedColumns() {
    return selectedColumns;
  }

  public static class Builder {
    private final File sawFile;
    private int threadPoolSize = READER_POOL_SIZE;
    private List<String> selectedColumns = new ArrayList<>();

    private Builder(File sawFile) {
      this.sawFile = sawFile;
    }

    public Builder(String path) {
      Path sawPath = Paths.get(path);
      this.sawFile = sawPath.toFile();
    }

    public Builder threadPoolSize(int size) {
      Preconditions.checkArgument(size > 0);
      this.threadPoolSize = size;
      return this;
    }

    public Builder selectedColumns(String... columnNames) {
      this.selectedColumns = Lists.newArrayList(columnNames);
      return this;
    }

    public Builder selectedColumns(List<String> columnNames) {
      this.selectedColumns = columnNames;
      return this;
    }

    public SawReadRequest build() {
      return new SawReadRequest(this);
    }
  }
}
