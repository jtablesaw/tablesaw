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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DoubleColumnTest {

  @Test
  public void createFromList() {
    List<Double> list = new ArrayList<>();
    list.add(2.0);
    DoubleColumn col = DoubleColumn.create("test", list);
    assertArrayEquals(new double[] {2.0}, col.asDoubleArray());
  }

  @Test
  public void createFromObjectArray() {
    Double[] input = new Double[] {2.0, 6.0};
    DoubleColumn col = DoubleColumn.create("test", input);
    assertArrayEquals(new double[] {2.0, 6.0}, col.asDoubleArray());
  }

  @Test
  public void unique() {
    DoubleColumn uniq = DoubleColumn.create("test", 5, 4, 3, 2, 1, 5, 4, 3, 2, 1).unique();
    double[] arr = uniq.asDoubleArray();
    Arrays.sort(arr);
    assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, arr);
  }

  @Test
  public void createThenSortAscending() {
    DoubleColumn col = DoubleColumn.create("test");
    col.append(3.0).append(1.0).append(2.0).append(4.0);
    col.sortAscending();
    assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0}, col.asDoubleArray());
  }

  @Test
  public void sortAscending() {
    DoubleColumn col = DoubleColumn.create("test", 3.0, 1.0, 2.0, 4.0);
    col.sortAscending();
    assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0}, col.asDoubleArray());
  }

  @Test
  public void uniqueThenSort() {
    DoubleColumn uniq = DoubleColumn.create("test", 5, 4, 3, 2, 1, 5, 4, 3, 2, 1).unique();
    uniq.sortAscending();
    assertArrayEquals(new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, uniq.asDoubleArray());
  }
}
