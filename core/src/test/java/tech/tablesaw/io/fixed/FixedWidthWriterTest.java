package tech.tablesaw.io.fixed;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.*;

import com.univocity.parsers.fixed.FixedWidthFields;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  @Test
  public void testFileOutputStreamWhetherClose() throws IOException {
    // Create directory if it doesn't exist
    String DEFAULT_OUTPUT_FOLDER = "../testoutput";
    Path path = Paths.get(DEFAULT_OUTPUT_FOLDER, "testOutput.txt");
    try {
      Files.createDirectories(path.getParent());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    File file = path.toFile();
    FileOutputStream fos = new FileOutputStream(file);
    FixedWidthFields fwf = new FixedWidthFields(10, 10);

    FixedWidthWriteOptions options =
        new FixedWidthWriteOptions.Builder(fos)
            .header(true)
            .autoConfigurationEnabled(false)
            .columnSpecs(fwf)
            .build();
    FixedWidthWriter writer = new FixedWidthWriter();
    writer.write(table, options);

    // Read file content
    FileInputStream fis = new FileInputStream(file);
    byte[] filecontent = new byte[(int) file.length()];
    fis.read(filecontent);
    String output = new String(filecontent);

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

    // cannot access the status of fos, so write again to test whether it close
    try {
      writer.write(table, options);

      fis = new FileInputStream(file);
      filecontent = new byte[(int) file.length()];
      fis.read(filecontent);
      output = new String(filecontent);

      assertEquals(
          "v1________v2________"
              + LINE_END
              + "1.0_______1.0_______"
              + LINE_END
              + "2.0_______2.0_______"
              + LINE_END
              + "____________________"
              + LINE_END
              + ""
              + "v1________v2________"
              + LINE_END
              + "1.0_______1.0_______"
              + LINE_END
              + "2.0_______2.0_______"
              + LINE_END
              + "____________________"
              + LINE_END
              + "",
          output);
    } catch (Exception e) {
      fail(e);
    } finally {
      file.delete();
    }
  }

  @Test
  public void testOutputStreamWriterWhetherClose() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamWriter osw = new OutputStreamWriter(baos);
    FixedWidthFields fwf = new FixedWidthFields(10, 10);

    FixedWidthWriteOptions options =
        new FixedWidthWriteOptions.Builder(osw)
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

    // cannot access the status of osw directly, so write again to test whether it close
    try {
      writer.write(table, options);
      output = baos.toString();

      assertEquals(
          "v1________v2________"
              + LINE_END
              + "1.0_______1.0_______"
              + LINE_END
              + "2.0_______2.0_______"
              + LINE_END
              + "____________________"
              + LINE_END
              + ""
              + "v1________v2________"
              + LINE_END
              + "1.0_______1.0_______"
              + LINE_END
              + "2.0_______2.0_______"
              + LINE_END
              + "____________________"
              + LINE_END
              + "",
          output);
    } catch (Exception e) {
      fail(e);
    }
  }
}
