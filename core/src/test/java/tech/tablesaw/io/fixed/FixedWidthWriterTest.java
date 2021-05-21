package tech.tablesaw.io.fixed;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.*;

import com.univocity.parsers.fixed.FixedWidthFields;
import java.io.*;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class FixedWidthWriterTest {
  private static final String LINE_END = System.lineSeparator();

  private double[] v1 = {1, 2, NaN};
  private double[] v2 = {1, 2, NaN};
  private Table table =
      Table.create("t", DoubleColumn.create("v1", v1), DoubleColumn.create("v2", v2));

  @Test
  public void testOutputInFixedWidthFormat() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    FixedWidthFields fwf = new FixedWidthFields(10, 10);
    FixedWidthWriteOptions options =
        new FixedWidthWriteOptions.Builder(baos)
            .header(true)
            .columnSpecs(fwf)
            .autoConfigurationEnabled(false)
            .build();
    FixedWidthWriter writer = new FixedWidthWriter();
    writer.write(table, options);

    String output = baos.toString();
    assertEquals(
        "v1________v2________"
            + LINE_END
            + "1.0_______1.0_______"
            + LINE_END
            + "2.0_______2.0_______"
            + LINE_END
            + "____________________"
            + LINE_END
            + "",
        output);
  }
}
