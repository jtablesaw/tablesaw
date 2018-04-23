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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public class IntComparatorChain implements IntComparator, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<IntComparator> comparatorChain;
    private BitSet orderingBits;
    private boolean isLocked;

    public IntComparatorChain(IntComparator comparator) {
        this(comparator, false);
    }

    private IntComparatorChain(IntComparator comparator, boolean reverse) {
        this.orderingBits = null;
        this.isLocked = false;
        this.comparatorChain = new ArrayList<>(1);
        this.comparatorChain.add(comparator);
        this.orderingBits = new BitSet(1);
        if (reverse) {
            this.orderingBits.set(0);
        }
    }

    public void addComparator(IntComparator comparator) {
        this.addComparator(comparator, false);
    }

    private void addComparator(IntComparator comparator, boolean reverse) {
        this.comparatorChain.add(comparator);
        if (reverse) {
            this.orderingBits.set(this.comparatorChain.size() - 1);
        }
    }

    public int size() {
        return this.comparatorChain.size();
    }

    private void checkChainIntegrity() {
        if (this.comparatorChain.size() == 0) {
            throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
        }
    }

    public int compare(int o1, int o2) throws UnsupportedOperationException {
        if (!this.isLocked) {
            this.checkChainIntegrity();
            this.isLocked = true;
        }

        Iterator<IntComparator> comparators = this.comparatorChain.iterator();

        for (int comparatorIndex = 0; comparators.hasNext(); ++comparatorIndex) {
            IntComparator comparator = comparators.next();
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

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (null == object) {
            return false;
        } else if (!object.getClass().equals(this.getClass())) {
            return false;
        } else {
            label48: {
                label32: {
                    IntComparatorChain chain = (IntComparatorChain) object;
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
}
