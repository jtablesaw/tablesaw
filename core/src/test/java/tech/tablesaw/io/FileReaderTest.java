package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReadOptions.Builder;

class FileReaderTest {

  @Test
  void parseWhenHeadersHaveWhitespace() throws Exception {
    Table t = Table.read().csv("../data/2017_Climate_Investment_Funds.csv");
    assertNotNull(t);
  }

  @Test
  void trimHeadersWithEmptyTrailingSpace() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    String columnName = "Actual  Annual Electricity Output (Latest Year, MWh)";
    builder.trimHeaders(true);
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }

  @Test
  void shouldNotTrimHeadersWithEmptyTrailingSpace() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    builder.trimHeaders(false);
    String columnName = "Actual  Annual Electricity Output (Latest Year, MWh) ";
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }
}
