package tech.tablesaw.joining;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SortMergeFullJoin extends SortMergeJoinStrategy{
    private final String LEFT_RECORD_ID_NAME;
    private final String RIGHT_RECORD_ID_NAME;

    public SortMergeFullJoin(int[] rightJoinColumnPositions, int[] leftJoinColumnPositions,
                             String LEFT_RECORD_ID_NAME, String RIGHT_RECORD_ID_NAME) {
        super(rightJoinColumnPositions, leftJoinColumnPositions);
        this.LEFT_RECORD_ID_NAME = LEFT_RECORD_ID_NAME;
        this.RIGHT_RECORD_ID_NAME = RIGHT_RECORD_ID_NAME;
    }

    @Override
    void perform(Table destination, Table left, Table right, int[] ignoreColumns) {
        Table tempDestination = destination.emptyCopy();

        joinInner(destination, left, right, ignoreColumns);

        Selection unmatchedLeft =
                left.intColumn(LEFT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(LEFT_RECORD_ID_NAME).unique());
        addLeftOnlyValues(destination, left, unmatchedLeft);

        Selection unmatchedRight =
                right.intColumn(RIGHT_RECORD_ID_NAME)
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
}
