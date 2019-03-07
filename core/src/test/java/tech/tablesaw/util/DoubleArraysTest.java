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

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.TableSliceGroup;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoubleArraysTest {

    @Test
    public void testTo2dArray() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        TableSliceGroup tableSliceGroup = table.splitOn("Scale");
        int columnNuumber = table.columnIndex("Injuries");
        DoubleArrays.to2dArray(tableSliceGroup, columnNuumber);
    }

    @Test
    public void testToN() {
        double[] array = {0, 1, 2};
        assertTrue(Arrays.equals(array, DoubleArrays.toN(3)));
    }

}
