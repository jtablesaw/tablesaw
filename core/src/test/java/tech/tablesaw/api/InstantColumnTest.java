package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstantColumnTest {

  private final InstantColumn instanceColumn = InstantColumn.create("Test");

  private Instant now = Instant.now();

  private long baseline = now.getEpochSecond();
  private long before = baseline - 100L;
  private long after = baseline + 100L;

  private Instant baselineInst = Instant.ofEpochSecond(baseline);
  private Instant beforeInst = Instant.ofEpochSecond(before);
  private Instant afterInst = Instant.ofEpochSecond(after);

  @BeforeEach
  void setUp() {

    instanceColumn.append(beforeInst);
    instanceColumn.append(baselineInst);
    instanceColumn.append(afterInst);
    instanceColumn.appendMissing();
  }

  @Test
  void isAfter() {
    assertEquals(2, instanceColumn.isAfter(baselineInst).get(0));
  }

  @Test
  void isBefore() {
    assertEquals(0, instanceColumn.isBefore(baselineInst).get(0));
    assertEquals(1, instanceColumn.isBefore(afterInst).get(1));
  }

  @Test
  void isEqualTo() {
    assertEquals(2, instanceColumn.isEqualTo(afterInst).get(0));
  }

  @Test
  void isMissing() {
    assertEquals(3, instanceColumn.isMissing().get(0));
  }

  @Test
  void isNotMissing() {
    assertEquals(0, instanceColumn.isNotMissing().get(0));
    assertEquals(1, instanceColumn.isNotMissing().get(1));
    assertEquals(2, instanceColumn.isNotMissing().get(2));
  }

  @Test
  public void testCountUnique() {
    InstantColumn column1 = InstantColumn.create("instants");
    column1.append(baselineInst);
    column1.append(baselineInst);
    column1.append(afterInst);
    column1.appendMissing();

    assertEquals(3, column1.countUnique());
  }
}
