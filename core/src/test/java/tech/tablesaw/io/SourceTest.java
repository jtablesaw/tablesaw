package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class SourceTest {

  @Test
  void getCharSet() throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get("../data", "urb_cpop1_1_Data.csv"));
    assertEquals("ISO-8859-1", Source.getCharSet(ByteBuffer.wrap(bytes)).name());
  }
}
