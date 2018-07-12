package tech.tablesaw.conversion;

import java.util.List;

import com.google.common.base.Preconditions;

import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

public class TableConverter {

  private final Relation table;
  
  public TableConverter(Relation table) {
    this.table = table;
  }

  public double[][] doubleMatrix() {
    return doubleMatrix(table.columns());
  }

  public double[][] doubleMatrix(int... columnIndicies) {
    return doubleMatrix(table.columns(columnIndicies));
  }

  public double[][] doubleMatrix(String... columnNames) {
    return doubleMatrix(table.columns(columnNames));
  }

  public float[][] floatMatrix() {
    return floatMatrix(table.columns());
  }

  public float[][] floatMatrix(int... columnIndicies) {
    return floatMatrix(table.columns(columnIndicies));
  }

  public float[][] floatMatrix(String... columnNames) {
    return floatMatrix(table.columns(columnNames));
  }
  
  public int[][] intMatrix() {
    return intMatrix(table.columns());
  }

  public int[][] intMatrix(int... columnIndicies) {
    return intMatrix(table.columns(columnIndicies));
  }

  public int[][] intMatrix(String... columnNames) {
    return intMatrix(table.columns(columnNames));
  }

  private static double[][] doubleMatrix(List<Column> columns) {
    Preconditions.checkArgument(columns.size() >= 1);
    int obs = columns.get(0).size();
    double[][] allVals = new double[obs][columns.size()];

    for (int r = 0; r < obs; r++) {
      for (int c = 0; c < columns.size(); c++) {
        allVals[r][c] = columns.get(c).getDouble(r);
      }
    }
    return allVals;
  }

  private static float[][] floatMatrix(List<Column> columns) {
    Preconditions.checkArgument(columns.size() >= 1);
    int obs = columns.get(0).size();
    float[][] allVals = new float[obs][columns.size()];

    for (int r = 0; r < obs; r++) {
      for (int c = 0; c < columns.size(); c++) {
        allVals[r][c] = (float) columns.get(c).getDouble(r);
      }
    }
    return allVals;
  }

  private static int[][] intMatrix(List<Column> columns) {
    Preconditions.checkArgument(columns.size() >= 1);
    int obs = columns.get(0).size();
    int[][] allVals = new int[obs][columns.size()];

    for (int r = 0; r < obs; r++) {
      for (int c = 0; c < columns.size(); c++) {
        allVals[r][c] = (int) columns.get(c).getDouble(r);
      }
    }
    return allVals;
  }

}
