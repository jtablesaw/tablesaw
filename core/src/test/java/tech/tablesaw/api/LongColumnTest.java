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

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.filtering.IntPredicate;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Long columns
 */
public class LongColumnTest {

    private LongColumn longColumn;

    @Before
    public void setUp() throws Exception {
        longColumn = new LongColumn("t1");
    }

    @Test
    public void testDifference() {
        long[] originalValues = new long[]{32, 42, 40, 57, 52};
        long[] expectedValues = new long[]{LongColumn.MISSING_VALUE, 10, -2, 17, -5};

        LongColumn initial = new LongColumn("Test", originalValues.length);
        Arrays.stream(originalValues).forEach(initial::append);

        LongColumn difference = initial.difference();
        assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());

        for (int index = 0; index < difference.size(); index++) {
            long actual = difference.get(index);
            assertEquals("difference operation at index:" + index + " failed", expectedValues[index], actual);
        }
    }
}
