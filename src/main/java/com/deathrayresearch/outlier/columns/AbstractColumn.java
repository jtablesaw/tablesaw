package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.store.ColumnMetadata;

import java.util.UUID;

/**
 *
 */
abstract class AbstractColumn implements Column {

  private final String id = UUID.randomUUID().toString();

  private String name;

  public AbstractColumn(String name) {
    this.name = name;
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

}
