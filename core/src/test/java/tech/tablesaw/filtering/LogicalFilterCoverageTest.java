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

package tech.tablesaw.filtering;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class LogicalFilterCoverageTest {

  private Table dummyTable;

  private Function<Table, Selection> selectAll;
  private Function<Table, Selection> selectNone;
  private Function<Table, Selection> select012;
  private Function<Table, Selection> select123;
  private Function<Table, Selection> select01;
  private Function<Table, Selection> select23;
  private Function<Table, Selection> select02;

  @BeforeEach
  public void setUp() {
    dummyTable = Table.create("Dummy", IntColumn.create("id", new int[] {10, 20, 30, 40}));

    selectAll = t -> Selection.with(0, 1, 2, 3);
    selectNone = t -> Selection.with(); // vazio
    select012 = t -> Selection.with(0, 1, 2);
    select123 = t -> Selection.with(1, 2, 3);
    select01 = t -> Selection.with(0, 1);
    select23 = t -> Selection.with(2, 3);
    select02 = t -> Selection.with(0, 2);
  }

  @Test
  public void testAndWithEmptyArguments() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new And());
    assertTrue(ex.getMessage().contains("length 1 or greater"));
  }

  @Test
  public void testOrWithEmptyArguments() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Or());
    assertTrue(ex.getMessage().contains("length 1 or greater"));
  }

  @Test
  public void testAndAllAndAll() {
    And andFilter = new And(selectAll, selectAll);
    Selection result = andFilter.apply(dummyTable);
    assertArrayEquals(new int[] {0, 1, 2, 3}, result.toArray());
  }

  @Test
  public void testAndOverlap() {
    And andFilter = new And(select012, select123);
    Selection result = andFilter.apply(dummyTable);
    assertArrayEquals(new int[] {1, 2}, result.toArray());
  }

  @Test
  public void testAndNoOverlap() {
    And andFilter = new And(select01, select23);
    Selection result = andFilter.apply(dummyTable);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testOrOverlap() {
    Or orFilter = new Or(select012, select123);
    Selection result = orFilter.apply(dummyTable);
    assertArrayEquals(new int[] {0, 1, 2, 3}, result.toArray());
  }

  @Test
  public void testOrNoOverlap() {
    Or orFilter = new Or(select01, select23);
    Selection result = orFilter.apply(dummyTable);
    assertArrayEquals(new int[] {0, 1, 2, 3}, result.toArray());
  }

  @Test
  public void testNotAll() {
    Not notFilter = new Not(selectAll);
    Selection result = notFilter.apply(dummyTable);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testNotNone() {
    Not notFilter = new Not(selectNone);
    Selection result = notFilter.apply(dummyTable);
    assertArrayEquals(new int[] {0, 1, 2, 3}, result.toArray());
  }

  @Test
  public void testNotPartial() {
    Not notFilter = new Not(select02);
    Selection result = notFilter.apply(dummyTable);
    assertArrayEquals(new int[] {1, 3}, result.toArray());
  }

  @Test
  public void testNotDoubleNegation() {
    Not doubleNotFilter = new Not(new Not(select01));
    Selection result = doubleNotFilter.apply(dummyTable);
    assertArrayEquals(new int[] {0, 1}, result.toArray());
  }
}
