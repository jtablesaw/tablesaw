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
  void shouldTrimWhitespacesOutsideQuotes() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    String columnName = "Reporting year";
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }

  @Test
  void ignoreTrailingWhitespacesInQuotes() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    String columnName = "Actual  Annual Electricity Output (Latest Year, MWh)";
    builder.ignoreTrailingWhitespacesInQuotes(true);
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }

  @Test
  void shouldNotIgnoreTrailingWhitespacesInQuotes() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    builder.ignoreTrailingWhitespacesInQuotes(false);
    String columnName = "Actual  Annual Electricity Output (Latest Year, MWh) ";
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }

  @Test
  void ignoreLeadingWhitespacesInQuotes() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    String columnName = "Project Title";
    builder.ignoreLeadingWhitespacesInQuotes(true);
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }

  @Test
  void shouldNotIgnoreLeadingWhitespacesInQuotes() throws Exception {
    Builder builder = CsvReadOptions.builder("../data/2017_Climate_Investment_Funds.csv");
    builder.ignoreLeadingWhitespacesInQuotes(false);
    String columnName = " Project Title";
    Column<?> c = Table.read().csv(builder).column(columnName);
    assertNotNull(c);
  }
}
