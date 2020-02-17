package tech.tablesaw.analytic;

import java.util.function.Function;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSlice;

/**
 * Execute the aggregate function once for every row in the slice.
 *
 * <p>Any window with a Fixed end (UNBOUNDED FOLLOWING) is converted ("mirrored") into the
 * equivalent UNBOUNDED PRECEDING widow so that it is an append window and a faster algorithm can be
 * used.
 */
class WindowSlider {
  private final boolean mirrored;
  private final WindowGrowthType windowGrowthType;
  private final int initialLeftBound;
  private final int initialRightBound;

  @SuppressWarnings({"unchecked", "rawtypes"})
  private final AggregateFunction function;

  private final TableSlice slice;
  private final Column<?> sourceColumn;

  @SuppressWarnings({"unchecked", "rawtypes"})
  private final Column destinationColumn;

  WindowSlider(
      WindowFrame windowFrame,
      AggregateFunctions func,
      TableSlice slice,
      Column<?> sourceColumn,
      Column<?> destinationColumn) {
    this.slice = slice;
    this.destinationColumn = destinationColumn;
    this.sourceColumn = sourceColumn;
    this.function = func.getImplementation(windowFrame.windowGrowthType());

    // Convert UNBOUNDED FOLLOWING to an equivalent UNBOUNDED PRECEDING window.
    if (windowFrame.windowGrowthType() == WindowGrowthType.FIXED_RIGHT) {
      this.windowGrowthType = WindowGrowthType.FIXED_LEFT;
      this.mirrored = true;
      this.initialLeftBound = windowFrame.getInitialRightBound() * -1;
      this.initialRightBound = windowFrame.getInitialLeftBound() * -1;
    } else {
      this.mirrored = false;
      this.initialLeftBound = windowFrame.getInitialLeftBound();
      this.initialRightBound = windowFrame.getInitialRightBound();
      this.windowGrowthType = windowFrame.windowGrowthType();
    }
  }

  /** Slide the window over the slice calculating an aggregate value for every row in the slice. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  void execute() {
    initWindow();
    // Initial window bounds can be outside the current slice. This allows for windows like 20
    // PRECEDING 10 PRECEDING
    // to slide into the slice. Rows outside the slide will be ignored.
    int leftBound = getInitialLeftBound() - 1;
    int rightBound = getInitialRightBound();
    for (int i = 0; i < slice.rowCount(); i++) {
      this.set(i, function.getValue());

      // Slide the left side of the window if applicable for the window definition.
      int newLeftBound = slideLeftStrategy().apply(leftBound);
      if (newLeftBound > leftBound && isRowNumberInSlice(newLeftBound)) {
        // If the left side of the window changed remove the left most value from the aggregate
        // function.
        function.removeLeftMost();
      }
      leftBound = newLeftBound;

      // Slide the right side of the window if applicable for the window definition.
      int newRightBound = slideRightStrategy().apply(rightBound);
      if (newRightBound > rightBound && isRowNumberInSlice(newRightBound)) {
        // If the right side of the window changed add the next value to the aggregate function.
        if (isMissing(newRightBound)) {
          function.addRightMostMissing();
        } else {
          function.addRightMost(get(newRightBound));
        }
      }
      rightBound = newRightBound;
    }
  }

  /**
   * Returns the mirrored index about the center of the window. Used to convert UNBOUNDED FOLLOWING
   * windows to UNBOUNDED PRECEDING windows.
   */
  int mirror(int rowNumber) {
    if (this.mirrored) {
      return slice.rowCount() - rowNumber - 1;
    }
    return rowNumber;
  }

  /**
   * Adds initial values to the aggregate function for the first window. E.G. ROWS BETWEEN CURRENT
   * ROW AND 3 FOLLOWING would add the first four rows in the slice to the function.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initWindow() {
    int leftBound = Math.max(getInitialLeftBound(), 0);
    int rightBound = Math.min(getInitialRightBound(), slice.rowCount() - 1);
    for (int i = leftBound; i <= rightBound; i++) {
      if (isMissing(i)) {
        function.addRightMostMissing();
      } else {
        function.addRightMost(get(i));
      }
    }
  }

  /** Set the value in the destination column that corresponds to the row in the view. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void set(int rowNumberInSlice, Object value) {
    destinationColumn.set(slice.mappedRowNumber(mirror(rowNumberInSlice)), value);
  }

  /** Get a value from the source column that corresponds to the row in the view. */
  private Object get(int rowNumberInSlice) {
    return sourceColumn.get(slice.mappedRowNumber(mirror(rowNumberInSlice)));
  }

  /**
   * Determine if the value in the source column that corresponds to the row in the view is missing.
   */
  private boolean isMissing(int rowNumberInSlice) {
    return sourceColumn.isMissing(slice.mappedRowNumber(mirror(rowNumberInSlice)));
  }

  /** Returns true of the rowNumber exists in the slice. */
  private boolean isRowNumberInSlice(int rowNumber) {
    return rowNumber >= 0 && rowNumber < slice.rowCount();
  }

  private Function<Integer, Integer> slideLeftStrategy() {
    switch (this.windowGrowthType) {
      case FIXED:
      case FIXED_LEFT:
        return i -> i;
      case SLIDING:
        return i -> i + 1;
    }
    throw new IllegalArgumentException("Unexpected growthType: " + this.windowGrowthType);
  }

  private Function<Integer, Integer> slideRightStrategy() {
    switch (this.windowGrowthType) {
      case FIXED:
        return i -> i;
      case FIXED_LEFT:
      case SLIDING:
        return i -> i + 1;
    }
    throw new IllegalArgumentException("Unexpected growthType: " + this.windowGrowthType);
  }

  private int getInitialLeftBound() {
    // is zero for FIXED and FIXED_LEFT windows.
    return this.initialLeftBound;
  }

  private int getInitialRightBound() {
    switch (this.windowGrowthType) {
      case FIXED:
        return slice.rowCount() - 1;
      case FIXED_LEFT:
      case SLIDING:
        return this.initialRightBound;
    }
    throw new IllegalArgumentException("Unexpected growthType: " + this.windowGrowthType);
  }
}
