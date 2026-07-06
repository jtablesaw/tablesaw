/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LevenshteinDistanceTest {

  /*
   * COVERAGE NOTE:
   * The private method limitedCompare() contains a defensive check for negative thresholds.
   * However, this block is unreachable via public APIs because the LevenshteinDistance
   * constructor already validates and rejects negative thresholds.
   * Testing this unreachable block via Reflection was removed to comply with SonarCloud
   * security rules regarding accessibility modification.
   */

  // =================================================================================
  // 1. STRUCTURAL TESTS - Covering missed branches
  // =================================================================================

  @Test
  void testConstructorWithNegativeThreshold() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new LevenshteinDistance(-1);
        });
  }

  @Test
  void testConstructorWithPositiveThreshold() {
    LevenshteinDistance ld = new LevenshteinDistance(5);
    assertEquals(5, ld.getThreshold());
  }

  @Test
  void testConstructorWithNullThreshold() {
    LevenshteinDistance ld = new LevenshteinDistance(null);
    assertNull(ld.getThreshold());
  }

  @Test
  void testLimitedCompareWithNullStrings() {
    LevenshteinDistance ld = new LevenshteinDistance(5);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ld.apply(null, "abc");
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ld.apply("abc", null);
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ld.apply(null, null);
        });
  }

  @Test
  void testGetDefaultInstanceReturnsSingleton() {
    LevenshteinDistance ld1 = LevenshteinDistance.getDefaultInstance();
    LevenshteinDistance ld2 = LevenshteinDistance.getDefaultInstance();

    assertSame(ld1, ld2, "Must return exactly the same instance (Singleton)");
    assertNull(ld1.getThreshold(), "The default instance must not have a threshold");
  }

  // =================================================================================
  // 2. SPECIFICATION-BASED TESTS - Without Threshold
  // =================================================================================

  @ParameterizedTest(name = "Left: ''{0}'', Right: ''{1}'', Expected: {2}")
  @CsvSource({
    "abc, abc, 0", // P1: Completely equal
    "abc, xyz, 3", // P2: Completely different
    "abc, abcd, 1", // P3: One insertion
    "abcd, abc, 1", // P4: One deletion
    "abc, aXc, 1", // P5: One substitution
    "'', '', 0", // P6: Both empty
    "'', abc, 3", // P7: Left string is empty
    "abc, '', 3", // P8: Right string is empty
    "a, b, 1", // P9: One character each
    "a, a, 0", // P10: One equal character
    "ABC, abc, 3" // P11: Case sensitive
  })
  void testApplyWithoutThreshold(String left, String right, int expectedDistance) {
    // Handle CsvSource sending nulls instead of empty strings correctly
    String safeLeft = (left == null) ? "" : left;
    String safeRight = (right == null) ? "" : right;

    LevenshteinDistance ld = new LevenshteinDistance(); // No threshold limit
    assertEquals(expectedDistance, ld.apply(safeLeft, safeRight));
  }

  // =================================================================================
  // 3. SPECIFICATION-BASED TESTS - With Threshold
  // =================================================================================

  @ParameterizedTest(name = "Threshold: {0}, Left: ''{1}'', Right: ''{2}'', Expected: {3}")
  @CsvSource({
    "5, ab, abcd, 2", // P1: Distance < threshold
    "3, abc, xyz, 3", // P2: Distance == threshold
    "2, abc, abcdef, -1", // P3: Distance > threshold (aborted early)
    "0, a, a, 0", // P4: Threshold = 0, equal strings
    "0, a, b, -1", // P5: Threshold = 0, different strings

    // --- NEW SCENARIOS: Mutant Killers ---

    // Kills the mutants in the MAX_VALUE subtraction and diagonal limits (Threshold < n - 1)
    "1, abcd, abce, 1",
    "1, hello, hallo, 1",

    // Kills Arrays.fill mutants (Official scenarios of the Gusfield/Emerick algorithm)
    "8, aaapppp, '', 7",
    "7, aaapppp, '', 7",
    "6, aaapppp, '', -1",
    "7, elephant, hippo, 7",
    "6, elephant, hippo, -1",
    "7, hippo, elephant, 7",
    "8, hippo, zzzzzzzz, 8"
  })
  void testApplyWithThreshold(int threshold, String left, String right, int expectedResult) {
    // Handle CsvSource sending nulls instead of empty strings correctly
    String safeLeft = (left == null) ? "" : left;
    String safeRight = (right == null) ? "" : right;

    LevenshteinDistance ld = new LevenshteinDistance(threshold);
    assertEquals(expectedResult, ld.apply(safeLeft, safeRight));
  }

  // =================================================================================
  // 4. EXTREME STRUCTURAL TESTS (WHITE BOX) - Targeting 100% Coverage
  // =================================================================================

  @Test
  void testUnlimitedCompareWithNullStrings() {
    LevenshteinDistance ldUnlimited = new LevenshteinDistance(); // No threshold limit

    assertThrows(IllegalArgumentException.class, () -> ldUnlimited.apply(null, "a"));
    assertThrows(IllegalArgumentException.class, () -> ldUnlimited.apply("a", null));
  }

  @Test
  void testLimitedCompareWithEmptyStrings() {
    LevenshteinDistance ld0 = new LevenshteinDistance(0);
    LevenshteinDistance ld1 = new LevenshteinDistance(1);

    // When n == 0 (left string is empty)
    assertEquals(1, ld1.apply("", "a")); // m <= threshold (1 <= 1) -> returns m
    assertEquals(-1, ld0.apply("", "a")); // m <= threshold (1 <= 0) is false -> returns -1

    // When m == 0 (right string is empty)
    assertEquals(1, ld1.apply("a", "")); // n <= threshold (1 <= 1) -> returns n
    assertEquals(-1, ld0.apply("a", "")); // n <= threshold (1 <= 0) is false -> returns -1
  }

  @Test
  void testLimitedCompareWithLeftLongerThanRight() {
    LevenshteinDistance ld = new LevenshteinDistance(5);
    // "abcd" (n=4) > "ab" (m=2)
    assertEquals(2, ld.apply("abcd", "ab"));
  }

  @Test
  void testLimitedCompareWithMaxIntegerThreshold() {
    LevenshteinDistance ld = new LevenshteinDistance(Integer.MAX_VALUE);
    assertEquals(1, ld.apply("a", "b"));
  }
}
