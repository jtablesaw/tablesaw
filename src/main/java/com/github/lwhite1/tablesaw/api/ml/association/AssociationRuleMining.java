package com.github.lwhite1.tablesaw.api.ml.association;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.TemporaryView;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import smile.association.ARM;
import smile.association.AssociationRule;

import java.util.Arrays;
import java.util.List;

/**
 * Association Rule Mining is an unsupervised mining technique related to frequent itemsets
 *
 * Where frequent itemset analysis is concerned only with identifying items that are found together in many baskets,
 * and labeling them with how often they are found. This can be confusing in that there may be some items that are
 * individually very common, and so they appear in the same basket frequently just by chance.
 *
 * Association Rule Mining attempts to identify frequent itemsets that are surprising: That is to say, where the items
 * appear together much more frequently (or less frequently) than one would expect by chance alone
 */
public class AssociationRuleMining {

  private final ARM model;

  public AssociationRuleMining(ShortColumn sets, ShortColumn items, double support) {
    Table temp = Table.create("temp");
    temp.addColumn(sets.copy());
    temp.addColumn(items.copy());
    temp.sortAscendingOn(sets.name(), items.name());

    ViewGroup baskets = temp.splitOn(temp.column(0));
    int[][] itemsets = new int[baskets.size()][];
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

    this.model = new ARM(itemsets, support);
  }

  public AssociationRuleMining(IntColumn sets, CategoryColumn items, double support) {
    Table temp = Table.create("temp");
    temp.addColumn(sets.copy());
    temp.addColumn(items.toIntColumn());
    temp.sortAscendingOn(sets.name(), items.name());

    ViewGroup baskets = temp.splitOn(temp.column(0));
    int[][] itemsets = new int[baskets.size()][];
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

    this.model = new ARM(itemsets, support);
  }

  public List<AssociationRule> learn(double confidenceThreshold) {
    return model.learn(confidenceThreshold);
  }

  public List<AssociationRule> interestingRules(double confidenceThreshold,
                                                double interestThreshold,
                                                Object2DoubleOpenHashMap confidenceMap) {
    List<AssociationRule> rules = model.learn(confidenceThreshold);
    for (AssociationRule rule : rules) {
      double interest = rule.confidence - confidenceMap.getDouble(rule.consequent);
      if (Math.abs(interest) < interestThreshold) {
        rules.remove(rule);
      }
    }
    return rules;
  }

  public Table interest(double confidenceThreshold,
                        double interestThreshold,
                        Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap) {

    Table interestTable = Table.create("Interest");
    interestTable.addColumn(CategoryColumn.create("Antecedent"));
    interestTable.addColumn(CategoryColumn.create("Consequent"));
    interestTable.addColumn(FloatColumn.create("Confidence"));
    interestTable.addColumn(FloatColumn.create("Interest"));

    List<AssociationRule> rules = model.learn(confidenceThreshold);

    for (AssociationRule rule : rules) {
      double interest = rule.confidence - confidenceMap.getDouble(new IntRBTreeSet(rule.consequent));
      if (Math.abs(interest) > interestThreshold) {
        interestTable.categoryColumn(0).addCell(Arrays.toString(rule.antecedent));
        interestTable.categoryColumn(1).addCell(Arrays.toString(rule.consequent));
        interestTable.floatColumn(2).add(rule.confidence);
        interestTable.floatColumn(3).add(interest);
      }
    }
    return interestTable;
  }
}
