package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.api.TimeColumn;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalTime;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalTime;

/**
 * An index for four-byte integer and Date columns
 */
public class TimeIndex {

  private final IntIndex index;

  public TimeIndex(TimeColumn column) {
    index = new IntIndex(column);
  }

  /**
   * Returns a bitmap containing row numbers of all cells matching the given int
   *
   * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
   */
  public Selection get(LocalTime value) {
    return index.get(PackedLocalTime.pack(value));
  }

  public Selection atLeast(LocalTime value) {
    return index.atLeast(PackedLocalTime.pack(value));
  }

  public Selection greaterThan(LocalTime value) {
    return index.greaterThan(PackedLocalTime.pack(value));
  }

  public Selection atMost(LocalTime value) {
    return index.atMost(PackedLocalTime.pack(value));
  }

  public Selection lessThan(LocalTime value) {
    return index.lessThan(PackedLocalTime.pack(value));
  }
}