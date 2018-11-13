package tech.tablesaw.conversion;

import java.util.List;
import java.util.stream.Collectors;

import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.NumericAttribute;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.table.Relation;

public class SmileConverter {

    private final Relation table;

    public SmileConverter(Relation table) {
        this.table = table;
    }

    /**
     * Returns a dataset where the response column is numeric. E.g. to be used for a regression
     */
    public AttributeDataset numericDataset(String responseColName) {
        return dataset(
            table.numberColumn(responseColName),
            AttributeType.NUMERIC,
            table.numericColumns().stream().filter(c -> !c.name().equals(responseColName)).collect(Collectors.toList()));
    }  

    /**
     * Returns a dataset where the response column is numeric. E.g. to be used for a regression
     */
    public AttributeDataset numericDataset(int responseColIndex, int... variablesColIndices) {
        return dataset(table.numberColumn(responseColIndex), AttributeType.NUMERIC, table.numericColumns(variablesColIndices));
    }  

    /**
     * Returns a dataset where the response column is numeric. E.g. to be used for a regression
     */
    public AttributeDataset numericDataset(String responseColName, String... variablesColNames) {
        return dataset(table.numberColumn(responseColName), AttributeType.NUMERIC, table.numericColumns(variablesColNames));
    }

    /**
     * Returns a dataset where the response column is nominal. E.g. to be used for a classification
     */
    public AttributeDataset nominalDataset(String responseColName) {
        return dataset(
            table.numberColumn(responseColName),
            AttributeType.NOMINAL,
            table.numericColumns().stream().filter(c -> !c.name().equals(responseColName)).collect(Collectors.toList()));
    }  

    /**
     * Returns a dataset where the response column is nominal. E.g. to be used for a classification
     */
    public AttributeDataset nominalDataset(int responseColIndex, int... variablesColIndices) {
        return dataset(table.numberColumn(responseColIndex), AttributeType.NOMINAL, table.numericColumns(variablesColIndices));
    }  

    /**
     * Returns a dataset where the response column is nominal. E.g. to be used for a classification
     */
    public AttributeDataset nominalDataset(String responseColName, String... variablesColNames) {
        return dataset(table.numberColumn(responseColName), AttributeType.NOMINAL, table.numericColumns(variablesColNames));
    }

    private AttributeDataset dataset(NumericColumn<?> responseCol, AttributeType type, List<NumericColumn<?>> variableCols) {
	Attribute responseAttribute = type == AttributeType.NOMINAL
		? new NominalAttribute(responseCol.name()) : new NumericAttribute(responseCol.name());
        AttributeDataset data = new AttributeDataset(table.name(),
            variableCols.stream().map(col -> new NumericAttribute(col.name())).toArray(Attribute[]::new),
            responseAttribute);
        for (int i = 0; i < responseCol.size(); i++) {
            final int r = i;
            double[] x = variableCols.stream().mapToDouble(c -> c.getDouble(r)).toArray();
            data.add(x, responseCol.getDouble(r));
        }
        return data;
    }

    private static enum AttributeType {
	NUMERIC,
	NOMINAL
    }

}
