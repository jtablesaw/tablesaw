package tech.tablesaw.api.ml.association;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.ml.association.AssociationRuleMining;
import tech.tablesaw.api.ml.association.FrequentItemset;
import tech.tablesaw.io.csv.CsvReadOptions;

/**
 *
 */
public class AssociationRuleMiningExample {

    public static void main(String[] args) throws Exception {

        Table table = Table.read().csv(CsvReadOptions
            .builder("../data/movielens.data")
            .separator('\t'));

        double supportThreshold = .25;
        double confidenceThreshold = .5;
        double interestThreshold = .5;

        AssociationRuleMining model = new AssociationRuleMining(table.shortColumn("user"), table.shortColumn("movie")
                , supportThreshold);

        FrequentItemset frequentItemsetModel = new FrequentItemset(table.shortColumn("user"), table.shortColumn
                ("movie"), supportThreshold);
        Object2DoubleOpenHashMap<IntRBTreeSet> confidenceMap = frequentItemsetModel.confidenceMap();

        Table interestingRuleTable = model.interest(confidenceThreshold, interestThreshold, confidenceMap);

        interestingRuleTable = interestingRuleTable.sortDescendingOn("Interest", "Antecedent");
        out(interestingRuleTable);
    }

    private static void out(Object o) {
        System.out.println(String.valueOf(o));
    }
}