package com.deathrayresearch.outlier.lang;

import com.deathrayresearch.outlier.Column;
import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.filter.Filter;
import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class Context {

  static Relation currentTable;
  static HashMap<String, Object> variables = new HashMap<>();

  static void load(String fileName) {

  }

  static void use(Relation t) {
    currentTable = t;
  }

  static void use(String tableName) {
    System.out.println("using table " + tableName);
    Object o = variables.get(tableName);
    if (o != null) {
      currentTable = (Relation) o;
    }
  }

  static View selectAll() {
    return new View(currentTable);
  }

  static View selectIf(Filter filter) {
    return new View(currentTable);
  }

  static View selectIf(List<Column> columnList, Filter filter) {
    return new View(currentTable);
  }

  static View select(Filter filter) {
    return new View(currentTable);
  }

  static String getCell(String tableName, int row, int column){
    return null;
  }

  public static Relation getCurrentTable() {
    return currentTable;
  }

  public static void setCurrentTable(Relation currentTable) {
    Context.currentTable = currentTable;
  }

  public static HashMap<String, Object> getVariables() {
    return variables;
  }
}
