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

package tech.tablesaw.api.ml.association;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import smile.association.FPGrowth;
import smile.association.ItemSet;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.TemporaryView;
import tech.tablesaw.table.ViewGroup;

import java.util.List;

/**
 * An unsupervised data mining technique for finding things that 'are found together' frequently.
 * We call the things 'items', and the groups they form baskets, transactions, or just 'sets'
 * <p>
 * Each basket consists of a set of items (an itemset). A set (or subset) of items that appears in many baskets
 * is considered 'frequent'.
 */
public class FrequentItemset {

    private final FPGrowth model;

    // the number of sets (baskets) in the input data
    private final int setCount;

    /**
     * Constructs and returns a frequent itemset model
     *
     * @param sets
     * @param items
     * @param supportThreshold the minimum support required to be included
     */
    public FrequentItemset(IntColumn sets, IntColumn items, double supportThreshold) {

        Table temp = Table.create("temp");
        temp.addColumn(sets.copy());
        temp.addColumn(items.copy());
        temp.sortAscendingOn(sets.name(), items.name());

        ViewGroup baskets = temp.splitOn(temp.column(0));

        this.setCount = baskets.size();

        int[][] itemsets = new int[setCount][];
        int basketIndex = 0;
        for (TemporaryView basket : baskets) {
            IntRBTreeSet set = new IntRBTreeSet(basket.intColumn(1).data());
            int itemIndex = 0;
            itemsets[basketIndex] = new int[set.size()];
            for (int item : set) {
                itemsets[basketIndex][itemIndex] = item;
                itemIndex++;
            }
            basketIndex++;
        }

        this.model = new FPGrowth(itemsets, supportThreshold);
    }

    public FrequentItemset(IntColumn sets, CategoryColumn items, double support) {

        Table temp = Table.create("temp");
        temp.addColumn(sets.copy());
        IntColumn encodedItems = items.asIntColumn();
        encodedItems.setName(items.name());   // Needs t
        temp.addColumn(encodedItems);
        temp.sortAscendingOn(sets.name(), items.name());

        ViewGroup baskets = temp.splitOn(temp.column(0));

        this.setCount = baskets.size();

        int[][] itemsets = new int[setCount][];
        int basketIndex = 0;
        for (TemporaryView basket : baskets) {
            IntRBTreeSet set = new IntRBTreeSet(basket.intColumn(1).data());
            int itemIndex = 0;
            itemsets[basketIndex] = new int[set.size()];
            for (int item : set) {
                itemsets[basketIndex][itemIndex] = item;
                itemIndex++;
            }
            basketIndex++;
        }

        this.model = new FPGrowth(itemsets, support);
    }

    public FrequentItemset(ShortColumn sets, CategoryColumn items, double support) {

        Table temp = Table.create("temp");
        temp.addColumn(sets.copy());
        IntColumn encodedItems = items.asIntColumn();
        encodedItems.setName(items.name());   // Needs t
        temp.addColumn(encodedItems);
        temp.sortAscendingOn(sets.name(), items.name());

        ViewGroup baskets = temp.splitOn(temp.column(0));

        this.setCount = baskets.size();

        int[][] itemsets = new int[setCount][];
        int basketIndex = 0;
        for (TemporaryView basket : baskets) {
            IntRBTreeSet set = new IntRBTreeSet(basket.intColumn(1).data());
            int itemIndex = 0;
            itemsets[basketIndex] = new int[set.size()];
            for (int item : set) {
                itemsets[basketIndex][itemIndex] = item;
                itemIndex++;
            }
            basketIndex++;
        }

        this.model = new FPGrowth(itemsets, support);
    }

    public FrequentItemset(ShortColumn sets, ShortColumn items, double support) {

        Table temp = Table.create("temp");
        temp.addColumn(sets.copy());
        temp.addColumn(items.copy());
        temp.sortAscendingOn(sets.name(), items.name());

        ViewGroup baskets = temp.splitOn(temp.column(0));

        this.setCount = baskets.size();

        int[][] itemsets = new int[setCount][];
        int basketIndex = 0;
        for (TemporaryView basket : baskets) {
            ShortRBTreeSet set = new ShortRBTreeSet(basket.shortColumn(1).data());
            int itemIndex = 0;
            itemsets[basketIndex] = new int[set.size()];
            for (short item : set) {
                itemsets[basketIndex][itemIndex] = item;
                itemIndex++;
            }
            basketIndex++;
        }

        this.model = new FPGrowth(itemsets, support);
    }

    /**
     * Returns a list of ItemSet objects, where each itemset consists of a list of the items that were found together,
     * plus the raw support for the itemset: the number of sets in which the combination appeared in the data
     * given to the model
     */
    public List<ItemSet> learn() {
        return model.learn();
    }

    /**
     * Returns a map of discovered ItemSets and their support, where the support is the number of times the combination
     * appears in the input data
     */
    public Object2IntOpenHashMap<int[]> supportMap() {
        List<ItemSet> itemSets = learn();
        Object2IntOpenHashMap<int[]> confidenceMap = new Object2IntOpenHashMap<>(itemSets.size());

        for (ItemSet itemSet : itemSets) {
            confidenceMap.put(itemSet.items, itemSet.support);
        }
        return confidenceMap;
    }

    /**
     * Returns a map of discovered ItemSets and their support, where the support is the number of times the combination
     * appears in the input data. The map returned contains only those items whose support is greater than the given
     * supportThreshold
     */
    public Object2DoubleOpenHashMap<int[]> supportMap(int supportThreshold) {
        List<ItemSet> itemSets = learn();
        Object2DoubleOpenHashMap<int[]> supportMap = new Object2DoubleOpenHashMap<>(itemSets.size());

        for (ItemSet itemSet : itemSets) {
            if (itemSet.support >= supportThreshold) {
                supportMap.put(itemSet.items, itemSet.support);
            }
        }
        return supportMap;
    }

    /**
     * Returns a map of associations and their confidence, where confidence is support for the itemset (that is, the
     * number of times it appears in the input data) divided by the total number of sets (i.e., the percentage of input
     * sets where it appears.
     */
    public Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap() {

        List<ItemSet> itemSets = learn();
        Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

        for (ItemSet itemSet : itemSets) {
            IntRBTreeSet itemSetCopy = new IntRBTreeSet(itemSet.items);
            confidenceMap.put(itemSetCopy, itemSet.support / (double) setCount);
        }
        return confidenceMap;
    }

    /**
     * Returns a map of associations and their confidence, where confidence is support for the itemset (that is, the
     * number of times it appears in the input data) divided by the total number of sets (i.e., the percentage of input
     * sets where it appears.
     * <p>
     * The map returned includes only those itemsets for which the confidence is above the given threshold
     */
    public Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap(double supportThreshold) {

        List<ItemSet> itemSets = learn();
        Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

        long intSupportThreshold = Math.round(itemSets.size() * supportThreshold);
        for (ItemSet itemSet : itemSets) {
            if (itemSet.support >= intSupportThreshold) {
                IntRBTreeSet itemSetCopy = new IntRBTreeSet(itemSet.items);
                confidenceMap.put(itemSetCopy, itemSet.support / (double) setCount);
            }
        }
        return confidenceMap;
    }
}
