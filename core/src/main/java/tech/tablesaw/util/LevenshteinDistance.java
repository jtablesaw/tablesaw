/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.util;

import java.util.Arrays;

/**
 * An algorithm for measuring the difference between two character sequences.
 *
 * <p>This is the number of changes needed to change one sequence into another, where each change is
 * a single character modification (deletion, insertion or substitution).
 *
 * <p>This code has been adapted from Apache Commons Lang 3.3.
 *
 * @since 1.0
 */
public class LevenshteinDistance {

  /** Default instance. */
  private static final LevenshteinDistance DEFAULT_INSTANCE = new LevenshteinDistance();

  /** Threshold. */
  private final Integer threshold;

  /**
   * This returns the default instance that uses a version of the algorithm that does not use a
   * threshold parameter.
   *
   * @see LevenshteinDistance#getDefaultInstance()
   */
  public LevenshteinDistance() {
    this(null);
  }

  /**
   * If the threshold is not null, distance calculations will be limited to a maximum length. If the
   * threshold is null, the unlimited version of the algorithm will be used.
   *
   * @param threshold If this is null then distances calculations will not be limited. This may not
   *     be negative.
   */
  public LevenshteinDistance(final Integer threshold) {
    if (threshold != null && threshold < 0) {
      throw new IllegalArgumentException("Threshold must not be negative");
    }
    this.threshold = threshold;
  }

  /**
   * Find the Levenshtein distance between two Strings.
   *
   * <p>A higher score indicates a greater distance.
   *
   * <p>The previous implementation of the Levenshtein distance algorithm was from <a
   * href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a>
   *
   * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError which
   * can occur when my Java implementation is used with very large strings.<br>
   * This implementation of the Levenshtein distance algorithm is from <a
   * href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a>
   *
   * <pre>
   * distance.apply(null, *)             = IllegalArgumentException
   * distance.apply(*, null)             = IllegalArgumentException
   * distance.apply("","")               = 0
   * distance.apply("","a")              = 1
   * distance.apply("aaapppp", "")       = 7
   * distance.apply("frog", "fog")       = 1
   * distance.apply("fly", "ant")        = 3
   * distance.apply("elephant", "hippo") = 7
   * distance.apply("hippo", "elephant") = 7
   * distance.apply("hippo", "zzzzzzzz") = 8
   * distance.apply("hello", "hallo")    = 1
   * </pre>
   *
   * @param left the first string, must not be null
   * @param right the second string, must not be null
   * @return result distance, or -1
   * @throws IllegalArgumentException if either String input {@code null}
   */
  public Integer apply(final CharSequence left, final CharSequence right) {
    if (threshold != null) {
      return limitedCompare(left, right, threshold);
    }
    return unlimitedCompare(left, right);
  }

  /**
   * Gets the default instance.
   *
   * @return the default instance
   */
  public static LevenshteinDistance getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /**
   * Gets the distance threshold.
   *
   * @return the distance threshold
   */
  public Integer getThreshold() {
    return threshold;
  }

  /**
   * Find the Levenshtein distance between two CharSequences if it's less than or equal to a given
   * threshold.
   *
   * <p>This implementation follows from Algorithms on Strings, Trees and Sequences by Dan Gusfield
   * and Chas Emerick's implementation of the Levenshtein distance algorithm from <a
   * href="http://www.merriampark.com/ld.htm" >http://www.merriampark.com/ld.htm</a>
   *
   * <pre>
   * limitedCompare(null, *, *)             = IllegalArgumentException
   * limitedCompare(*, null, *)             = IllegalArgumentException
   * limitedCompare(*, *, -1)               = IllegalArgumentException
   * limitedCompare("","", 0)               = 0
   * limitedCompare("aaapppp", "", 8)       = 7
   * limitedCompare("aaapppp", "", 7)       = 7
   * limitedCompare("aaapppp", "", 6))      = -1
   * limitedCompare("elephant", "hippo", 7) = 7
   * limitedCompare("elephant", "hippo", 6) = -1
   * limitedCompare("hippo", "elephant", 7) = 7
   * limitedCompare("hippo", "elephant", 6) = -1
   * </pre>
   *
   * @param left the first string, must not be null
   * @param right the second string, must not be null
   * @param threshold the target threshold, must not be negative
   * @return result distance, or -1
   */
  private static int limitedCompare(
      CharSequence left, CharSequence right, final int threshold) { // NOPMD
    if (left == null || right == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }
    if (threshold < 0) {
      throw new IllegalArgumentException("Threshold must not be negative");
    }

    /*
     * This implementation only computes the distance if it's less than or
     * equal to the threshold value, returning -1 if it's greater. The
     * advantage is performance: unbounded distance is O(nm), but a bound of
     * k allows us to reduce it to O(km) time by only computing a diagonal
     * stripe of width 2k + 1 of the cost table. It is also possible to use
     * this to compute the unbounded Levenshtein distance by starting the
     * threshold at 1 and doubling each time until the distance is found;
     * this is O(dm), where d is the distance.
     *
     * One subtlety comes from needing to ignore entries on the border of
     * our stripe eg. p[] = |#|#|#|* d[] = *|#|#|#| We must ignore the entry
     * to the left of the leftmost member We must ignore the entry above the
     * rightmost member
     *
     * Another subtlety comes from our stripe running off the matrix if the
     * strings aren't of the same size. Since string s is always swapped to
     * be the shorter of the two, the stripe will always run off to the
     * upper right instead of the lower left of the matrix.
     *
     * As a concrete example, suppose s is of length 5, t is of length 7,
     * and our threshold is 1. In this case we're going to walk a stripe of
     * length 3. The matrix would look like so:
     *
     * <pre>
     *    1 2 3 4 5
     * 1 |#|#| | | |
     * 2 |#|#|#| | |
     * 3 | |#|#|#| |
     * 4 | | |#|#|#|
     * 5 | | | |#|#|
     * 6 | | | | |#|
     * 7 | | | | | |
     * </pre>
     *
     * Note how the stripe leads off the table as there is no possible way
     * to turn a string of length 5 into one of length 7 in edit distance of
     * 1.
     *
     * Additionally, this implementation decreases memory usage by using two
     * single-dimensional arrays and swapping them back and forth instead of
     * allocating an entire n by m matrix. This requires a few minor
     * changes, such as immediately returning when it's detected that the
     * stripe has run off the matrix and initially filling the arrays with
     * large values so that entries we don't compute are ignored.
     *
     * See Algorithms on Strings, Trees and Sequences by Dan Gusfield for
     * some discussion.
     */

    int n = left.length(); // length of left
    int m = right.length(); // length of right

    // if one string is empty, the edit distance is necessarily the length
    // of the other
    if (n == 0) {
      return m <= threshold ? m : -1;
    } else if (m == 0) {
      return n <= threshold ? n : -1;
    }

    if (n > m) {
      // swap the two strings to consume less memory
      final CharSequence tmp = left;
      left = right;
      right = tmp;
      n = m;
      m = right.length();
    }

    int[] p = new int[n + 1]; // 'previous' cost array, horizontally
    int[] d = new int[n + 1]; // cost array, horizontally
    int[] tempD; // placeholder to assist in swapping p and d

    // fill in starting table values
    final int boundary = Math.min(n, threshold) + 1;
    for (int i = 0; i < boundary; i++) {
      p[i] = i;
    }
    // these fills ensure that the value above the rightmost entry of our
    // stripe will be ignored in following loop iterations
    Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
    Arrays.fill(d, Integer.MAX_VALUE);

    // iterates through t
    for (int j = 1; j <= m; j++) {
      final char rightJ = right.charAt(j - 1); // jth character of right
      d[0] = j;

      // compute stripe indices, constrain to array size
      final int min = Math.max(1, j - threshold);
      final int max = j > Integer.MAX_VALUE - threshold ? n : Math.min(n, j + threshold);

      // the stripe may lead off of the table if s and t are of different
      // sizes
      if (min > max) {
        return -1;
      }

      // ignore entry left of leftmost
      if (min > 1) {
        d[min - 1] = Integer.MAX_VALUE;
      }

      // iterates through [min, max] in s
      for (int i = min; i <= max; i++) {
        if (left.charAt(i - 1) == rightJ) {
          // diagonally left and up
          d[i] = p[i - 1];
        } else {
          // 1 + minimum of cell to the left, to the top, diagonally
          // left and up
          d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
        }
      }

      // copy current distance counts to 'previous row' distance counts
      tempD = p;
      p = d;
      d = tempD;
    }

    // if p[n] is greater than the threshold, there's no guarantee on it
    // being the correct
    // distance
    if (p[n] <= threshold) {
      return p[n];
    }
    return -1;
  }

  /**
   * Find the Levenshtein distance between two Strings.
   *
   * <p>A higher score indicates a greater distance.
   *
   * <p>The previous implementation of the Levenshtein distance algorithm was from <a
   * href="https://web.archive.org/web/20120526085419/http://www.merriampark.com/ldjava.htm">
   * https://web.archive.org/web/20120526085419/http://www.merriampark.com/ldjava.htm</a>
   *
   * <p>This implementation only need one single-dimensional arrays of length s.length() + 1
   *
   * <pre>
   * unlimitedCompare(null, *)             = IllegalArgumentException
   * unlimitedCompare(*, null)             = IllegalArgumentException
   * unlimitedCompare("","")               = 0
   * unlimitedCompare("","a")              = 1
   * unlimitedCompare("aaapppp", "")       = 7
   * unlimitedCompare("frog", "fog")       = 1
   * unlimitedCompare("fly", "ant")        = 3
   * unlimitedCompare("elephant", "hippo") = 7
   * unlimitedCompare("hippo", "elephant") = 7
   * unlimitedCompare("hippo", "zzzzzzzz") = 8
   * unlimitedCompare("hello", "hallo")    = 1
   * </pre>
   *
   * @param left the first String, must not be null
   * @param right the second String, must not be null
   * @return result distance, or -1
   * @throws IllegalArgumentException if either String input {@code null}
   */
  private static int unlimitedCompare(CharSequence left, CharSequence right) {
    if (left == null || right == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }

    /*
      This implementation use two variable to record the previous cost counts,
      So this implementation use less memory than previous impl.
    */

    int n = left.length(); // length of left
    int m = right.length(); // length of right

    if (n == 0) {
      return m;
    } else if (m == 0) {
      return n;
    }

    if (n > m) {
      // swap the input strings to consume less memory
      final CharSequence tmp = left;
      left = right;
      right = tmp;
      n = m;
      m = right.length();
    }

    final int[] p = new int[n + 1];

    // indexes into strings left and right
    int i; // iterates through left
    int j; // iterates through right
    int upperLeft;
    int upper;

    char rightJ; // jth character of right
    int cost; // cost

    for (i = 0; i <= n; i++) {
      p[i] = i;
    }

    for (j = 1; j <= m; j++) {
      upperLeft = p[0];
      rightJ = right.charAt(j - 1);
      p[0] = j;

      for (i = 1; i <= n; i++) {
        upper = p[i];
        cost = left.charAt(i - 1) == rightJ ? 0 : 1;
        // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
        p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
        upperLeft = upper;
      }
    }

    return p[n];
  }
}
