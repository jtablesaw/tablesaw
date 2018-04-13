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

package tech.tablesaw.util;

import com.google.common.collect.Lists;
import org.junit.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSliceGroup;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class DoubleArraysTest {

    @Test
    public void testTo2dArray() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        TableSliceGroup tableSliceGroup = table.splitOn(table.numberColumn("Scale"));
        int columnNuumber = table.columnIndex("Injuries");
        DoubleArrays.to2dArray(tableSliceGroup, columnNuumber);
    }

    @Test
    public void testToN() {
        double[] array = {0, 1, 2};
        assertTrue(Arrays.equals(array, DoubleArrays.toN(3)));
    }

    @Test
    public void testTo2DArray2() {
        double[] array1 = {0, 1, 2};
        double[] array2 = {0, 1, 2};

        double[][] expected = {{0.0, 0.0}, {1.0, 1.0}, {2.0, 2.0}};
        double[][] results = DoubleArrays.to2dArray(array1, array2);
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void testTo2DArray3() {
        double[] array1 = {0, 1, 2};
        double[] array2 = {0, 1, 2};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);

        double[][] expected = {{0.0, 0.0}, {1.0, 1.0}, {2.0, 2.0}};
        double[][] results = DoubleArrays.to2dArray(c1, c2);
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void testTo2DArray4() {
        double[] array1 = {0, 1, 2};
        double[] array2 = {0, 1, 2};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        ArrayList<Column> columnArrayList = Lists.newArrayList(c1, c2);

        double[][] expected = {{0.0, 0.0}, {1.0, 1.0}, {2.0, 2.0}};
        double[][] results = DoubleArrays.to2dArray(columnArrayList);
        assertTrue(Arrays.deepEquals(expected, results));
    }
}