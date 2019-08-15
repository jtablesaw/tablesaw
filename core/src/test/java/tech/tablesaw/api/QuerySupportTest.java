package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.api.QuerySupport.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuerySupportTest {

  private Table t;

  @BeforeEach
  public void setUp() throws Exception {
    t = Table.read().csv("../data/bush.csv");
  }

  @Test
  public void test1() {
    Table result =
        t.where(
            all(
                stringColumn("who").startsWith("f"),
                dateColumn("date").isInYear(2002),
                numberColumn("approval").isLessThan(75)));

    assertTrue(result.getString(0, "who").startsWith("f"));
  }

  @Test
  public void test3() {
    Table result = t.where(stringColumn("who").isIn("fox"));
    assertEquals("fox", result.getString(0, "who"));

    result = t.where(stringColumn("who").isNotIn("fox", "zogby"));
    assertFalse(result.getString(0, "who").startsWith("f"));
  }

  @Test
  public void test2() {
    Table result = t.where(stringColumn("who").startsWith("f"));

    assertTrue(result.getString(0, "who").startsWith("f"));
  }
}
