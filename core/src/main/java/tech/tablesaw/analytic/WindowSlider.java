package tech.tablesaw.analytic;

import java.util.function.Function;
import tech.tablesaw.analytic.ArgumentList.FunctionCall;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSlice;

class WindowSlider {
  private final WindowFrame windowFrame;
  @SuppressWarnings({"unchecked", "rawtypes"})
  private final AnalyticAggregateFunctions functionContainer;
  private final AggregateFunction function;
  // TODO change to table slice
  private final TableSlice slice;
  @SuppressWarnings({"unchecked", "rawtypes"})
  private final Column<?> sourceColumn;
  private final Column destinationColumn;

  WindowSlider(WindowFrame windowFrame,  AnalyticAggregateFunctions func, TableSlice slice,
    Column<?> sourceColumn, Column destinationColumn) {
    this.windowFrame = windowFrame;
    this.functionContainer = func;
    this.function = func.getImplementation(windowFrame.windowGrowthType());
    this.slice = slice;
    this.destinationColumn = destinationColumn;
    this.sourceColumn = sourceColumn;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  void process() {

    int leftBound = getInitialStartIndex();
    int rightBound = getInitialEndIndex();
    initWindow(sourceColumn);

    for (int i = 0; i < slice.rowCount(); i++) {
      destinationColumn.set(slice.mappedRowNumber(i), function.getValue());

      int newLeftBound = slideLeftStrategy().apply(leftBound);
      if(newLeftBound > leftBound && inTableRange(newLeftBound)) {
        function.removeLeftMost();
      }
      leftBound = newLeftBound;

      int newRightBound = slideRightStrategy().apply(rightBound);
      if(newRightBound > rightBound && inTableRange(newRightBound)) {
        if(isMissing(sourceColumn, newRightBound)) {
          function.addRightMostMissing();
        } else {
          Object value = get(sourceColumn, newRightBound);
          function.addRightMost(value);
        }
      }
      rightBound = newRightBound;
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initWindow(Column<?> sourceColumn) {
    int leftBound = Math.max(getInitialStartIndex(), 0);
    int rightBound = Math.min(getInitialEndIndex(), slice.rowCount() - 1);
    for (int i = leftBound; i <= rightBound; i++) {
      function.addRightMost(get(sourceColumn, i));
    }
  }

  private Object get(Column<?> sourceColumn, int rowNumberInSlice)  {
    return sourceColumn.get(slice.mappedRowNumber(rowNumberInSlice));
  }

  // TODO use mapped row number
  private boolean isMissing(Column<?> sourceColumn, int rowNumberInSlice)  {
    return sourceColumn.isMissing(slice.mappedRowNumber(rowNumberInSlice));
  }

  private boolean inTableRange(int rowNumber) {
    return rowNumber >= 0 && rowNumber < slice.rowCount();
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
        return slice.rowCount() - 1;
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
