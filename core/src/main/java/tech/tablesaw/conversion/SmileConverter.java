package tech.tablesaw.conversion;

import java.util.List;
import java.util.stream.Collectors;

import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NumericAttribute;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.table.Relation;

public class SmileConverter {

    private final Relation table;

    public SmileConverter(Relation table) {
        this.table = table;
    }

    public AttributeDataset dataset(String responseColName) {
        return dataset(
            table.numberColumn(responseColName),
            table.numericColumns().stream().filter(c -> !c.name().equals(responseColName)).collect(Collectors.toList()));
    }  

    public AttributeDataset dataset(int responseColIndex, int... variablesColIndices) {
        return dataset(table.numberColumn(responseColIndex), table.numericColumns(variablesColIndices));
    }  

    public AttributeDataset dataset(String responseColName, String... variablesColNames) {
        return dataset(table.numberColumn(responseColName), table.numericColumns(variablesColNames));
    }

    private AttributeDataset dataset(NumericColumn<?> responseCol, List<NumericColumn<?>> variableCols) {
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
