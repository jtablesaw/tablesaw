package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class CsvWriterTest {

  @Test
  public void toWriterWithExtension() throws IOException {
    StringColumn colA = StringColumn.create("colA", ImmutableList.of("a", "b"));
    StringColumn colB = StringColumn.create("colB", ImmutableList.of("1", "2"));
    Table table = Table.create("testTable", colA, colB);
    StringWriter writer = new StringWriter();
    table.write().toWriter(writer, "csv");
    assertEquals("colA,colB\na,1\nb,2\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  public void quoteAll() throws IOException {
    StringColumn colA = StringColumn.create("colA", ImmutableList.of("a", "b"));
    StringColumn colB = StringColumn.create("colB", ImmutableList.of("1", "2"));
    Table table = Table.create("testTable", colA, colB);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).quoteAllFields(true).build());
    assertEquals(
        "\"colA\",\"colB\"\n\"a\",\"1\"\n\"b\",\"2\"\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }
}
