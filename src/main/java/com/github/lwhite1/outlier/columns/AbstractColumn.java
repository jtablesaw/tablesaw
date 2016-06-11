package com.github.lwhite1.outlier.columns;

import com.github.lwhite1.outlier.api.ColumnType;
import com.github.lwhite1.outlier.store.ColumnMetadata;

import java.util.UUID;

/**
 *
 */
abstract class AbstractColumn implements Column {

  private String id;

  private String name;

  private String comment;

  private ColumnType columnType;

  public AbstractColumn(String name) {
    this.name = name;
    this.id = UUID.randomUUID().toString();
  }

  public AbstractColumn(ColumnMetadata metadata) {
    this.name = metadata.getName();
    this.id = metadata.getId();
  }

  public String name() {
    return name;
  }

  public String id() {
    return id;
  }

  @Override
  public String metadata() {
    return new ColumnMetadata(this).toJson();
  }

  public void setName(String name) {
    this.name = name;
  }

  public abstract void addCell(String stringvalue);

  @Override
  public String comment() {
    return comment;
  }

  @Override
  public ColumnType type() {
    return columnType;
  }

  protected void setColumnType(ColumnType columnType) {
    this.columnType = columnType;
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }
}