package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class AnalyticNumberingFunctionsTest {

  private Table table;

  @BeforeEach
  public void setUp() throws Exception {
    table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
  }

  @Test
  public void rowNumber() {
    IntColumn rowNumbers = (IntColumn) AnalyticNumberingFunctions.ROW_NUMBER.getImplementation().apply(table.stringColumn("who"));
    double[] expected = IntStream.range(1, table.rowCount() + 1).mapToDouble(Double::valueOf).toArray();

    assertEquals(table.rowCount(), rowNumbers.size());
    assertArrayEquals(expected, rowNumbers.asDoubleArray());
  }

}