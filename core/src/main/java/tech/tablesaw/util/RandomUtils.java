package tech.tablesaw.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
  /**
   * Returns a pseudorandom double value between the specified origin (inclusive) and bound
   * (exclusive).
   */
  public static int nextInt(int origin, int bound) {
    return ThreadLocalRandom.current().nextInt(origin, bound);
  }

  /**
   * Returns a pseudorandom int value between the specified origin (inclusive) and the specified
   * bound (exclusive).
   */
  public static double nextDouble(double origin, double bound) {
    return ThreadLocalRandom.current().nextDouble(origin, bound);
  }
}
