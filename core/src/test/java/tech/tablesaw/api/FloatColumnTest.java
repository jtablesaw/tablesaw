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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.FloatParser;
import tech.tablesaw.selection.Selection;

public class FloatColumnTest {

  private final float[] floatColumnValues = {4, 5, 9.3f, 33.2f, 121, 77};
  private final FloatColumn floatColumn = FloatColumn.create("fc", floatColumnValues);

  @Test
  public void appendFloat() {
    FloatColumn floatColumn = FloatColumn.create("floats");
    Float floatValue = 2.5f;
    floatColumn.append(floatValue);
    assertEquals(floatValue, floatColumn.get(0));
  }

  @Test
  public void appendFloatObject() {
    FloatColumn floatColumn = FloatColumn.create("floats");
    Float floatObject = 2.5f;
    floatColumn.append(floatObject);
    assertEquals(floatObject, floatColumn.get(0));
  }

  @Test
  public void createFromObjectArray() {
    Float[] floats = new Float[] {2.5f, null, 4.0f};
    FloatColumn floatColumn = FloatColumn.create("floats", floats);
    assertEquals(2.5f, floatColumn.get(0));
    assertTrue(floatColumn.isMissing(1));
    assertEquals(4.0f, floatColumn.get(2));
  }

  @Test
  void isIn() {
    Selection result = floatColumn.isIn(4, 40);
    assertEquals(1, result.size());
    assertTrue(floatColumn.where(result).contains(4f));
  }

  @Test
  void isNotIn() {
    Selection result = floatColumn.isNotIn(4, 40);
    assertEquals(5, result.size());
    assertTrue(floatColumn.where(result).contains(5f));
  }

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    FloatParser customParser = new FloatParser(ColumnType.FLOAT);
    customParser.setMissingValueStrings(Arrays.asList("not here"));
    floatColumn.setParser(customParser);

    floatColumn.appendCell("not here");
    assertTrue(floatColumn.isMissing(floatColumn.size() - 1));
    floatColumn.appendCell("5.0");
    assertFalse(floatColumn.isMissing(floatColumn.size() - 1));
  }

  @Test
  public void asSet() {
    final float[] floatColumnValues = {4, 5, 9.3f, 5, 9.3f};
    final FloatColumn floatColumn = FloatColumn.create("fc", floatColumnValues);
    assertEquals(3, floatColumn.asSet().size());
    assertTrue(floatColumn.asSet().contains(4f));
  }
}
