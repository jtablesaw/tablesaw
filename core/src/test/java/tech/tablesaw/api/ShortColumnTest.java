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

package tech.tablesaw.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tech.tablesaw.api.ShortColumn.MISSING_VALUE;

/**
 * Tests for Short columns
 */
public class ShortColumnTest {
    @Test
    public void testGetInt() {
        ShortColumn column = new ShortColumn("Test", new short[]{20, 3245, MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20, column.getInt(0));
        assertEquals("Primitive type conversion error", 3245, column.getInt(1));
        assertEquals("Primitive type conversion error", IntColumn.MISSING_VALUE, column.getInt(2));
        assertEquals("Primitive type conversion error", 234, column.getInt(3));
    }

    @Test
    public void testGetLong() {
        ShortColumn column = new ShortColumn("Test", new short[]{20, 3245, MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20L, column.getLong(0));
        assertEquals("Primitive type conversion error", 3245L, column.getLong(1));
        assertEquals("Primitive type conversion error", LongColumn.MISSING_VALUE, column.getLong(2));
        assertEquals("Primitive type conversion error", 234L, column.getLong(3));
    }

    @Test
    public void testGetFloat() {
        ShortColumn column = new ShortColumn("Test", new short[]{20, 3245, MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20.0, column.getFloat(0), 0.1);
        assertEquals("Primitive type conversion error", 3245.0, column.getFloat(1), 1);
        assertTrue("Primitive type conversion error", Float.isNaN(column.getFloat(2)));
        assertEquals("Primitive type conversion error", 234.0, column.getFloat(3), 0.1);
    }

    @Test
    public void testGetDouble() {
        ShortColumn column = new ShortColumn("Test", new short[]{20, 3245, MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20.0, column.getDouble(0), 0.1);
        assertEquals("Primitive type conversion error", 3245.0, column.getDouble(1), 1);
        assertTrue("Primitive type conversion error", Double.isNaN(column.getDouble(2)));
        assertEquals("Primitive type conversion error", 234.0, column.getDouble(3), 0.1);
    }

    @Test
    public void testCumSum() {
        short[] originalValues = new short[]{32, 42, MISSING_VALUE, 57, 52, -10, 0};
        short[] expectedValues = new short[]{32, 74, MISSING_VALUE, 131, 183, 173, 173};
        ShortColumn initial = new ShortColumn("Test", originalValues);
        ShortColumn csum = initial.cumSum();
        
        assertEquals("Both sets of data should be the same size.", expectedValues.length, csum.size());
        
        for (int index = 0; index < csum.size(); index++) {
            short actual = csum.get(index);
            assertEquals("cumSum() operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }
    }

    @Test
    public void testAdd() {
        short[] originalValues = new short[]{32, 42, 57, 52, -10, 0};
        ShortColumn initial = new ShortColumn("Test", originalValues);
        LongColumn longs = (LongColumn) initial.add(1234L);

        assertEquals("Both sets of data should be the same size.", longs.size(), initial.size());

        for (int index = 0; index < initial.size(); index++) {
            long actual = longs.get(index);
            assertEquals("add() operation at index:" + index + " failed", 1234 + originalValues[index], actual, 0);
        }
    }

    @Test
    public void testPctChange() {
        short[] originalValues = new short[]{ 10, 12, 13 };
        float[] expectedValues = new float[]{ FloatColumn.MISSING_VALUE, 0.2f, 0.083333f };
        ShortColumn initial = new ShortColumn("Test", originalValues);
        FloatColumn pctChange = initial.pctChange();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, pctChange.size());

        for (int index = 0; index < pctChange.size(); index++) {
            double actual = pctChange.get(index);
            assertEquals("pctChange() operation at index:" + index + " failed", expectedValues[index], actual, 0.0001);
        }
    }
}
