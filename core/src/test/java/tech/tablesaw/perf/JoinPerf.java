package tech.tablesaw.perf;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.testutil.NanoBench;

public class JoinPerf {

  private static Table createXYZTable(String name, int cardinality) {
    Table table = Table.create(name);
    int[] filler = new int[cardinality];
    Arrays.fill(filler, 1);
    IntColumn x = IntColumn.create("x", IntStream.range(0, cardinality).toArray());
    IntColumn y = IntColumn.create("y", IntStream.range(0, cardinality).toArray());
    IntColumn z = IntColumn.create("z", filler);
    table.addColumns(x, y, z);
    return table;
  }

  @Test
  void testTimeout() {
    Table a = createXYZTable("a", 500);
    Table b = createXYZTable("b", 500);
    assertTimeout(Duration.ofSeconds(1), () -> a.joinOn("x", "y", "z").inner(b));
  }

  @Test
  void testPerf() {
    NanoBench bench = NanoBench.create();
    bench.warmUps(1);
    bench.measurements(4);
    Runnable r = new Runner();
    bench.measure("test", r);
  }

  static class Runner extends Thread {
    Table a = createXYZTable("a", 50000);
    Table b = createXYZTable("b", 50000);

    {
      for (int i = 2; i <= 14; i++) {
        a.addColumns(IntColumn.indexColumn(String.valueOf(i), a.rowCount(), 1));
      }
      b.addColumns(IntColumn.indexColumn("1", b.rowCount(), 0));
      // System.out.println(b.intColumn("1").max());
    }

    public void run() {
      a.sortDescendingOn("x", "y", "z");
      b.sortDescendingOn("x", "y", "z");
      Table result = a.joinOn("x", "y", "z").inner(b);
    }
  }
}
