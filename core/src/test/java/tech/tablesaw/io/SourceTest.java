package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class SourceTest {

  @Test
  void getCharSet() {
    assertEquals(
        "ISO-8859-1",
        Source.getCharSet(Paths.get("../data", "urb_cpop1_1_Data.csv").toFile()).name());
  }
}
