package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

class FileReaderTest {

  @Test
  void parseWhenHeadersHaveWhitespace() throws Exception {
    Table t = Table.read().csv("../data/2017_Climate_Investment_Funds.csv");
    assertNotNull(t);
  }
}
