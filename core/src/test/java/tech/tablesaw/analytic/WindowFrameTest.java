package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tech.tablesaw.analytic.WindowFrame.WindowBoundTypes;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;

class WindowFrameTest {

  @Test
  public void testDefault() {
    WindowFrame frame = WindowFrame.builder().build();

    String expectedString = "ROWS BETWEEN UNBOUNDED_PRECEDING AND UNBOUNDED_FOLLOWING";

    assertEquals(WindowBoundTypes.UNBOUNDED_PRECEDING, frame.getLeftBoundType());
    assertEquals(WindowBoundTypes.UNBOUNDED_FOLLOWING, frame.getRightBoundType());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void testPreceding() {
    WindowFrame frame = WindowFrame.builder().setLeftPreceding(5).setRightPreceding(2).build();
    String expectedString = "ROWS BETWEEN 5 PRECEDING AND 2 PRECEDING";

    assertEquals(WindowBoundTypes.PRECEDING, frame.getLeftBoundType());
    assertEquals(-5, frame.getInitialLeftBound());
    assertEquals(WindowBoundTypes.PRECEDING, frame.getRightBoundType());
    assertEquals(-2, frame.getInitialRightBound());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void testCurrentRowToUnbounded() {
    WindowFrame frame = WindowFrame.builder().setLeftCurrentRow().build();

    String expectedString = "ROWS BETWEEN CURRENT_ROW AND UNBOUNDED_FOLLOWING";

    assertEquals(WindowBoundTypes.CURRENT_ROW, frame.getLeftBoundType());
    assertEquals(0, frame.getInitialLeftBound());
    assertEquals(WindowBoundTypes.UNBOUNDED_FOLLOWING, frame.getRightBoundType());
    assertEquals(0, frame.getInitialRightBound());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void testFollowing() {
    WindowFrame frame = WindowFrame.builder().setLeftFollowing(2).setRightFollowing(5).build();
    String expectedString = "ROWS BETWEEN 2 FOLLOWING AND 5 FOLLOWING";

    assertEquals(WindowBoundTypes.FOLLOWING, frame.getLeftBoundType());
    assertEquals(2, frame.getInitialLeftBound());
    assertEquals(WindowBoundTypes.FOLLOWING, frame.getRightBoundType());
    assertEquals(5, frame.getInitialRightBound());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void precedingBeforeFollowing() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setLeftFollowing(10).setRightPreceding(10).build());

    assertTrue(thrown.getMessage().contains("FOLLOWING cannot come before PRECEDING"));
  }

  @Test
  public void followingBeforeCurrentRow() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setLeftFollowing(10).setRightCurrentRow().build());

    assertTrue(thrown.getMessage().contains("FOLLOWING cannot come before CURRENT_ROW"));
  }

  @Test
  public void rightShiftLargerThanLeftShiftWithPrecedingWindow() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setLeftPreceding(5).setRightPreceding(10).build());
    assertTrue(
        thrown
            .getMessage()
            .contains("must be greater than the number preceding at the end of the window "));
  }

  @Test
  public void rightShiftLargerThanLeftShiftWithFollowinggWindow() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setLeftFollowing(10).setRightFollowing(5).build());
    assertTrue(
        thrown
            .getMessage()
            .contains("must be less than the number following at the end of the window"));
  }

  @Test
  public void rightShiftEqualsThanLeftShift() {
    assertThrows(
        IllegalArgumentException.class,
        () -> WindowFrame.builder().setLeftPreceding(5).setRightPreceding(5).build());
  }

  @Test
  public void windowGrowthTypeUnbounded() {
    WindowGrowthType growthType = WindowFrame.builder().build().windowGrowthType();
    assertEquals(WindowGrowthType.FIXED, growthType);
  }

  @Test
  public void windowGrowthTypeFixedStart() {
    WindowGrowthType growthType =
        WindowFrame.builder().setRightFollowing(10).build().windowGrowthType();
    assertEquals(WindowGrowthType.FIXED_LEFT, growthType);
  }

  @Test
  public void windowGrothTypeFixedEnd() {
    WindowGrowthType growthType =
        WindowFrame.builder().setLeftFollowing(10).build().windowGrowthType();
    assertEquals(WindowGrowthType.FIXED_RIGHT, growthType);
  }

  @Test
  public void windowGrowthTypeSliding() {
    WindowGrowthType growthType =
        WindowFrame.builder().setLeftPreceding(5).setRightFollowing(5).build().windowGrowthType();
    assertEquals(WindowGrowthType.SLIDING, growthType);
  }

  @Test
  public void windowGrowthTypeSlidingWithCurrentRow() {
    WindowGrowthType growthType =
        WindowFrame.builder().setLeftPreceding(5).setRightCurrentRow().build().windowGrowthType();
    assertEquals(WindowGrowthType.SLIDING, growthType);
  }
}
