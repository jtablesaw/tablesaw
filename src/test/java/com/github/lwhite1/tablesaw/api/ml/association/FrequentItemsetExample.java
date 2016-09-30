package com.github.lwhite1.tablesaw.api.ml.association;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import smile.association.ItemSet;

import java.util.List;

/**
 *
 */
public class FrequentItemsetExample {

  public static void main(String[] args) throws Exception {

    Table table = Table.createFromCsv("data/movielens.data", true, '\t');
    out(table.structure().print());
    out(table.shape());
    ShortColumn movie = table.shortColumn("movie");
    CategoryColumn moviecat = CategoryColumn.create("MovieCat");
    for (int i = 0; i < movie.size(); i++) {
      moviecat.addCell(movie.getString(i));
    }
    table.addColumn(moviecat);

    out(table.shortColumn("user").unique().size());
    out(table.shortColumn("movie").unique().size());

    FrequentItemset model = new FrequentItemset(table.shortColumn("user"), table.categoryColumn("MovieCat"), .24);
    List<ItemSet> itemSetList = model.learn();

    out("Frequent Itemsets");
    for (ItemSet itemSet : itemSetList) {
      if(itemSet.items.length == 2)
        out(itemSet);
    }

    out(model.supportMap(250));

    Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap = model.confidenceMap(.90);
    Object2DoubleMap.FastEntrySet<IntRBTreeSet> entrySet = confidenceMap.object2DoubleEntrySet();

    out("");
    out("Confidence Map");
    for (Object2DoubleMap.Entry<IntRBTreeSet> entry : entrySet) {
      out(entry.getKey() + " : " + entry.getDoubleValue());
    }

    Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap2 = model.confidenceMap();
    Object2DoubleMap.FastEntrySet<IntRBTreeSet> entrySet2 = confidenceMap2.object2DoubleEntrySet();

    out("");
    out("Confidence Map2");
    for (Object2DoubleMap.Entry<IntRBTreeSet> entry2 : entrySet2) {
      out(entry2.getKey() + " : " + entry2.getDoubleValue());
    }
  }

  private static void out(Object o) {
    System.out.println(String.valueOf(o));
  }
}