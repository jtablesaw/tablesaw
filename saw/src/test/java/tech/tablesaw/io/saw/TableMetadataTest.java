package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.io.saw.CompressionType.SNAPPY;

import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

class TableMetadataTest {

  private static Table table1;
  private static TableMetadata tableMetadata1;

  @BeforeEach
  void setUp() throws Exception {
    table1 = Table.read().csv("../data/bush.csv");
    tableMetadata1 = new TableMetadata(table1, SNAPPY);
  }

  @Test
  void toAndFromJson() {
    String json = tableMetadata1.toJson();
    TableMetadata copy = TableMetadata.fromJson(json);
    assertEquals(tableMetadata1, copy);
  }

  @Test
  void testReadTableMetaDataFromFile() {
    String path = SawTable.write("../testoutput/bush", table1);
    TableMetadata metadata = TableMetadata.readTableMetadata(Paths.get(path));
    assertEquals(table1.name(), metadata.getName());
    assertEquals(table1.rowCount(), metadata.getRowCount());
    assertEquals(table1.columnNames(), metadata.columnNames());
    assertEquals(SNAPPY, metadata.getCompressionType());

    String json = tableMetadata1.toJson();
    TableMetadata copy = TableMetadata.fromJson(json);
    assertEquals(tableMetadata1, copy);
  }

  @Test
  void getName() {
    assertEquals(table1.name(), tableMetadata1.getName());
  }

  @Test
  void getRowCount() {
    assertEquals(table1.rowCount(), tableMetadata1.getRowCount());
  }

  @Test
  void getVersion() {
    assertEquals(1, tableMetadata1.getVersion());
  }

  @Test
  void getColumnMetadataList() {
    for (int i = 0; i < table1.columnCount(); i++) {
      Column<?> c = table1.columns().get(i);
      ColumnMetadata cmd = tableMetadata1.getColumnMetadataList().get(i);
      assertEquals(c.name(), cmd.getName());
    }
  }

  @Test
  void getColumnCount() {
    assertEquals(table1.columnCount(), tableMetadata1.columnCount());
  }

  @Test
  void shape() {
    assertEquals(table1.shape(), tableMetadata1.shape());
  }

  @Test
  void columnNames() {
    assertEquals(table1.columnNames(), tableMetadata1.columnNames());
  }

  @Test
  void structure() {
    assertEquals(table1.structure().toString(), tableMetadata1.structure().toString());
  }
}
