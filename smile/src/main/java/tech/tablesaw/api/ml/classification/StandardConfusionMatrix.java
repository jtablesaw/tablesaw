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

package tech.tablesaw.api.ml.classification;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A confusion matrix is used to measure the accuracy of a classifier by counting the number of correct and
 * incorrect values produced when testing the classifier such that the counts are made for every combination of
 * correct and incorrect classification
 */
public class StandardConfusionMatrix implements ConfusionMatrix {

    private final Table<Integer, Integer, Integer> table = TreeBasedTable.create();

    private SortedMap<Integer, Object> labels = new TreeMap<>();

    public StandardConfusionMatrix(SortedSet<Object> labels) {

        int i = 0;
        for (Object object : labels) {
            this.labels.put(i, object);
            i++;
        }
    }

    public void increment(Integer predicted, Integer actual) {
        Integer v = table.get(predicted, actual);
        if (v == null) {
            table.put(predicted, actual, 1);
        } else {
            table.put(predicted, actual, v + 1);
        }
    }

    @Override
    public String toString() {
        return toTable().toString();
    }

    public tech.tablesaw.api.Table toTable() {
        tech.tablesaw.api.Table t = tech.tablesaw.api.Table.create("Confusion Matrix");
        t.addColumn(new CategoryColumn(""));

        // make a set of all the values needed, from the prediction set or the actual set
        TreeSet<Integer> allValues = new TreeSet<>();
        allValues.addAll(table.columnKeySet());
        allValues.addAll(table.rowKeySet());

        for (Integer comparable : allValues) {
            t.addColumn(new IntColumn(String.valueOf(labels.get(comparable))));
            t.column(0).appendCell("Predicted " + labels.get(comparable));
        }

        // put them in a list so they can be accessed by index number
        List<Comparable<?>> valuesList = new ArrayList<>(allValues);
        int n = 0;
        for (int r = 0; r < valuesList.size(); r++) {
            for (int c = 0; c < valuesList.size(); c++) {
                Integer value = table.get(valuesList.get(r), valuesList.get(c));
                if (value == null) {
                    t.intColumn(c + 1).append(0);
                } else {
                    t.intColumn(c + 1).append(value);
                    n = n + value;
                }
            }
        }
        t.column(0).setName("n = " + n);
        for (int c = 1; c <= valuesList.size(); c++) {
            t.column(c).setName("Actual " + labels.get(c - 1));
        }
        return t;
    }

    public double accuracy() {
        int hits = 0;
        int misses = 0;
        for (Table.Cell<Integer, Integer, Integer> cell : table.cellSet()) {
            if (cell.getRowKey().equals(cell.getColumnKey())) {
                hits = hits + cell.getValue();
            } else {
                misses = misses + cell.getValue();
            }
        }
        return hits / ((hits + misses) * 1.0);
    }
}
