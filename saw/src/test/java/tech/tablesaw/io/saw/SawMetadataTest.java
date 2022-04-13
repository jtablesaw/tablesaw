package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.io.saw.CompressionType.SNAPPY;

import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

class SawMetadataTest {

  private static Table table1;
  private static SawMetadata metadata1;

  @BeforeEach
  void setUp() throws Exception {
    table1 = Table.read().csv("../data/bush.csv");
    metadata1 = new SawMetadata(table1, new SawWriteOptions());
  }

  @Test
  void toAndFromJson() {
    String json = metadata1.toJson();
    SawMetadata copy = SawMetadata.fromJson(json);
    assertEquals(metadata1, copy);
  }

  @Test
  void testReadTableMetaDataFromFile() {
    String path = new SawWriter("../testoutput/bush", table1).write();
    SawMetadata metadata = SawMetadata.readMetadata(Paths.get(path));
    assertEquals(table1.name(), metadata.getTableName());
    assertEquals(table1.rowCount(), metadata.getRowCount());
    assertEquals(table1.columnNames(), metadata.columnNames());
    assertEquals(SNAPPY, metadata.getCompressionType());

    String json = metadata.toJson();
    SawMetadata copy = SawMetadata.fromJson(json);
    assertEquals(metadata1, copy);
  }

  @Test
  void getName() {
    assertEquals(table1.name(), metadata1.getTableName());
  }

  @Test
  void getRowCount() {
    assertEquals(table1.rowCount(), metadata1.getRowCount());
  }

  @Test
  void getVersion() {
    assertEquals(3, metadata1.getVersion());
  }

  @Test
  void getColumnMetadataList() {
    for (int i = 0; i < table1.columnCount(); i++) {
      Column<?> c = table1.columns().get(i);
      ColumnMetadata cmd = metadata1.getColumnMetadataList().get(i);
      assertEquals(c.name(), cmd.getName());
    }
  }

  @Test
  void getColumnCount() {
    assertEquals(table1.columnCount(), metadata1.columnCount());
  }

  @Test
  void shape() {
    assertEquals(table1.shape(), metadata1.shape());
  }

  @Test
  void columnNames() {
    assertEquals(table1.columnNames(), metadata1.columnNames());
  }

  @Test
  void structure() {
    assertEquals(table1.structure().toString(), metadata1.structure().toString());
  }
}
