package tech.tablesaw.splitting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;

import java.util.List;
import java.util.Map;

/**
 * Splits a table into a list of table, such that for every unique value in column ColumnReference
 * in the input table, there is one output table containing all such rows
 */

public class Splitter {

    private final ColumnReference columnReference;

    private final Classification classification;

    public Splitter(ColumnReference columnReference, Classification classification) {
        this.columnReference = columnReference;
        this.classification = classification;
    }

    public ColumnReference getColumnReference() {
        return columnReference;
    }

    public List<Table> split(Table table) {

        Column column = table.column(columnReference.getColumnName());
        Map<Object, Table> tableMap = Maps.newHashMap();

        Row row = new Row(table);
        for (int rowNumber : table.rows()) {
            row.at(rowNumber);
            String newKey = String.valueOf(classification.cut(row, column));
            if (!tableMap.containsKey(newKey)) {
                Table newTable = table.emptyCopy();
                newTable.setName(newKey);
                tableMap.put(newKey, newTable);
            }
            tableMap.get(newKey).addRow(row);
        }
        return Lists.newArrayList(tableMap.values());
    }
}
