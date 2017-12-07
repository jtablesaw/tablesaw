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

package tech.tablesaw.index;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.FloatColumnUtils;
import tech.tablesaw.index.FloatIndex;
import tech.tablesaw.util.Selection;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class FloatIndexTest {

    private FloatIndex index;
    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv("../data/bus_stop_test.csv");
        index = new FloatIndex(table.floatColumn("stop_lat"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isEqualTo, 30.330425f);
        Selection fromIdx = index.get(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isGreaterThanOrEqualTo, 30.330425f);
        Selection fromIdx = index.atLeast(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isLessThanOrEqualTo, 30.330425f);
        Selection fromIdx = index.atMost(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isLessThan, 30.330425f);
        Selection fromIdx = index.lessThan(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isGreaterThan, 30.330425f);
        Selection fromIdx = index.greaterThan(30.330425f);
        assertEquals(fromCol, fromIdx);
    }
}