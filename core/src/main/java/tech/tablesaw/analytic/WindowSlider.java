package tech.tablesaw.analytic;

import java.util.function.Function;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

class WindowSlider {
  WindowFrame windowFrame;
  @SuppressWarnings({"unchecked", "rawtypes"})
  AggregateFunction function;
  // TODO change to table slice
  Table table;
  String sourceColumnName;
  @SuppressWarnings({"unchecked", "rawtypes"})
  Column destination;


  @SuppressWarnings({"unchecked", "rawtypes"})
  void process() {
    validateColumn();

    int leftBound = getInitialStartIndex();
    int rightBound = getInitialEndIndex();
    initWindow();

    for (int i = 0; i < table.rowCount(); i++) {
      destination.set( getMappedRowNumber(i), function.getValue());

      int newLeftBound = slideLeftStrategy().apply(leftBound);
      if(newLeftBound > leftBound && inTableRange(newLeftBound)) {
        function.removeLeftMost();
      }
      leftBound = newLeftBound;

      int newRightBound = slideRightStrategy().apply(rightBound);
      if(newRightBound > rightBound && inTableRange(newRightBound)) {
        if(isMissing(newRightBound)) {
          function.addRightMostMissing();
        } else {
          function.addRightMost(get(newRightBound));
        }
      }
      rightBound = newRightBound;
    }
  }

  private int mirrorIndex(int index) {
    return table.rowCount() - 1 - index;
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initWindow() {
    int leftBound = Math.max(getInitialStartIndex(), 0);
    int rightBound = Math.min(getInitialEndIndex(), table.rowCount() - 1);
    for (int i = leftBound; i <= rightBound; i++) {
      function.addRightMost(get(i));
    }
  }

  private void validateColumn() {
    if(!function.isCompatibleColumn(getSourceColumn().type())) {
      throw new IllegalArgumentException("Function: " + function.functionName()
        + " Is not compatible with column type: " + getSourceColumn().type());
    }
  }

  // TODO use mapped row number.
  private Object get(int rowNumber)  {
    return getSourceColumn().get(rowNumber);
  }

  // TODO use mapped row number
  private boolean isMissing(int rowNumber)  {
    return getSourceColumn().isMissing(rowNumber);
  }

  //TODO use mapped row number for that table slice.
  private int getMappedRowNumber(int rowNumber) {
    return rowNumber;
  }

  private boolean inTableRange(int rowNumber) {
    return rowNumber >= 0 && rowNumber < table.rowCount();
  }

  private Column getSourceColumn() {
    // TODO call on the column using the mapped index with tableSlice.
    return table.column(sourceColumnName);
  }

  private Function<Integer, Integer> iterationStrategy() {
    if(this.windowFrame.windowGrowthType() == WindowGrowthType.FIXED_END) {
      return i -> i -1;
    }
    return i -> i +1;
  }

  private Function<Integer, Integer> slideLeftStrategy() {
    switch (this.windowFrame.windowGrowthType()) {
      case FIXED:
      case FIXED_START:
        return i -> i;
      case FIXED_END:
      case SLIDING:
        return i -> i + 1;
    }
    throw new RuntimeException("Unrecognized growthType: " + this.windowFrame.windowGrowthType());
  }

  private Function<Integer, Integer> slideRightStrategy() {
      switch (this.windowFrame.windowGrowthType()) {
        case FIXED:
        case FIXED_END:
          return i -> i;
        case FIXED_START:
        case SLIDING:
          return i -> i + 1;
      }
    throw new RuntimeException("Unrecognized growthType: " + this.windowFrame.windowGrowthType());
  }

  private int getInitialStartIndex() {
    switch (this.windowFrame.windowGrowthType()) {
      case FIXED:
      case FIXED_START:
        return 0;
      case FIXED_END:
      case SLIDING:
        return increase(this.windowFrame.getFrameStartShift());
    }
    throw new RuntimeException("Unrecognized growthType: " + this.windowFrame.windowGrowthType());
  }

  private int getInitialEndIndex() {
    switch (this.windowFrame.windowGrowthType()) {
      case FIXED:
      case FIXED_END:
        return table.rowCount() - 1;
      case FIXED_START:
      case SLIDING:
        return this.windowFrame.getFrameEndShift();
    }
    throw new RuntimeException("Unrecognized growthType: " + this.windowFrame.windowGrowthType());
  }

  private int increase(int value) {
    if(value == 0) {
      return value;
    }

    if(value < 0) {
      return value - 1;
    }
    return value +1;
  }

}
