package tech.tablesaw.conversion;

import com.google.common.base.Preconditions;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NumericAttribute;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.table.Relation;

import java.util.List;
import java.util.stream.Collectors;

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
        Preconditions.checkArgument(numberColumns.size() >= 1);
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
        Preconditions.checkArgument(numberColumns.size() >= 1);
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
        Preconditions.checkArgument(numberColumns.size() >= 1);
        int obs = numberColumns.get(0).size();
        int[][] allVals = new int[obs][numberColumns.size()];

        for (int r = 0; r < obs; r++) {
            for (int c = 0; c < numberColumns.size(); c++) {
                allVals[r][c] = (int) numberColumns.get(c).getDouble(r);
            }
        }
        return allVals;
    }

    public AttributeDataset smileDataset(String responseColName) {
        return smileDataset(
            table.numberColumn(responseColName),
            table.numericColumns().stream().filter(c -> !c.name().equals(responseColName)).collect(Collectors.toList()));
    }  

    public AttributeDataset smileDataset(int responseColIndex, int... variablesColIndices) {
        return smileDataset(table.numberColumn(responseColIndex), table.numericColumns(variablesColIndices));
    }  

    public AttributeDataset smileDataset(String responseColName, String... variablesColNames) {
        return smileDataset(table.numberColumn(responseColName), table.numericColumns(variablesColNames));
    }

    private AttributeDataset smileDataset(NumericColumn<?> responseCol, List<NumericColumn<?>> variableCols) {
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
     * We convert all numberColumns to NumericAttribute. Smile's AttributeDataset only stores data as double.
     * While Smile defines NominalAttribute and DateAttribute they appear to be little used.
     */
    private Attribute colToAttribute(NumericColumn<?> col) {
        return new NumericAttribute(col.name());
    }

}
