package tech.tablesaw.columns.instant;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.ColumnType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstantParserTest {

  private static final InstantParser parser = new InstantParser(ColumnType.INSTANT);

  @Test
  public void testToString() {
    Instant instant = Instant.now();
    assertEquals(instant, parser.parse(instant.toString()));
  }

  @Test
  public void canParse() {
    assertFalse(parser.canParse("foobar"));
    assertTrue(parser.canParse(Instant.now().toString()));
  }

}
