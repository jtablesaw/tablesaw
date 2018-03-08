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
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import smile.association.ARM;
import smile.association.AssociationRule;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.TemporaryView;
import tech.tablesaw.table.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * Association Rule Mining is an unsupervised mining technique related to frequent itemsets
 * <p>
 * Where frequent itemset analysis is concerned only with identifying items that are found together in many baskets,
 * and labeling them with how often they are found. This can be confusing in that there may be some items that are
 * individually very common, and so they appear in the same basket frequently just by chance.
 * <p>
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
        temp.addColumn(items.asIntColumn());
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
                                                  Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap) {
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
        interestTable.addColumn(new CategoryColumn("Antecedent"));
        interestTable.addColumn(new CategoryColumn("Consequent"));
        interestTable.addColumn(new FloatColumn("Confidence"));
        interestTable.addColumn(new FloatColumn("Interest"));

        List<AssociationRule> rules = model.learn(confidenceThreshold);

        for (AssociationRule rule : rules) {
            double interest = rule.confidence - confidenceMap.getDouble(new IntRBTreeSet(rule.consequent));
            if (Math.abs(interest) > interestThreshold) {
                interestTable.categoryColumn(0).appendCell(Arrays.toString(rule.antecedent));
                interestTable.categoryColumn(1).appendCell(Arrays.toString(rule.consequent));
                interestTable.floatColumn(2).append(rule.confidence);
                interestTable.floatColumn(3).append(interest);
            }
        }
        return interestTable;
    }
}
