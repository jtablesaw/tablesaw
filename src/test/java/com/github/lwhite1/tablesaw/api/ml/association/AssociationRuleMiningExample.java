package com.github.lwhite1.tablesaw.api.ml.association;

import com.github.lwhite1.tablesaw.api.Table;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 *
 */
public class AssociationRuleMiningExample {

  public static void main(String[] args) throws Exception {

    Table table = Table.createFromCsv("data/movielens.data", true, '\t');

    double supportThreshold = .25;
    double confidenceThreshold = .5;
    double interestThreshold = .5;

    AssociationRuleMining model = new AssociationRuleMining(table.shortColumn("user"), table.shortColumn("movie"), supportThreshold);

    FrequentItemset frequentItemsetModel = new FrequentItemset(table.shortColumn("user"), table.shortColumn("movie"), supportThreshold);
    Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap = frequentItemsetModel.confidenceMap();

    Table interestingRuleTable = model.interest(confidenceThreshold, interestThreshold, confidenceMap);

    interestingRuleTable = interestingRuleTable.sortDescendingOn("Interest", "Antecedent");
    out(interestingRuleTable.print());
  }

  private static void out(Object o) {
    System.out.println(String.valueOf(o));
  }
}