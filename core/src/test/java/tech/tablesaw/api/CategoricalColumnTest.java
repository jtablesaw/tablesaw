package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CategoricalColumnTest {

  @Test
  void testCountByCategory1() {
    IntColumn column = IntColumn.create("test");
    column.append(1).append(2).appendMissing().append(4);
    assertEquals(4, column.countByCategory().nCol("Count").sum());
  }
}
