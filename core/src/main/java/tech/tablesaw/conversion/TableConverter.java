package tech.tablesaw.conversion;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NumericAttribute;
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

    private static double[][] doubleMatrix(List<Column<?>> columns) {
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

    private static float[][] floatMatrix(List<Column<?>> columns) {
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

    private static int[][] intMatrix(List<Column<?>> columns) {
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

    public AttributeDataset smileDataset(String responseColName) {
        return smileDataset(
            table.column(responseColName),
            table.columns().stream().filter(c -> !c.name().equals(responseColName)).collect(Collectors.toList()));
    }  

    public AttributeDataset smileDataset(int responseColIndex, int... variablesColIndices) {
        return smileDataset(table.column(responseColIndex), table.columns(variablesColIndices));
    }  

    public AttributeDataset smileDataset(String responseColName, String... variablesColNames) {
        return smileDataset(table.column(responseColName), table.columns(variablesColNames));
    }

    private AttributeDataset smileDataset(Column<?> responseCol, List<Column<?>> variableCols) {
        AttributeDataset data = new AttributeDataset(table.name(),
            variableCols.stream().map(this::colToAttribute).toArray(Attribute[]::new),
            colToAttribute(responseCol));
        for (int i = 0; i < responseCol.size(); i++) {
            final int r = i;
            double[] x = variableCols.stream().mapToDouble(c -> c.getDouble(r)).toArray();
            data.add(x, responseCol.getDouble(r));
        }
        return data;
    }

    /**
     * We convert all columns to NumericAttribute. Smile's AttributeDataset only stores data as double.
     * While Smile defines NominalAttribute and DateAttribute they appear to be little used.
     */
    private Attribute colToAttribute(Column<?> col) {
        return new NumericAttribute(col.name());
    }

}
