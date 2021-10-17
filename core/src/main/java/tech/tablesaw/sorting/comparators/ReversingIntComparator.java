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

package tech.tablesaw.sorting.comparators;

import it.unimi.dsi.fastutil.ints.IntComparator;
import javax.annotation.concurrent.Immutable;

/** A Comparator for int primitives that takes an input comparator and creates its opposite */
@Immutable
public final class ReversingIntComparator implements IntComparator {

  private final IntComparator intComparator;

  /** Constructs a comparator that sorts in reverse order of the argument */
  private ReversingIntComparator(IntComparator intComparator) {
    this.intComparator = intComparator;
  }

  /** Returns a comparator that sorts in reverse order of the argument */
  public static IntComparator reverse(IntComparator intComparator) {
    return new ReversingIntComparator(intComparator);
  }

  @Override
  public int compare(int i, int i1) {
    return -intComparator.compare(i, i1);
  }
}
