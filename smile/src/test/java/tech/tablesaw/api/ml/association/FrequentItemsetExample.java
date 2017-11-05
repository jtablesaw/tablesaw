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
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import smile.association.ItemSet;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.List;

/**
 *
 */
public class FrequentItemsetExample {

    public static void main(String[] args) throws Exception {

        Table table = Table.read().csv(CsvReadOptions
            .builder("../data/movielens.data")
            .separator('\t'));
        out(table.structure());
        out(table.shape());
        ShortColumn movie = table.shortColumn("movie");
        CategoryColumn moviecat = new CategoryColumn("MovieCat");
        for (int i = 0; i < movie.size(); i++) {
            moviecat.appendCell(movie.getString(i));
        }
        table.addColumn(moviecat);

        out(table.shortColumn("user").unique().size());
        out(table.shortColumn("movie").unique().size());

        FrequentItemset model = new FrequentItemset(table.shortColumn("user"), table.categoryColumn("MovieCat"), .24);
        List<ItemSet> itemSetList = model.learn();

        out("Frequent Itemsets");
        for (ItemSet itemSet : itemSetList) {
            if (itemSet.items.length == 2)
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