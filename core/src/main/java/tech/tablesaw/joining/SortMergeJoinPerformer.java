package tech.tablesaw.joining;

import tech.tablesaw.api.Table;

public class SortMergeJoinPerformer {
    JoinType joinType;
    private final String LEFT_RECORD_ID_NAME;
    private final String RIGHT_RECORD_ID_NAME;
    private final int[] leftJoinColumnPositions;
    private final int[] rightJoinColumnPositions;
    private SortMergeJoinStrategy joinStrategy;

    public SortMergeJoinPerformer(JoinType joinType, String LEFT_RECORD_ID_NAME, String RIGHT_RECORD_ID_NAME,
                                  int[] leftJoinColumnPositions, int[] rightJoinColumnPositions){
        this.joinType = joinType;
        this.LEFT_RECORD_ID_NAME = LEFT_RECORD_ID_NAME;
        this.RIGHT_RECORD_ID_NAME = RIGHT_RECORD_ID_NAME;
        this.leftJoinColumnPositions = leftJoinColumnPositions;
        this.rightJoinColumnPositions = rightJoinColumnPositions;
    }

    public void setJoinStrategy(SortMergeJoinStrategy joinStrategy) {
        this.joinStrategy = joinStrategy;
    }

    public void perform(Table result, Table table1, Table table2, int[] resultIgnoreColIndexes){
        if (joinType == JoinType.INNER) {
            setJoinStrategy(new SortMergeInnerJoin(rightJoinColumnPositions, leftJoinColumnPositions));
        } else if (joinType == JoinType.LEFT_OUTER) {
            setJoinStrategy(new SortMergeLeftJoin(rightJoinColumnPositions, leftJoinColumnPositions, LEFT_RECORD_ID_NAME));
        } else if (joinType == JoinType.RIGHT_OUTER) {
            setJoinStrategy(new SortMergeRightJoin(rightJoinColumnPositions, leftJoinColumnPositions, RIGHT_RECORD_ID_NAME));
        } else if (joinType == JoinType.FULL_OUTER) {
            setJoinStrategy(new SortMergeFullJoin(rightJoinColumnPositions, leftJoinColumnPositions,
                    LEFT_RECORD_ID_NAME, RIGHT_RECORD_ID_NAME));
        }

        joinStrategy.perform(result, table1, table2, resultIgnoreColIndexes);
    }
}
