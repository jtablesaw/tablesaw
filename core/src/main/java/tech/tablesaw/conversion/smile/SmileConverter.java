package tech.tablesaw.conversion.smile;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.NumericAttribute;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
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
        return dataset(table.numberColumn(responseColIndex), AttributeType.NUMERIC, table.columns(variablesColIndices));
    }  

    /**
     * Returns a dataset where the response column is numeric. E.g. to be used for a regression
     */
    public AttributeDataset numericDataset(String responseColName, String... variablesColNames) {
        return dataset(table.numberColumn(responseColName), AttributeType.NUMERIC, table.columns(variablesColNames));
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
        return dataset(table.numberColumn(responseColIndex), AttributeType.NOMINAL, table.columns(variablesColIndices));
    }  

    /**
     * Returns a dataset where the response column is nominal. E.g. to be used for a classification
     */
    public AttributeDataset nominalDataset(String responseColName, String... variablesColNames) {
        return dataset(table.numberColumn(responseColName), AttributeType.NOMINAL, table.columns(variablesColNames));
    }

    private AttributeDataset dataset(NumericColumn<?> responseCol, AttributeType type, List<Column<?>> variableCols) {
        List<Column<?>> convertedVariableCols = variableCols.stream()
            .map(col -> col.type() == ColumnType.STRING ? col : table.nCol(col.name()))
            .collect(Collectors.toList());
        Attribute responseAttribute = type == AttributeType.NOMINAL
            ? colAsNominalAttribute(responseCol) : new NumericAttribute(responseCol.name());
        AttributeDataset dataset = new AttributeDataset(table.name(),
            convertedVariableCols.stream().map(col -> colAsAttribute(col)).toArray(Attribute[]::new),
            responseAttribute);
        for (int i = 0; i < responseCol.size(); i++) {
            final int r = i;
            double[] x = IntStream.range(0, convertedVariableCols.size())
                .mapToDouble(c -> getDouble(convertedVariableCols.get(c), dataset.attributes()[c], r))
                .toArray();
            dataset.add(x, responseCol.getDouble(r));
        }
        return dataset;
    }
    
    private double getDouble(Column<?> col, Attribute attr, int r) {
        if (col.type() == ColumnType.STRING) {
            String value = ((StringColumn) col).get(r);
            try {
                return attr.valueOf(value);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Error converting " + value + " to nominal", e);
            }
        }
        if (col instanceof NumericColumn) {
            return ((NumericColumn<?>) col).getDouble(r);
        }
        throw new IllegalStateException("Error converting " + col.type() + " column " + col.name() + " to Smile");
    }

    private Attribute colAsAttribute(Column<?> col) {
        return col.type() == ColumnType.STRING ? colAsNominalAttribute(col) : new NumericAttribute(col.name());
    }

    private NominalAttribute colAsNominalAttribute(Column<?> col) {
        Column<?> unique = col.unique();
        return new NominalAttribute(col.name(),
            unique.mapInto(o -> o.toString(), StringColumn.create(col.name(), unique.size())).asObjectArray());
    }

    private static enum AttributeType {
        NUMERIC,
        NOMINAL
    }

}
