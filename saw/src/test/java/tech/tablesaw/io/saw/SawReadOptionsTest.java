package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Lists;
import java.util.List;
import org.junit.jupiter.api.Test;

class SawReadOptionsTest {

  @Test
  void threadPoolSize() {
    SawReadOptions options = new SawReadOptions().threadPoolSize(10001);
    assertEquals(10001, options.getThreadPoolSize());
  }

  @Test
  void selectedColumns() {
    String[] names = {"foo", "bar"};
    List<String> nameList = Lists.newArrayList(names);
    SawReadOptions options = new SawReadOptions().selectedColumns(names);
    assertEquals(nameList, options.getSelectedColumns());
  }

  @Test
  void testSelectedColumns() {
    String[] names = {"foo", "bar"};
    List<String> nameList = Lists.newArrayList(names);
    SawReadOptions options = new SawReadOptions().selectedColumns(nameList);
    assertEquals(nameList, options.getSelectedColumns());
  }
}
