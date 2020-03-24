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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

  @Test
  public void testRemoveZeroDecimal() {
    // Assert zero decimals being removed
    assertEquals("246", StringUtils.removeZeroDecimal("246.0"));
    assertEquals("146", StringUtils.removeZeroDecimal("146.00"));
    assertEquals("357", StringUtils.removeZeroDecimal("357.000"));
    assertEquals("347", StringUtils.removeZeroDecimal("347.0000"));

    // Assert no change to input value
    assertEquals("468", StringUtils.removeZeroDecimal("468"));
    assertEquals("24", StringUtils.removeZeroDecimal("24"));
    assertEquals("468.02", StringUtils.removeZeroDecimal("468.02"));
    assertEquals("246.004", StringUtils.removeZeroDecimal("246.004"));
    assertEquals("246.4000", StringUtils.removeZeroDecimal("246.4000"));

    // Assert empty string and null handling
    assertEquals("", StringUtils.removeZeroDecimal(""));
    assertNull(StringUtils.removeZeroDecimal(null));
  }
}
