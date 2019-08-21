package tech.tablesaw.analytic;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/** Simple Data class capturing the start and end of the window. */
public final class WindowFrame {

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

  // TODO make this package private
  enum WindowGrowthType {
    FIXED,
    FIXED_START,
    FIXED_END,
    SLIDING;
  }

  private final WindowBoundTypes frameStart;
  private final int frameStartShift;
  private final WindowBoundTypes frameEnd;
  private final int frameEndShift;

  private WindowFrame(
      WindowBoundTypes frameStart,
      int frameStartShift,
      WindowBoundTypes frameEnd,
      int frameEndShift) {
    this.frameStart = frameStart;
    this.frameStartShift = frameStartShift;
    this.frameEnd = frameEnd;
    this.frameEndShift = frameEndShift;
    validateWindow();
  }

  static Builder builder() {
    return new Builder();
  }

  public WindowBoundTypes getFrameStart() {
    return frameStart;
  }

  public int getFrameStartShift() {
    return frameStartShift;
  }

  public WindowBoundTypes getFrameEnd() {
    return frameEnd;
  }

  public int getFrameEndShift() {
    return frameEndShift;
  }

  /** Throw if invalid combination */
  private void validateWindow() {
    String errorMsg = "Invalid Window: " + this.toString() + '.';
    // If bounds are the same they both must either be preceding or following.
    if (this.frameEnd == this.frameStart) {
      Preconditions.checkArgument(
          frameStart == WindowBoundTypes.PRECEDING || frameStart == WindowBoundTypes.FOLLOWING,
          errorMsg);
      // When the bounds are both preceding the lef bound should be greater than
      if (this.frameStart == WindowBoundTypes.PRECEDING) {
        Preconditions.checkArgument(
            frameStartShift < frameEndShift,
            errorMsg
                + " The number preceding at start of the window '"
                + Math.abs(frameStartShift)
                + "' must be greater than the number preceding at the end of the window '"
                + Math.abs(frameEndShift)
                + "'");
      } else {
        Preconditions.checkArgument(
            frameEndShift > frameStartShift,
            errorMsg
                + " The number following at start of the window '"
                + frameStartShift
                + "' must be less than the number following at the end of the window '"
                + frameEndShift
                + "'");
      }
    }
    Preconditions.checkArgument(
        frameEnd.order >= frameStart.order,
        errorMsg + ". " + frameStart + " cannot come before " + frameEnd);
  }

  WindowGrowthType windowGrowthType() {
    if (frameStart == WindowBoundTypes.UNBOUNDED_PRECEDING
        && frameEnd == WindowBoundTypes.UNBOUNDED_FOLLOWING) {
      return WindowGrowthType.FIXED;
    } else if ((frameStart == WindowBoundTypes.PRECEDING
            || frameStart == WindowBoundTypes.FOLLOWING
            || frameStart == WindowBoundTypes.CURRENT_ROW)
        && (frameEnd == WindowBoundTypes.PRECEDING
            || frameEnd == WindowBoundTypes.FOLLOWING
            || frameEnd == WindowBoundTypes.CURRENT_ROW)) {
      return WindowGrowthType.SLIDING;
    }
    if (frameStart == WindowBoundTypes.UNBOUNDED_PRECEDING) {
      return WindowGrowthType.FIXED_START;
    }
    return WindowGrowthType.FIXED_END;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WindowFrame that = (WindowFrame) o;
    return frameStartShift == that.frameStartShift
        && frameEndShift == that.frameEndShift
        && frameStart == that.frameStart
        && frameEnd == that.frameEnd;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(frameStart, frameStartShift, frameEnd, frameEndShift);
  }

  public String toSqlString() {
    String formatedStart = frameStart.toString();
    if (frameStart == WindowBoundTypes.PRECEDING || frameStart == WindowBoundTypes.FOLLOWING) {
      formatedStart = Math.abs(frameStartShift) + " " + formatedStart;
    }

    String formattedRightBound = frameEnd.toString();
    if (frameEnd == WindowBoundTypes.PRECEDING || frameEnd == WindowBoundTypes.FOLLOWING) {
      formattedRightBound = Math.abs(frameEndShift) + " " + formattedRightBound;
    }

    return "ROWS BETWEEN " + formatedStart + " AND " + formattedRightBound;
  }

  @Override
  public String toString() {
    return toSqlString();
  }

  static class Builder {

    private WindowBoundTypes frameStart = WindowBoundTypes.UNBOUNDED_PRECEDING;
    private int frameStartShift = 0;
    private WindowBoundTypes frameEnd = WindowBoundTypes.UNBOUNDED_FOLLOWING;
    private int frameEndShift = 0;

    private Builder() {}

    Builder setStartPreceding(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.frameStart = WindowBoundTypes.PRECEDING;
      this.frameStartShift = nRows * -1;
      return this;
    }

    Builder setStartCurrentRow() {
      this.frameStart = WindowBoundTypes.CURRENT_ROW;
      return this;
    }

    Builder setStartFollowing(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.frameStart = WindowBoundTypes.FOLLOWING;
      this.frameStartShift = nRows;
      return this;
    }

    Builder setEndPreceding(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.frameEnd = WindowBoundTypes.PRECEDING;
      this.frameEndShift = nRows * -1;
      return this;
    }

    Builder setEnndCurrentRow() {
      this.frameEnd = WindowBoundTypes.CURRENT_ROW;
      return this;
    }

    Builder setEndFollowing(int nRows) {
      Preconditions.checkArgument(nRows > 0);
      this.frameEnd = WindowBoundTypes.FOLLOWING;
      this.frameEndShift = nRows;
      return this;
    }

    public WindowFrame build() {
      return new WindowFrame(frameStart, frameStartShift, frameEnd, frameEndShift);
    }
  }
}
