package com.github.lwhite1.tablesaw.api.ml.association;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.TemporaryView;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
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


  public FrequentItemset(IntColumn sets, IntColumn items, double support) {

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

  public List<ItemSet> learn() {
    return model.learn();
  }

  public Object2DoubleOpenHashMap<int[]> supportMap() {
    List<ItemSet> itemSets = learn();
    Object2DoubleOpenHashMap<int[]> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

    for (ItemSet itemSet : itemSets) {
      confidenceMap.put(itemSet.items, itemSet.support);
    }
    return confidenceMap;
  }

  public Object2DoubleOpenHashMap<int[]> supportMap(double supportThreshold) {
    List<ItemSet> itemSets = learn();
    Object2DoubleOpenHashMap<int[]> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

    for (ItemSet itemSet : itemSets) {
      if (itemSet.support >= supportThreshold) {
        confidenceMap.put(itemSet.items, itemSet.support);
      }
    }
    return confidenceMap;
  }

  public Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap() {

    List<ItemSet> itemSets = learn();
    Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap = new Object2DoubleOpenHashMap<>(itemSets.size());

    for (ItemSet itemSet : itemSets) {
      //ImmutableSet<Integer> immutableItemSet = new ImmutableSet.Builder().add(itemSet.items);
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
