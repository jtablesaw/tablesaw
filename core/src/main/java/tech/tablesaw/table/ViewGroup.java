/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.table;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.NumericSummaryTable;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

import java.nio.ByteBuffer;
import java.util.*;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class ViewGroup implements Iterable<TemporaryView> {

    private static final String SPLIT_STRING = "~~~";
    private static final Splitter SPLITTER = Splitter.on(SPLIT_STRING);


    private final Table sourceTable;

    private final List<TemporaryView> subTables = new ArrayList<>();

    private String[] splitColumnNames;

    public ViewGroup(Table original, CategoricalColumn... columns) {
        splitColumnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            splitColumnNames[i] = columns[i].name();
        }
        this.sourceTable = original.sortOn(splitColumnNames);
        splitOn(splitColumnNames);
    }

    public ViewGroup(Table original, String subTableNameTemplate, int step) {
        this.sourceTable = original;
        List<Selection> selections = new ArrayList<>();
        for (int i = 0; i < original.rowCount() - step; i+= step) {
            Selection selection = new BitmapBackedSelection();
            selection.addRange(i, i + step);
            selections.add(selection);
        }
        splitColumnNames = new String[0];
        splitOnSelection(subTableNameTemplate, selections);
    }

    /**
     * Returns a viewGroup splitting the original table on the given columns.
     * The named columns must be CategoricalColumns
     */
    public static ViewGroup create(Table original, String... columnsNames) {
        List<CategoricalColumn> columns = original.categoricalColumns(columnsNames);
        return new ViewGroup(original, columns.toArray(new CategoricalColumn[columns.size()]));
    }

    /**
     * Splits the sourceTable table into sub-tables, grouping on the columns whose names are given in
     * splitColumnNames
     */
    private void splitOn(String... columnNames) {

        List<Column> columns = sourceTable.columns(columnNames);
        int byteSize = getByteSize(columns);

        byte[] currentKey = null;
        String currentStringKey = null;
        TemporaryView view;

        Selection selection = new BitmapBackedSelection();

        for (int row = 0; row < sourceTable.rowCount(); row++) {

            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            String newStringKey = "";

            for (int col = 0; col < columnNames.length; col++) {
                if (col > 0) {
                    newStringKey = newStringKey + SPLIT_STRING;
                }

                Column c = sourceTable.column(columnNames[col]);
                String groupKey = sourceTable.get(row, sourceTable.columnIndex(c));
                newStringKey = newStringKey + groupKey;
                byteBuffer.put(c.asBytes(row));
            }
            byte[] newKey = byteBuffer.array();
            if (row == 0) {
                currentKey = newKey;
                currentStringKey = newStringKey;
            }
            if (!Arrays.equals(newKey, currentKey)) {
                currentKey = newKey;
                view = new TemporaryView(sourceTable, selection);
                view.setName(currentStringKey);
                currentStringKey = newStringKey;
                addViewToSubTables(view);
                selection = new BitmapBackedSelection();
                selection.add(row);
            } else {
                selection.add(row);
            }
        }
        if (!selection.isEmpty()) {
            view = new TemporaryView(sourceTable, selection);
            view.setName(currentStringKey);
            addViewToSubTables(view);
        }
    }

    private int getByteSize(List<Column> columns) {
        int byteSize = 0;
        {
            for (Column c : columns) {
                byteSize += c.byteSize();
            }
        }
        return byteSize;
    }

    private void splitOnSelection(String nameTemplate, List<Selection> selections) {
        for (int i = 0; i < selections.size(); i++ ) {
            TemporaryView view = new TemporaryView(sourceTable, selections.get(i));
            String name = nameTemplate + ": " + i + 1;
            view.setName(name);
            subTables.add(view);
        }
    }

    private void addViewToSubTables(TemporaryView view) {
        subTables.add(view);
    }

    public List<TemporaryView> getSubTables() {
        return subTables;
    }

    public TemporaryView get(int i) {
        return subTables.get(i);
    }

    @VisibleForTesting
    public Table getSourceTable() {
        return sourceTable;
    }

    public int size() {
        return subTables.size();
    }


    /**
     * For a subtable that is grouped by the values in more than one column, split the grouping column into separate
     * cols and return the revised view
     */
    private NumericSummaryTable splitGroupingColumn(NumericSummaryTable groupTable) {

        if (splitColumnNames.length > 0) {
            List<Column> newColumns = new ArrayList<>();
            List<Column> columns = sourceTable.columns(splitColumnNames);
            for (Column column : columns) {
                Column newColumn = column.emptyCopy();
                newColumns.add(newColumn);
            }
            // iterate through the rows in the table and split each of the grouping columns into multiple columns
            for (int row = 0; row < groupTable.rowCount(); row++) {
                List<String> strings = SPLITTER.splitToList(groupTable.categoryColumn("Group").get(row));
                for (int col = 0; col < newColumns.size(); col++) {
                    newColumns.get(col).appendCell(strings.get(col));
                }
            }
            for (int col = 0; col < newColumns.size(); col++) {
                Column c = newColumns.get(col);
                groupTable.addColumn(col, c);
            }
            groupTable.removeColumns("Group");
        }

        return groupTable;
    }

    public NumericSummaryTable first(String columnName) {
        return agg(columnName, first);
    }

    public NumericSummaryTable last(String columnName) {
        return agg(columnName, last);
    }

    public NumericSummaryTable count(String columnName) {
        return agg(columnName, count);
    }

    public NumericSummaryTable mean(String columnName) {
        return agg(columnName, mean);
    }

    public NumericSummaryTable sum(String columnName) {
        return agg(columnName, sum);
    }

    public NumericSummaryTable median(String columnName) {
        return agg(columnName, median);
    }

    public NumericSummaryTable quartile1(String columnName) {
        return agg(columnName, quartile1);
    }

    public NumericSummaryTable quartile3(String columnName) {
        return agg(columnName, quartile3);
    }

    public NumericSummaryTable percentile90(String columnName) {
        return agg(columnName, percentile90);
    }

    public NumericSummaryTable percentile95(String columnName) {
        return agg(columnName, percentile95);
    }

    public NumericSummaryTable percentile99(String columnName) {
        return agg(columnName, percentile99);
    }

    public NumericSummaryTable range(String columnName) {
        return agg(columnName, range);
    }

    public NumericSummaryTable min(String columnName) {
        return agg(columnName, min);
    }

    public NumericSummaryTable max(String columnName) {
        return agg(columnName, max);
    }

    public NumericSummaryTable product(String columnName) {
        return agg(columnName, product);
    }

    public NumericSummaryTable geometricMean(String columnName) {
        return agg(columnName, geometricMean);
    }

    public NumericSummaryTable populationVariance(String columnName) {
        return agg(columnName, populationVariance);
    }

    public NumericSummaryTable quadraticMean(String columnName) {
        return agg(columnName, quadraticMean);
    }

    public NumericSummaryTable kurtosis(String columnName) {
        return agg(columnName, kurtosis);
    }

    public NumericSummaryTable skewness(String columnName) {
        return agg(columnName, skewness);
    }

    public NumericSummaryTable sumOfSquares(String columnName) {
        return agg(columnName, sumOfSquares);
    }

    public NumericSummaryTable sumOfLogs(String columnName) {
        return agg(columnName, sumOfLogs);
    }

    public NumericSummaryTable variance(String columnName) {
        return agg(columnName, variance);
    }

    public NumericSummaryTable stdDev(String columnName) {
        return agg(columnName, stdDev);
    }

    /**
     * Applies the given aggregation to the given column.
     * The apply and combine steps of a split-apply-combine.
     */
    public NumericSummaryTable aggregate(String colName1, AggregateFunction... func1) {
        ArrayListMultimap<String, AggregateFunction> map = ArrayListMultimap.create();
        map.putAll(colName1, Lists.newArrayList(func1));
        return aggregate(map);
    }

    /**
     * Applies the given aggregation to the given column.
     * The apply and combine steps of a split-apply-combine.
     */
    public NumericSummaryTable agg(String colName1, AggregateFunction func1) {
        return agg(ImmutableMap.of(colName1, func1));
    }

    /**
     * Applies the given aggregation to the given column.
     * The apply and combine steps of a split-apply-combine.
     */
    public NumericSummaryTable agg(
            String colName1, AggregateFunction func1,
            String colName2, AggregateFunction func2) {
        return agg(ImmutableMap.of(
                colName1, func1,
                colName2, func2));
    }

    /**
     * Applies the given aggregation to the given column.
     * The apply and combine steps of a split-apply-combine.
     */
    public NumericSummaryTable agg(
            String colName1, AggregateFunction func1,
            String colName2, AggregateFunction func2,
            String colName3, AggregateFunction func3) {
        return agg(ImmutableMap.of(
                colName1, func1,
                colName2, func2,
                colName3, func3));
    }

    /**
     * Applies the given aggregation to the given column.
     * The apply and combine steps of a split-apply-combine.
     */
    public NumericSummaryTable agg(
            String colName1, AggregateFunction func1,
            String colName2, AggregateFunction func2,
            String colName3, AggregateFunction func3,
            String colName4, AggregateFunction func4) {
        return agg(ImmutableMap.of(
                colName1, func1,
                colName2, func2,
                colName3, func3,
                colName4, func4));
    }


    /**
     * Applies the given aggregations to the given columns.
     * The apply and combine steps of a split-apply-combine.
     *
     * @param functions map from column name to aggregation to apply on that function
     */
    public NumericSummaryTable aggregate(ArrayListMultimap<String, AggregateFunction> functions) {
        Preconditions.checkArgument(!subTables.isEmpty());
        NumericSummaryTable groupTable = NumericSummaryTable.create(sourceTable.name() + " summary");
        CategoryColumn groupColumn = new CategoryColumn("Group", subTables.size());
        groupTable.addColumn(groupColumn);
        for (Map.Entry<String, Collection<AggregateFunction>> entry : functions.asMap().entrySet()) {
            String columnName = entry.getKey();
            int functionCount = 0;
            for (AggregateFunction function : entry.getValue()) {
                String colName = aggregateColumnName(columnName, function.functionName());
                DoubleColumn resultColumn = new DoubleColumn(colName, subTables.size());
                for (TemporaryView subTable : subTables) {
                    double result = subTable.reduce(columnName, function);
                    if (functionCount == 0) {
                        groupColumn.append(subTable.name());
                    }
                    resultColumn.append(result);
                }
                groupTable.addColumn(resultColumn);
                functionCount++;
            }
        }
        return splitGroupingColumn(groupTable);
    }

    /**
     * Applies the given aggregations to the given columns.
     * The apply and combine steps of a split-apply-combine.
     *
     * @param functions map from column name to aggregation to apply on that function
     */
    public NumericSummaryTable agg(Map<String, AggregateFunction> functions) {
        Preconditions.checkArgument(!subTables.isEmpty());
        NumericSummaryTable groupTable = NumericSummaryTable.create(sourceTable.name() + " summary");
        CategoryColumn groupColumn = new CategoryColumn("Group", subTables.size());
        groupTable.addColumn(groupColumn);
        for (Map.Entry<String, AggregateFunction> entry : functions.entrySet()) {
            String columnName = entry.getKey();
            AggregateFunction function = entry.getValue();

            String colName = aggregateColumnName(columnName, function.functionName());
            DoubleColumn resultColumn = new DoubleColumn(colName, subTables.size());
            for (TemporaryView subTable : subTables) {
                double result = subTable.reduce(columnName, function);
                groupColumn.append(subTable.name());
                resultColumn.append(result);
            }
            groupTable.addColumn(resultColumn);
        }
        return splitGroupingColumn(groupTable);
    }


    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<TemporaryView> iterator() {
        return subTables.iterator();
    }

    private String aggregateColumnName(String columnName, String functionName) {
        return String.format("%s [%s]", functionName, columnName);
    }
}
