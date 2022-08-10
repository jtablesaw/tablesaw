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

package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.DoubleParser;

class DoubleParserTest {
  @Test
  void testCanParse1() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertTrue(doubleParser.canParse("1.3"));
  }

  @Test
  void testCanParse2() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertTrue(doubleParser.canParse("1.3%"));
  }

  @Test
  void testCanParse3() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertFalse(doubleParser.canParse("%"));
  }

  @Test
  void testCanParse4() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertFalse(doubleParser.canParse(","));
  }

  @Test
  void testCanParse5() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertFalse(doubleParser.canParse("0% - 25%"));
  }

  @Test
  void testParseDouble1() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertEquals(1.3, doubleParser.parseDouble("1.3"));
  }

  @Test
  void testParseDouble2() {
    DoubleParser doubleParser = new DoubleParser(ColumnType.DOUBLE);
    assertEquals(0.012, doubleParser.parseDouble("1.2%"));
  }
}
