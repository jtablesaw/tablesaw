package tech.tablesaw.analytic;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * This class holds data on the WindowFrame clause of an analytic query.
 *
 * <p>Each Window is viewed as an array of values/rows and has a let bound and right bound.
 *
 * <p>For example in the window [1, 2, (3, 4, 5), 6, 7] The left most element in the window is 3 and
 * the rightmost element is 5.
 *
 * <p>For more information on the window frame clause in SQL see {@link
 * AnalyticQuerySteps.DefineWindowFame}
 */
final class WindowFrame {

  enum WindowBoundTypes {
    UNBOUNDED_PRECEDING(0),
    PRECEDING(1),
    CURRENT_ROW(2),
    FOLLOWING(3),
    UNBOUNDED_FOLLOWING(4);

    private final int order;

    WindowBoundTypes(int order) {
      this.order = order;
    }
  }

  enum WindowGrowthType {
    // UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    FIXED,
    // UNBOUNDED PRECEDING AND NOT UNBOUNDED FOLLOWING
    FIXED_LEFT,
    // NOT UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    FIXED_RIGHT,
    // NOT UNBOUNDED PRECEDING AND NOT UNBOUNDED FOLLOWING
    SLIDING;
  }

  private final WindowBoundTypes leftBoundType;
  private final int initialLeftBound;
  private final WindowBoundTypes rightBoundType;
  // Set to zero for UNBOUNDED FOLLOWING windows.
  private final int initialRightBound;

  private WindowFrame(
      WindowBoundTypes leftBoundType,
      int initialLeftBound,
      WindowBoundTypes rightBoundType,
      int initialRightBound) {
    this.leftBoundType = leftBoundType;
    this.initialLeftBound = initialLeftBound;
    this.rightBoundType = rightBoundType;
    this.initialRightBound = initialRightBound;
    validateWindow();
  }

  static Builder builder() {
    return new Builder();
  }

  WindowBoundTypes getLeftBoundType() {
    return leftBoundType;
  }

  int getInitialLeftBound() {
    return initialLeftBound;
  }

  WindowBoundTypes getRightBoundType() {
    return rightBoundType;
  }

  int getInitialRightBound() {
    return initialRightBound;
  }

  /**
   * Throw if invalid window frame. For example ROWS BETWEEN FOLLOWING AND UNBOUNDED PRECEDING is
   * invalid.
   */
  private void validateWindow() {
    String errorMsg = "Invalid Window: " + this.toString() + '.';
    // If bounds are the same they both must either be preceding or following.
    if (this.rightBoundType == this.leftBoundType) {
      Preconditions.checkArgument(
          leftBoundType == WindowBoundTypes.PRECEDING
              || leftBoundType == WindowBoundTypes.FOLLOWING,
          errorMsg);
      // When the bounds are both preceding the lef bound should be greater than
      if (this.leftBoundType == WindowBoundTypes.PRECEDING) {
        Preconditions.checkArgument(
            initialLeftBound < initialRightBound,
            errorMsg
                + " The number preceding at start of the window '"
                + Math.abs(initialLeftBound)
                + "' must be greater than the number preceding at the end of the window '"
                + Math.abs(initialRightBound)
                + "'");
      } else {
        Preconditions.checkArgument(
            initialRightBound > initialLeftBound,
            errorMsg
                + " The number following at start of the window '"
                + initialLeftBound
                + "' must be less than the number following at the end of the window '"
                + initialRightBound
                + "'");
      }
    }
    Preconditions.checkArgument(
        rightBoundType.order >= leftBoundType.order,
        errorMsg + ". " + leftBoundType + " cannot come before " + rightBoundType);
  }

  /**
   * Calculate the window growth type. Knowing the growth type simplifies the executing the query.
   */
  WindowGrowthType windowGrowthType() {
    if (leftBoundType == WindowBoundTypes.UNBOUNDED_PRECEDING
        && rightBoundType == WindowBoundTypes.UNBOUNDED_FOLLOWING) {
      return WindowGrowthType.FIXED;
    } else if ((leftBoundType == WindowBoundTypes.PRECEDING
            || leftBoundType == WindowBoundTypes.FOLLOWING
            || leftBoundType == WindowBoundTypes.CURRENT_ROW)
        && (rightBoundType == WindowBoundTypes.PRECEDING
            || rightBoundType == WindowBoundTypes.FOLLOWING
            || rightBoundType == WindowBoundTypes.CURRENT_ROW)) {
      return WindowGrowthType.SLIDING;
    }
    if (leftBoundType == WindowBoundTypes.UNBOUNDED_PRECEDING) {
      return WindowGrowthType.FIXED_LEFT;
    }
    return WindowGrowthType.FIXED_RIGHT;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WindowFrame that = (WindowFrame) o;
    return initialLeftBound == that.initialLeftBound
        && initialRightBound == that.initialRightBound
        && leftBoundType == that.leftBoundType
        && rightBoundType == that.rightBoundType;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(leftBoundType, initialLeftBound, rightBoundType, initialRightBound);
  }

  String toSqlString() {
    String formatedStart = leftBoundType.toString();
    if (leftBoundType == WindowBoundTypes.PRECEDING
        || leftBoundType == WindowBoundTypes.FOLLOWING) {
      formatedStart = Math.abs(initialLeftBound) + " " + formatedStart;
    }

    String formattedRightBound = rightBoundType.toString();
    if (rightBoundType == WindowBoundTypes.PRECEDING
        || rightBoundType == WindowBoundTypes.FOLLOWING) {
      formattedRightBound = Math.abs(initialRightBound) + " " + formattedRightBound;
    }

    return "ROWS BETWEEN " + formatedStart + " AND " + formattedRightBound;
  }

  @Override
  public String toString() {
    return toSqlString();
  }

  /**
   * Builder for a {@link WindowFrame}. Defaults to UNBOUNDED PRECEDING UNBOUNDED FOLLOWING.
   *
   * <p>The shift is the number of rows to extend the window left or right from the current row.
   * Negative includes rows to the left, positive includes rows to the right.
   */
  static final class Builder {

    private WindowBoundTypes leftBoundType = WindowBoundTypes.UNBOUNDED_PRECEDING;
    private int initialLeftBound = 0;
    private WindowBoundTypes rightBoundType = WindowBoundTypes.UNBOUNDED_FOLLOWING;
    // Set to zero for UNBOUNDED FOLLOWING windows
    private int initialRightBound = 0;

    private Builder() {}

    Builder setLeftPreceding(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.leftBoundType = WindowBoundTypes.PRECEDING;
      this.initialLeftBound = nRows * -1;
      return this;
    }

    Builder setLeftCurrentRow() {
      this.leftBoundType = WindowBoundTypes.CURRENT_ROW;
      return this;
    }

    Builder setLeftFollowing(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.leftBoundType = WindowBoundTypes.FOLLOWING;
      this.initialLeftBound = nRows;
      return this;
    }

    Builder setRightPreceding(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.rightBoundType = WindowBoundTypes.PRECEDING;
      this.initialRightBound = nRows * -1;
      return this;
    }

    Builder setRightCurrentRow() {
      this.rightBoundType = WindowBoundTypes.CURRENT_ROW;
      return this;
    }

    Builder setRightFollowing(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.rightBoundType = WindowBoundTypes.FOLLOWING;
      this.initialRightBound = nRows;
      return this;
    }

    public WindowFrame build() {
      return new WindowFrame(leftBoundType, initialLeftBound, rightBoundType, initialRightBound);
    }
  }
}
