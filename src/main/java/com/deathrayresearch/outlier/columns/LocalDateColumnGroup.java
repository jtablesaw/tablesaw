package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.splitter.LocalDateSplitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A group of columns derived from a single base column, each containing a subset of the original values
 */
public class LocalDateColumnGroup extends ColumnGroup {

  private LocalDateColumn original;
  private HashMap<String, LocalDateColumn> subColumns;

  public LocalDateColumnGroup(LocalDateColumn original, LocalDateSplitter splitter) {
    this.original = original;
    subColumns = split(splitter);
  }

  public String name() {
    return original.name();
  }

  public ColumnType type() {
    return original.type();
  }

  public Column getOriginal() {
    return original;
  }

  /**
   * Returns the number of groups();
   */
  @Override
  public int groups() {
    return subColumns.size();
  }

  public List<LocalDateColumn> getSubColumns() {
    return new ArrayList<>(subColumns.values());
  }

  private HashMap<String, LocalDateColumn> split(LocalDateSplitter splitter) {
    HashMap<String, LocalDateColumn> columns = new HashMap<>();
    LocalDateColumn c;
    for (int row = 0; row < original.size(); row++) {
      String key = splitter.groupKey(original.getInt(row));
      if (columns.containsKey(key)) {
        c = columns.get(key);
      } else {
        c = original.emptyCopy();
        columns.put(key, c);
      }
      c.add(original.getInt(row));
    }
    return columns;
  }
}
