package tech.tablesaw.joining;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SortMergeLeftJoin extends SortMergeJoinStrategy{
    private final String LEFT_RECORD_ID_NAME;

    public SortMergeLeftJoin(int[] rightJoinColumnPositions, int[] leftJoinColumnPositions,
                             String LEFT_RECORD_ID_NAME) {
        super(rightJoinColumnPositions, leftJoinColumnPositions);
        this.LEFT_RECORD_ID_NAME = LEFT_RECORD_ID_NAME;
    }

    @Override
    public void perform(Table destination, Table left, Table right, int[] ignoreColumns) {
        joinInner(destination, left, right, ignoreColumns);
        Selection unmatched =
                left.intColumn(LEFT_RECORD_ID_NAME)
                        .isNotIn(destination.intColumn(LEFT_RECORD_ID_NAME).unique());
        addLeftOnlyValues(destination, left, unmatched);
    }


}
