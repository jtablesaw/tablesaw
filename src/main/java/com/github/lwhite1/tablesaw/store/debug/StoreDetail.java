package com.github.lwhite1.tablesaw.store.debug;

import com.google.common.collect.ImmutableList;

public final class StoreDetail {
  private final int columnCount;
  private final int rowCount;
  private final ImmutableList<ColumnDetail> columnDetails;
  private final long totalSizeInBytes;

  public StoreDetail(int columnCount, int rowCount, ImmutableList<ColumnDetail> columnDetails, long totalSizeInBytes) {
    this.columnCount = columnCount;
    this.rowCount = rowCount;
    this.columnDetails = columnDetails;
    this.totalSizeInBytes = totalSizeInBytes;
  }

  public int columnCount() { return columnCount; }

  public int rowCount() { return rowCount; }

  public ImmutableList<ColumnDetail> columnDetails() { return columnDetails; }

  public long totalSizeInBytes() { return totalSizeInBytes; }
}
