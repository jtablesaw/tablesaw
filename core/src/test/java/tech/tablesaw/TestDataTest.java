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

package tech.tablesaw;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Make sure our test data is available
 */
public class TestDataTest {

    @Test
    public void verifyAllTestData() {
        for (TestData testData : TestData.values()) {
            verify(testData);
        }
    }

    private void verify(TestData testData) {

        assertNotNull("Table available", testData.getTable());

        // cheap attempt at testing data integrity
        assertEquals("Column name count matches column type count", testData.getColumnNames().length,
                testData.getColumnTypes().length);

        assertNotNull("Data path available", testData.getSource());
    }

}
