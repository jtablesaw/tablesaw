package com.deathrayresearch.outlier.app.model;

import com.deathrayresearch.outlier.columns.ColumnType;
import com.google.common.base.MoreObjects;

/**
 *
 */
public class CsvFile {

  private String path;
  private ColumnType[] columnTypes;
  private boolean includesHeader;

  public CsvFile(String path, ColumnType[] columnTypes, boolean includesHeader) {
    this.path = path;
    this.columnTypes = columnTypes;
    this.includesHeader = includesHeader;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ColumnType[] getColumnTypes() {
    return columnTypes;
  }

  public void setColumnTypes(ColumnType[] columnTypes) {
    this.columnTypes = columnTypes;
  }

  public boolean isIncludesHeader() {
    return includesHeader;
  }

  public void setIncludesHeader(boolean includesHeader) {
    this.includesHeader = includesHeader;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("path", path)
        .add("columnTypes", columnTypes)
        .add("includesHeader", includesHeader)
        .toString();
  }
}
