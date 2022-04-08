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

package tech.tablesaw.joining;

import java.util.*;
import tech.tablesaw.api.Row;

/**
 * A comparator for comparing two Row objects on one or more Columns. The two rows are expected to
 * be from different tables, but they should have the same number and type of Columns being used for
 * their sorts The implementation is based on Apache Commons Collections
 */
class RowComparatorChain implements Comparator<Row> {

  private final List<Comparator<Row>> comparatorChain;
  private BitSet orderingBits;
  private boolean isLocked;

  /** Constructs a comparator chain with the argument as the first node in the chain */
  public RowComparatorChain(Comparator<Row> comparator) {
    this(comparator, false);
  }

  private RowComparatorChain(Comparator<Row> comparator, boolean reverse) {
    this.orderingBits = null;
    this.isLocked = false;
    this.comparatorChain = new ArrayList<>(1);
    this.comparatorChain.add(comparator);
    this.orderingBits = new BitSet(1);
    if (reverse) {
      this.orderingBits.set(0);
    }
  }

  /** Appends the comparator to the end of the chain */
  public void addComparator(Comparator<Row> comparator) {
    this.comparatorChain.add(comparator);
  }

  /** Returns the number of comparators in the chain */
  public int size() {
    return this.comparatorChain.size();
  }

  private void checkChainIntegrity() {
    if (this.comparatorChain.isEmpty()) {
      throw new UnsupportedOperationException(
          "ComparatorChains must contain at least one Comparator");
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws UnsupportedOperationException
   */
  @Override
  public int compare(Row o1, Row o2) throws UnsupportedOperationException {
    if (!this.isLocked) {
      this.checkChainIntegrity();
      this.isLocked = true;
    }

    Iterator<Comparator<Row>> comparators = this.comparatorChain.iterator();

    for (int comparatorIndex = 0; comparators.hasNext(); ++comparatorIndex) {
      Comparator<Row> comparator = comparators.next();
      int retval = comparator.compare(o1, o2);
      if (retval != 0) {
        if (this.orderingBits.get(comparatorIndex)) {
          if (retval > 0) {
            retval = -1;
          } else {
            retval = 1;
          }
        }
        return retval;
      }
    }
    return 0;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    if (null != this.comparatorChain) {
      hash ^= this.comparatorChain.hashCode();
    }

    if (null != this.orderingBits) {
      hash ^= this.orderingBits.hashCode();
    }
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    } else if (null == object) {
      return false;
    } else if (!object.getClass().equals(this.getClass())) {
      return false;
    } else {
      label48:
      {
        label32:
        {
          RowComparatorChain chain = (RowComparatorChain) object;
          if (null == this.orderingBits) {
            if (null != chain.orderingBits) {
              break label32;
            }
          } else if (!this.orderingBits.equals(chain.orderingBits)) {
            break label32;
          }

          if (null == this.comparatorChain) {
            if (null == chain.comparatorChain) {
              break label48;
            }
          } else if (this.comparatorChain.equals(chain.comparatorChain)) {
            break label48;
          }
        }
        return false;
      }
      return true;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RowComparatorChain{");
    sb.append("comparatorChain=").append(comparatorChain);
    sb.append('}');
    return sb.toString();
  }
}
