package tech.tablesaw.conversion;

import com.google.common.base.Preconditions;
import java.util.List;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.table.Relation;

/**
 * A tool for converting a Table or other Relation to a two-dimensional array of numeric primitives.
 */
public class TableConverter {

  private final Relation table;

  public TableConverter(Relation table) {
    this.table = table;
  }

  public double[][] doubleMatrix() {
    return doubleMatrix(table.numericColumns());
  }

  public double[][] doubleMatrix(int... columnIndicies) {
    return doubleMatrix(table.numericColumns(columnIndicies));
  }

  public double[][] doubleMatrix(String... columnNames) {
    return doubleMatrix(table.numericColumns(columnNames));
  }

  public float[][] floatMatrix() {
    return floatMatrix(table.numericColumns());
  }

  public float[][] floatMatrix(int... columnIndicies) {
    return floatMatrix(table.numericColumns(columnIndicies));
  }

  public float[][] floatMatrix(String... columnNames) {
    return floatMatrix(table.numericColumns(columnNames));
  }

  public int[][] intMatrix() {
    return intMatrix(table.numericColumns());
  }

  public int[][] intMatrix(int... columnIndicies) {
    return intMatrix(table.numericColumns(columnIndicies));
  }

  public int[][] intMatrix(String... columnNames) {
    return intMatrix(table.numericColumns(columnNames));
  }

  private static double[][] doubleMatrix(List<NumericColumn<?>> numberColumns) {
    Preconditions.checkArgument(!numberColumns.isEmpty());
    int obs = numberColumns.get(0).size();
    double[][] allVals = new double[obs][numberColumns.size()];

    for (int r = 0; r < obs; r++) {
      for (int c = 0; c < numberColumns.size(); c++) {
        allVals[r][c] = numberColumns.get(c).getDouble(r);
      }
    }
    return allVals;
  }

  private static float[][] floatMatrix(List<NumericColumn<?>> numberColumns) {
    Preconditions.checkArgument(!numberColumns.isEmpty());
    int obs = numberColumns.get(0).size();
    float[][] allVals = new float[obs][numberColumns.size()];

    for (int r = 0; r < obs; r++) {
      for (int c = 0; c < numberColumns.size(); c++) {
        allVals[r][c] = (float) numberColumns.get(c).getDouble(r);
      }
    }
    return allVals;
  }

  private static int[][] intMatrix(List<NumericColumn<?>> numberColumns) {
    Preconditions.checkArgument(!numberColumns.isEmpty());
    int obs = numberColumns.get(0).size();
    int[][] allVals = new int[obs][numberColumns.size()];

    for (int r = 0; r < obs; r++) {
      for (int c = 0; c < numberColumns.size(); c++) {
        allVals[r][c] = (int) numberColumns.get(c).getDouble(r);
      }
    }
    return allVals;
  }
}
