package tech.tablesaw.analytic;

import java.util.function.Function;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSlice;

/**
 * Execute the aggregate function over the correct windows.
 *
 * Any window with a Fixed end (UNBOUNDED FOLLOWING) is converted ("mirrored")
 * into the equivalent UNBOUNDED PRECEDING widow so that a faster algorithm can be used.
 */
class WindowSlider {
  private final boolean mirrored;
  private final WindowGrowthType windowGrowthType;
  private final int frameStartShift;
  private final int frameEndShift;

  @SuppressWarnings({"unchecked", "rawtypes"})
  private final AggregateFunction function;
  // TODO change to table slice
  private final TableSlice slice;
  private final Column<?> sourceColumn;
  @SuppressWarnings({"unchecked", "rawtypes"})
  private final Column destinationColumn;

  WindowSlider(WindowFrame windowFrame,  AnalyticAggregateFunctions func, TableSlice slice,
    Column<?> sourceColumn, Column<?> destinationColumn) {
    this.slice = slice;
    this.destinationColumn = destinationColumn;
    this.sourceColumn = sourceColumn;
    this.function = func.getImplementation(windowFrame.windowGrowthType());

    // Can convert UNBOUNDED FOLLOWING to an equivalent UNBOUNDED PRECEDING window by mirroring everything.
    if (windowFrame.windowGrowthType() == WindowGrowthType.FIXED_END) {
      this.windowGrowthType = WindowGrowthType.FIXED_START;
      this.mirrored = true;
      this.frameStartShift = windowFrame.getFrameEndShift() * -1;
      this.frameEndShift = windowFrame.getFrameStartShift() * -1;
    } else {
      this.mirrored = false;
      this.frameStartShift = windowFrame.getFrameStartShift();
      this.frameEndShift = windowFrame.getFrameEndShift();
      this.windowGrowthType = windowFrame.windowGrowthType();
    }
  }

  /**
   * Slide over the partition getting a value for all the relevant windows.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  void process() {
    initWindow(sourceColumn);
    int leftBound = getInitialStartIndex() -1;
    int rightBound = getInitialEndIndex();
    for (int i = 0; i < slice.rowCount(); i++) {
      this.set(i, function.getValue());

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

  // Get the mirrored index about the center of the window.
  int mirror(int rowNumber) {
    if(this.mirrored) {
      return slice.rowCount() - rowNumber - 1;
    }
    return rowNumber;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initWindow(Column<?> sourceColumn) {
    int leftBound = Math.max(getInitialStartIndex(), 0);
    int rightBound = Math.min(getInitialEndIndex(), slice.rowCount() - 1);
    for (int i = leftBound; i <= rightBound; i++) {
      if(isMissing(i)) {
        function.addRightMostMissing();
      } else {
        function.addRightMost(get(i));
      }
    }
  }

  // Set value in the destination column.
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void set(int rowNumberInSlice, Object value) {
    destinationColumn.set(slice.mappedRowNumber(mirror(rowNumberInSlice)), value);
  }

  // Get value from the source column. Pulling from the source table behind the slide.
  private Object get(int rowNumberInSlice)  {
    return sourceColumn.get(slice.mappedRowNumber(mirror(rowNumberInSlice)));
  }

  // Determine if the value in the source column is missing.
  private boolean isMissing(int rowNumberInSlice)  {
    return sourceColumn.isMissing(slice.mappedRowNumber(mirror(rowNumberInSlice)));
  }

  private boolean inTableRange(int rowNumber) {
    return rowNumber >= 0 && rowNumber < slice.rowCount();
  }

  private Function<Integer, Integer> slideLeftStrategy() {
    switch (this.windowGrowthType) {
      case FIXED:
      case FIXED_START:
        return i -> i;
      case SLIDING:
        return i -> i + 1;
    }
    throw new RuntimeException("Unexpected growthType: " + this.windowGrowthType);
  }

  private Function<Integer, Integer> slideRightStrategy() {
      switch (this.windowGrowthType) {
        case FIXED:
          return i -> i;
        case FIXED_START:
        case SLIDING:
          return i -> i + 1;
      }
    throw new RuntimeException("Unexpected growthType: " + this.windowGrowthType);
  }

  private int getInitialStartIndex() {
    switch (this.windowGrowthType) {
      case FIXED:
      case FIXED_START:
        return 0;
      case SLIDING:
          return this.frameStartShift;
    }
    throw new RuntimeException("Unexpected growthType: " + this.windowGrowthType);
  }

  private int getInitialEndIndex() {
    switch (this.windowGrowthType) {
      case FIXED:
        return slice.rowCount() - 1;
      case FIXED_START:
      case SLIDING:
        return this.frameEndShift;
    }
    throw new RuntimeException("Unexpected growthType: " + this.windowGrowthType);
  }
}
