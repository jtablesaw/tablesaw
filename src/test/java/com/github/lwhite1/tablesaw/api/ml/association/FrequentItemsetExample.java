package com.github.lwhite1.tablesaw.api.ml.association;

import com.github.lwhite1.tablesaw.api.Table;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import smile.association.ItemSet;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class FrequentItemsetExample {

  public static void main(String[] args) throws Exception {

    Table table = Table.createFromCsv("data/movielens.data", true, '\t');
    out(table.structure().print());
    FrequentItemset model = new FrequentItemset(table.shortColumn("user"), table.shortColumn("movie"), .24);
    List<ItemSet> itemSetList = model.learn();
    for (ItemSet itemSet : itemSetList) {
      if(itemSet.items.length == 2)
        out(itemSet);
    }

    Object2DoubleOpenHashMap<int[]> confidenceMap = model.confidenceMap(.20);
    Object2DoubleMap.FastEntrySet<int[]> entrySet = confidenceMap.object2DoubleEntrySet();
    for (Object2DoubleMap.Entry<int[]> entry : entrySet) {
      out(Arrays.toString(entry.getKey()) + " : " + entry.getDoubleValue());
    }
  }

  private static void out(Object o) {
    System.out.println(String.valueOf(o));
  }
}