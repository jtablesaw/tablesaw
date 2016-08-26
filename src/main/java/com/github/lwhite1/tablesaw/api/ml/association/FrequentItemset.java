package com.github.lwhite1.tablesaw.api.ml.association;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.TemporaryView;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import smile.association.FPGrowth;
import smile.association.ItemSet;

import java.util.List;

/**
 *
 */
public class FrequentItemset {

  private final FPGrowth model;
  private final int setCount;

  /**
   * Constructs and returns a frequent itemset model
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
    temp.addColumn(items.toIntColumn());
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
    Object2DoubleOpenHashMap<int[]> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

    for (ItemSet itemSet : itemSets) {
      if (itemSet.support >= supportThreshold) {
        confidenceMap.put(itemSet.items, itemSet.support);
      }
    }
    return confidenceMap;
  }

  /**
   * Returns a map of associations and their confidence, where confidence is the P(B | A)
   * @return
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

  public Object2DoubleOpenHashMap<int[]> confidenceMap(double supportThreshold) {

    List<ItemSet> itemSets = learn();
    Object2DoubleOpenHashMap<int[]> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

    for (ItemSet itemSet : itemSets) {
      if (itemSet.support >= supportThreshold) {
        confidenceMap.put(itemSet.items, itemSet.support / (double) setCount);
      }
    }
    return confidenceMap;
  }
}
