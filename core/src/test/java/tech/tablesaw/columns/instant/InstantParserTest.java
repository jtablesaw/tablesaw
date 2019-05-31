package tech.tablesaw.columns.instant;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.InstantColumn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstantParserTest {

  private static final InstantParser parser = new InstantParser(ColumnType.INSTANT);

  @Test
  public void string() {
    Instant instant = Instant.parse("2019-05-31T03:45:04.021Z");
    assertEquals(instant, parser.parse(instant.toString()));
  }

  @Test
  public void unformattedString() {
    Instant instant = Instant.parse("2019-05-31T03:45:04.021Z");
    InstantColumn col = InstantColumn.create("instantCol", new Instant[] { instant });
    assertEquals(instant, parser.parse(col.getUnformattedString(0)));
  }

  @Test
  public void canParse() {
    assertFalse(parser.canParse("foobar"));
    assertTrue(parser.canParse(Instant.now().toString()));
  }

}
