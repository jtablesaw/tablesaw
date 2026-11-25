package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class FileCharsetDetectorTest {

  @Test
  void detect() {
    assertEquals(
        "ISO-8859-1",
        FileCharsetDetector.detect(Paths.get("../data", "urb_cpop1_1_Data.csv").toFile()).name());
  }
}
