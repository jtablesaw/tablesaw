package tech.tablesaw.joining;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortMergeJoinPerformer {
    JoinType joinType;
    private String LEFT_RECORD_ID_NAME;
    private String RIGHT_RECORD_ID_NAME;
    private int[] leftJoinColumnPositions;
    private int[] rightJoinColumnPositions;

    public SortMergeJoinPerformer(JoinType joinType, String LEFT_RECORD_ID_NAME, String RIGHT_RECORD_ID_NAME,
                                  int[] leftJoinColumnPositions, int[] rightJoinColumnPositions){
        this.joinType = joinType;
        this.LEFT_RECORD_ID_NAME = LEFT_RECORD_ID_NAME;
        this.RIGHT_RECORD_ID_NAME = RIGHT_RECORD_ID_NAME;
        this.leftJoinColumnPositions = leftJoinColumnPositions;
        this.rightJoinColumnPositions = rightJoinColumnPositions;
    }

    public void join(Table result, Table table1, Table table2, int[] resultIgnoreColIndexes){
        if (joinType == JoinType.INNER) {
            joinInner(result, table1, table2, resultIgnoreColIndexes);
        } else if (joinType == JoinType.LEFT_OUTER) {
            joinLeft(result, table1, table2, resultIgnoreColIndexes);
        } else if (joinType == JoinType.RIGHT_OUTER) {
            joinRight(result, table1, table2, resultIgnoreColIndexes);
        } else if (joinType == JoinType.FULL_OUTER) {
            joinFull(result, table1, table2, resultIgnoreColIndexes);
        }
    }

    private void joinInner(Table destination, Table left, Table right, int[] ignoreColumns) {

        Comparator<Row> comparator = getRowComparator(left, rightJoinColumnPositions);

        Row leftRow = left.row(0);
        Row rightRow = right.row(0);

        // Marks the position of the first record in right table that matches a specific join value
        int mark = -1;

        while (leftRow.hasNext() || rightRow.hasNext()) {
            if (mark == -1) {
                while (comparator.compare(leftRow, rightRow) < 0 && leftRow.hasNext()) leftRow.next();
                while (comparator.compare(leftRow, rightRow) > 0 && rightRow.hasNext()) rightRow.next();
                // set the position of the first matching record on the right side
                mark = rightRow.getRowNumber();
            }
            if (comparator.compare(leftRow, rightRow) == 0 && (leftRow.hasNext() || rightRow.hasNext())) {
                addValues(destination, leftRow, rightRow);
                if (rightRow.hasNext()) {
                    rightRow.next();
                } else {
                    rightRow.at(mark);
                    if (leftRow.hasNext()) {
                        leftRow.next();
                    }
                    mark = -1;
                }
            } else {
                if (rightRow.hasNext() && leftRow.hasNext()) {
                    rightRow.at(mark);
                    leftRow.next();
                    mark = -1;
                } else {
                    if (leftRow.hasNext()) leftRow.next();
                    if (!leftRow.hasNext()) {
                        break;
                    }
                }
            }
        }
        // add the last value if you end on a match
        if (comparator.compare(leftRow, rightRow) == 0) {
            addValues(destination, leftRow, rightRow);
        }
    }

    private void joinLeft(Table destination, Table left, Table right, int[] ignoreColumns) {

        joinInner(destination, left, right, ignoreColumns);
        Selection unmatched =
                left.intColumn(LEFT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(LEFT_RECORD_ID_NAME).unique());
        addLeftOnlyValues(destination, left, unmatched);
    }

    private void joinRight(Table destination, Table left, Table right, int[] ignoreColumns) {
        joinInner(destination, left, right, ignoreColumns);
        Selection unmatched =
                right
                        .intColumn(RIGHT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(RIGHT_RECORD_ID_NAME).unique());
        addRightOnlyValues(destination, left, right, unmatched);
    }

    private void joinFull(Table destination, Table left, Table right, int[] ignoreColumns) {

        Table tempDestination = destination.emptyCopy();

        joinInner(destination, left, right, ignoreColumns);

        Selection unmatchedLeft =
                left.intColumn(LEFT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(LEFT_RECORD_ID_NAME).unique());
        addLeftOnlyValues(destination, left, unmatchedLeft);

        Selection unmatchedRight =
                right
                        .intColumn(RIGHT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(RIGHT_RECORD_ID_NAME).unique());
        addRightOnlyValues(tempDestination, left, right, unmatchedRight);
        for (int i = 0; i < ignoreColumns.length; i++) {
            String name = tempDestination.columnNames().get(leftJoinColumnPositions[i]);
            tempDestination.replaceColumn(
                    leftJoinColumnPositions[i],
                    tempDestination.column(ignoreColumns[i]).copy().setName(name));
        }
        destination.append(tempDestination);
    }

    private void addLeftOnlyValues(Table destination, Table left, Selection unmatched) {
        for (Row leftRow : left.where(unmatched)) {
            Row destRow = destination.appendRow();
            for (int c = 0; c < leftRow.columnCount() - 1; c++) {
                updateDestinationRow(destRow, leftRow, c, c);
            }
            // update the index column putting it at the end of the destination table
            updateDestinationRow(destRow, leftRow, destRow.columnCount() - 2, leftRow.columnCount() - 1);
        }
    }

    private void addRightOnlyValues(Table destination, Table left, Table right, Selection unmatched) {
        int leftColumnCount = left.columnCount();
        for (Row rightRow : right.where(unmatched)) {
            Row destRow = destination.appendRow();
            for (int c = 0; c < rightRow.columnCount() - 1; c++) {
                updateDestinationRow(destRow, rightRow, c + leftColumnCount - 1, c);
            }
            // update the index column putting it at the end of the destination table
            updateDestinationRow(
                    destRow, rightRow, destRow.columnCount() - 1, rightRow.columnCount() - 1);
        }
    }

    private Comparator<Row> getRowComparator(Table left, int[] rightJoinColumnIndexes) {
        List<ColumnIndexPair> pairs = createJoinColumnPairs(left, rightJoinColumnIndexes);
        return SortKey.getChain(SortKey.create(pairs));
    }

    private List<ColumnIndexPair> createJoinColumnPairs(Table left, int[] rightJoinColumnIndexes) {
        List<ColumnIndexPair> pairs = new ArrayList<>();
        for (int i = 0; i < leftJoinColumnPositions.length; i++) {
            ColumnIndexPair columnIndexPair =
                    new ColumnIndexPair(
                            left.column(leftJoinColumnPositions[i]).type(),
                            leftJoinColumnPositions[i],
                            rightJoinColumnIndexes[i]);
            pairs.add(columnIndexPair);
        }
        return pairs;
    }

    private void updateDestinationRow(
            Row destRow, Row sourceRow, int destColumnPosition, int sourceColumnPosition) {
        ColumnType type = destRow.getColumnType(destColumnPosition);
        if (type.equals(ColumnType.INTEGER)) {
            destRow.setInt(destColumnPosition, sourceRow.getInt(sourceColumnPosition));
        } else if (type.equals(ColumnType.LONG)) {
            destRow.setLong(destColumnPosition, sourceRow.getLong(sourceColumnPosition));
        } else if (type.equals(ColumnType.SHORT)) {
            destRow.setShort(destColumnPosition, sourceRow.getShort(sourceColumnPosition));
        } else if (type.equals(ColumnType.STRING)) {
            destRow.setString(destColumnPosition, sourceRow.getString(sourceColumnPosition));
        } else if (type.equals(ColumnType.LOCAL_DATE)) {
            destRow.setPackedDate(destColumnPosition, sourceRow.getPackedDate(sourceColumnPosition));
        } else if (type.equals(ColumnType.LOCAL_TIME)) {
            destRow.setPackedTime(destColumnPosition, sourceRow.getPackedTime(sourceColumnPosition));
        } else if (type.equals(ColumnType.LOCAL_DATE_TIME)) {
            destRow.setPackedDateTime(
                    destColumnPosition, sourceRow.getPackedDateTime(sourceColumnPosition));
        } else if (type.equals(ColumnType.INSTANT)) {
            destRow.setPackedInstant(
                    destColumnPosition, sourceRow.getPackedInstant(sourceColumnPosition));
        } else if (type.equals(ColumnType.DOUBLE)) {
            destRow.setDouble(destColumnPosition, sourceRow.getDouble(sourceColumnPosition));
        } else if (type.equals(ColumnType.FLOAT)) {
            destRow.setFloat(destColumnPosition, sourceRow.getFloat(sourceColumnPosition));
        } else if (type.equals(ColumnType.BOOLEAN)) {
            destRow.setBooleanAsByte(
                    destColumnPosition, sourceRow.getBooleanAsByte(sourceColumnPosition));
        }
    }

    private void addValues(Table destination, Row leftRow, Row rightRow) {

        Row destRow = destination.appendRow();

        // update positionally, but take into account the RECORD_ID COLUMNS at the end of the dest table
        int leftColumnCount = leftRow.columnCount();
        int rightColumnCount = rightRow.columnCount();

        // update from the left table first (everythint but the RECORD_ID column)
        for (int destIdx1 = 0; destIdx1 < leftColumnCount - 1; destIdx1++) {
            updateDestinationRow(destRow, leftRow, destIdx1, destIdx1);
        }

        // update from the right table (everythint but the RECORD_ID column)
        for (int destIdx2 = (leftColumnCount - 1);
             destIdx2 < (leftColumnCount + rightColumnCount) - 2;
             destIdx2++) {
            int rightIndex = destIdx2 - (leftColumnCount - 1);
            updateDestinationRow(destRow, rightRow, destIdx2, rightIndex);
        }

        // update the RECORD_ID columns
        updateDestinationRow(destRow, leftRow, destRow.columnCount() - 2, leftColumnCount - 1);
        updateDestinationRow(destRow, rightRow, destRow.columnCount() - 1, rightColumnCount - 1);
    }
}
