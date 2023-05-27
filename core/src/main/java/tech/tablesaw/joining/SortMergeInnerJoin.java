package tech.tablesaw.joining;

import tech.tablesaw.api.Table;

public class SortMergeInnerJoin extends SortMergeJoinStrategy{

    public SortMergeInnerJoin(int[] rightJoinColumnPositions, int[] leftJoinColumnPositions) {
        super(rightJoinColumnPositions, leftJoinColumnPositions);
    }

    @Override
    public void perform(Table destination, Table left, Table right, int[] ignoreColumns) {
        joinInner(destination, left, right, ignoreColumns);
    }
}
