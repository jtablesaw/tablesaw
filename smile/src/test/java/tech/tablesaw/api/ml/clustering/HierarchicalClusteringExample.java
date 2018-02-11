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

package tech.tablesaw.api.ml.clustering;

import java.util.Arrays;
import tech.tablesaw.api.Table;

/**
 * An example program illustrating the use of hierarchical clustering
 */
public class HierarchicalClusteringExample {

    public static void main(String[] args) throws Exception {

        Table t = Table.read().csv("../data/whiskey.csv");

        HierarchicalClustering model = new HierarchicalClustering(
                LinkageFactory.type.SINGLE,
                t.nCol(2),
                t.nCol(3),
                t.nCol(4),
                t.nCol(5),
                t.nCol(6),
                t.nCol(7),
                t.nCol(8),
                t.nCol(9),
                t.nCol(10),
                t.nCol(11),
                t.nCol(12),
                t.nCol(13)
        );

        out("Clusters when K = 5: " + Arrays.toString(model.partition(5)));
        out("Clusters when Height = 2.5: " + Arrays.toString(model.partition(2.5)));
    }

    private static void out(Object object) {
        System.out.println(String.valueOf(object));
    }

}
