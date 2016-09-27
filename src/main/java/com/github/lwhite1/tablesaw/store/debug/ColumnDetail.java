package com.github.lwhite1.tablesaw.store.debug;

import com.github.lwhite1.tablesaw.api.ColumnType;

public final class ColumnDetail {
  private final String name;
  private final ColumnType columnType;
  private final long sizeinBytes;

  public ColumnDetail(String name, ColumnType columnType, long sizeinBytes) {
    this.name = name;
    this.columnType = columnType;
    this.sizeinBytes = sizeinBytes;
  }

  public String name() { return name; }

  public ColumnType columnType() { return columnType; }

  public long sizeInBytes() { return sizeinBytes; }
}
