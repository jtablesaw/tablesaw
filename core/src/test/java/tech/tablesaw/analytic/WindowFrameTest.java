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

    assertEquals(WindowBoundTypes.UNBOUNDED_PRECEDING, frame.getFrameStart());
    assertEquals(WindowBoundTypes.UNBOUNDED_FOLLOWING, frame.getFrameEnd());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void testPreceding() {
    WindowFrame frame = WindowFrame.builder().setStartPreceding(5).setEndPreceding(2).build();
    String expectedString = "ROWS BETWEEN 5 PRECEDING AND 2 PRECEDING";

    assertEquals(WindowBoundTypes.PRECEDING, frame.getFrameStart());
    assertEquals(-5, frame.getFrameStartShift());
    assertEquals(WindowBoundTypes.PRECEDING, frame.getFrameEnd());
    assertEquals(-2, frame.getFrameEndShift());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void testCurrentRowToUnbounded() {
    WindowFrame frame = WindowFrame.builder().setStartCurrentRow().build();

    String expectedString = "ROWS BETWEEN CURRENT_ROW AND UNBOUNDED_FOLLOWING";

    assertEquals(WindowBoundTypes.CURRENT_ROW, frame.getFrameStart());
    assertEquals(0, frame.getFrameStartShift());
    assertEquals(WindowBoundTypes.UNBOUNDED_FOLLOWING, frame.getFrameEnd());
    assertEquals(0, frame.getFrameEndShift());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void testFollowing() {
    WindowFrame frame = WindowFrame.builder().setStartFollowing(2).setEndFollowing(5).build();
    String expectedString = "ROWS BETWEEN 2 FOLLOWING AND 5 FOLLOWING";

    assertEquals(WindowBoundTypes.FOLLOWING, frame.getFrameStart());
    assertEquals(2, frame.getFrameStartShift());
    assertEquals(WindowBoundTypes.FOLLOWING, frame.getFrameEnd());
    assertEquals(5, frame.getFrameEndShift());
    assertEquals(expectedString, frame.toSqlString());
  }

  @Test
  public void precedingBeforeFollowing() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setStartFollowing(10).setEndPreceding(10).build());

    assertTrue(thrown.getMessage().contains("FOLLOWING cannot come before PRECEDING"));
  }

  @Test
  public void followingBeforeCurrentRow() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setStartFollowing(10).setEnndCurrentRow().build());

    assertTrue(thrown.getMessage().contains("FOLLOWING cannot come before CURRENT_ROW"));
  }

  @Test
  public void rightShiftLargerThanLeftShiftWithPrecedingWindow() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> WindowFrame.builder().setStartPreceding(5).setEndPreceding(10).build());
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
            () -> WindowFrame.builder().setStartFollowing(10).setEndFollowing(5).build());
    assertTrue(
        thrown
            .getMessage()
            .contains("must be less than the number following at the end of the window"));
  }

  @Test
  public void rightShiftEqualsThanLeftShift() {
    assertThrows(
        IllegalArgumentException.class,
        () -> WindowFrame.builder().setStartPreceding(5).setEndPreceding(5).build());
  }

  @Test
  public void windowGrowthTypeUnbounded() {
    WindowGrowthType growthType = WindowFrame.builder().build().windowGrowthType();
    assertEquals(growthType, WindowGrowthType.FIXED);
  }

  @Test
  public void windowGrowthTypeFixedStart() {
    WindowGrowthType growthType =
        WindowFrame.builder().setEndFollowing(10).build().windowGrowthType();
    assertEquals(growthType, WindowGrowthType.FIXED_START);
  }

  @Test
  public void windowGrothTypeFixedEnd() {
    WindowGrowthType growthType =
        WindowFrame.builder().setStartFollowing(10).build().windowGrowthType();
    assertEquals(growthType, WindowGrowthType.FIXED_END);
  }

  @Test
  public void windowGrowthTypeSliding() {
    WindowGrowthType growthType =
        WindowFrame.builder().setStartPreceding(5).setEndFollowing(5).build().windowGrowthType();
    assertEquals(growthType, WindowGrowthType.SLIDING);
  }

  @Test
  public void windowGrowthTypeSlidingWithCurrentRow() {
    WindowGrowthType growthType =
        WindowFrame.builder().setStartPreceding(5).setEnndCurrentRow().build().windowGrowthType();
    assertEquals(growthType, WindowGrowthType.SLIDING);
  }
}
