package tech.tablesaw.joining;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SortMergeRightJoin extends SortMergeJoinStrategy{
    private final String RIGHT_RECORD_ID_NAME;

    public SortMergeRightJoin(int[] rightJoinColumnPositions, int[] leftJoinColumnPositions, String RIGHT_RECORD_ID_NAME) {
        super(rightJoinColumnPositions, leftJoinColumnPositions);
        this.RIGHT_RECORD_ID_NAME = RIGHT_RECORD_ID_NAME;
    }

    @Override
    void perform(Table destination, Table left, Table right, int[] ignoreColumns) {
        joinInner(destination, left, right, ignoreColumns);
        Selection unmatched =
                right
                        .intColumn(RIGHT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(RIGHT_RECORD_ID_NAME).unique());
        addRightOnlyValues(destination, left, right, unmatched);
    }


}
