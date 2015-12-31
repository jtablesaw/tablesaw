package com.deathrayresearch.outlier;

import com.google.gson.Gson;

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
