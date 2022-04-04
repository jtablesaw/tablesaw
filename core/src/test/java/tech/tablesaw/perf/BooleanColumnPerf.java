package tech.tablesaw.perf;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.testutil.NanoBench;

public class BooleanColumnPerf {

  public static void main(String[] args) {

    NanoBench bench = NanoBench.create();
    bench.warmUps(50);
    Runnable r = new BooleanColumnPerf.Runner();
    bench.measure("test", r);
  }

  static class Runner extends Thread {

    public void run() {
      BooleanColumn booleanColumn = BooleanColumn.create("t");
      for (int i = 0; i < 10_000_000; i++) {
        booleanColumn.append(i % 2 == 0 ? Boolean.TRUE : Boolean.FALSE);
      }
      for (int i = 0; i < 1_000_000; i++) {
        if (i % 4 == 0) {
          booleanColumn.setMissing(i);
        }
        if (i % 5 == 0) {
          booleanColumn.set(i, Boolean.FALSE);
        }
      }
      booleanColumn.size();
      booleanColumn.countMissing();
      booleanColumn.countTrue();
      booleanColumn.countFalse();
      booleanColumn.sortAscending();
      booleanColumn.sortDescending();
      booleanColumn.asSelection();
      booleanColumn.isFalse();
    }
  }
}
