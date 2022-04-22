package tech.tablesaw.io.arrow;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

class ArrowWriterTest {

  private static final String tempDir = System.getProperty("java.io.tmpdir");
  Table bush = Table.read().csv("../data/bush.csv");

  @BeforeEach
  void setUp() {}

  @Test
  void write() {
    ArrowWriter writer = new ArrowWriter();
    File f = Paths.get(tempDir, "bush.arrow").toFile();
    writer.write(bush, f);
  }
}
