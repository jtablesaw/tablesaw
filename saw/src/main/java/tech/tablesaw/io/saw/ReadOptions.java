package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import tech.tablesaw.selection.Selection;

public class ReadOptions {

  private static final int READER_POOL_SIZE = 8;

  private List<String> selectedColumns = new ArrayList<>();
  private int threadPoolSize = READER_POOL_SIZE;
  private Selection selection = null;

  public ReadOptions threadPoolSize(int size) {
    Preconditions.checkArgument(size > 0);
    this.threadPoolSize = size;
    return this;
  }

  public ReadOptions selectedColumns(String... columnNames) {
    this.selectedColumns = Lists.newArrayList(columnNames);
    return this;
  }

  public ReadOptions selectedColumns(List<String> columnNames) {
    this.selectedColumns = columnNames;
    return this;
  }

  public List<String> getSelectedColumns() {
    return selectedColumns;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public Selection getSelection() {
    return selection;
  }
}
