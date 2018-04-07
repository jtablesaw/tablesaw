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

package tech.tablesaw.filters;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.StringColumn;

import static org.junit.Assert.*;

public class StringFiltersTest {

    private StringColumn sc1 = StringColumn.create("sc1");

    @Before
    public void setUp() {
        sc1.append("apple");      // 0
        sc1.append("Banana");     // 1
        sc1.append("cherry");
        sc1.append("diamond");    // 3
        sc1.append("elephant");
        sc1.append("fiscal");     // 5
        sc1.append("fish");
        sc1.append("apple wine"); // 7
        sc1.append("Santana");
        sc1.append("dog");        // 9
        sc1.append("dogmatic");
        sc1.append("1001");       // 11
        sc1.append("10");
        sc1.append("10 days ago"); // 13
        sc1.append("cran apple");
        sc1.append("8 * five");    // 15
        sc1.append("101ers");
        sc1.append("UPPERCASE");   // 17
        sc1.append("");
    }

    @Test
    public void testEqualsIgnoreCase() {
        assertTrue(sc1.equalsIgnoreCase("APPLE").contains(0));
    }

    @Test
    public void testStartsWith() {
        assertTrue(sc1.startsWith("dog").contains(9));
        assertTrue(sc1.startsWith("dog").contains(10));
    }

    @Test
    public void testEndsWith() {
        assertTrue(sc1.endsWith("dog").contains(9));
        assertFalse(sc1.endsWith("dog").contains(10));
    }

    @Test
    public void testContainsString() {
        assertTrue(sc1.containsString("eph").contains(4));
        assertFalse(sc1.containsString("eph").contains(10));
    }

    @Test
    public void testMatchesRegex() {
        assertTrue(sc1.matchesRegex("^apple").contains(0));
        assertFalse(sc1.matchesRegex("^apple").contains(7));
        assertFalse(sc1.matchesRegex("^apple").contains(10));
        assertFalse(sc1.matchesRegex("^apple").contains(14));
    }

    @Test
    public void testIsAlpha() {
        assertTrue(sc1.isAlpha().contains(4));
        assertFalse(sc1.isAlpha().contains(11));
        assertFalse(sc1.isAlpha().contains(13));
    }

    @Test
    public void testIsNumeric() {
        assertFalse(sc1.isNumeric().contains(4));
        assertTrue(sc1.isNumeric().contains(11));
        assertFalse(sc1.isNumeric().contains(13));
    }

    @Test
    public void testIsAlphaNumeric() {
        assertTrue(sc1.isAlphaNumeric().contains(4));
        assertTrue(sc1.isAlphaNumeric().contains(11));
        assertFalse(sc1.isAlphaNumeric().contains(13));
        assertFalse(sc1.isAlphaNumeric().contains(15));
        assertTrue(sc1.isAlphaNumeric().contains(16));
    }

    @Test
    public void testIsUpperCase() {
        assertFalse(sc1.isUpperCase().contains(4));
        assertFalse(sc1.isUpperCase().contains(13));
        assertTrue(sc1.isUpperCase().contains(17));
    }

    @Test
    public void testIsLowerCase() {
        assertTrue(sc1.isLowerCase().contains(4));
        assertFalse(sc1.isLowerCase().contains(17));
    }

    @Test
    public void testLengthEquals() {
        assertTrue(sc1.lengthEquals(5).contains(0));
        assertFalse(sc1.lengthEquals(5).contains(8));
    }

    @Test
    public void testIsShorterThan() {
        assertTrue(sc1.isShorterThan(5).contains(6));
        assertFalse(sc1.isShorterThan(5).contains(0));
    }

    @Test
    public void testIsLongerThan() {
        assertTrue(sc1.isLongerThan(5).contains(1));
        assertFalse(sc1.isLongerThan(5).contains(0));
    }

    @Test
    public void testIsIn() {
        assertTrue(sc1.isIn("diamond", "dog", "canary").contains(3));
        assertFalse(sc1.isIn("diamond", "dog", "canary").contains(8));
        assertTrue(sc1.isIn("diamond", "dog", "canary").contains(9));
    }

    @Test
    public void testIsNotIn() {
        assertFalse(sc1.isNotIn("diamond", "dog", "canary").contains(3));
        assertTrue(sc1.isNotIn("diamond", "dog", "canary").contains(8));
        assertFalse(sc1.isNotIn("diamond", "dog", "canary").contains(9));
    }
    @Test
    public void testIsMissing() {
        assertFalse(sc1.isMissing().contains(3));
        assertTrue(sc1.isMissing().contains(18));
    }

    @Test
    public void testIsNotMissing() {
        assertTrue(sc1.isNotMissing().contains(3));
        assertFalse(sc1.isNotMissing().contains(18));
    }

    @Test
    public void testIsEqualTo() {
        assertTrue(sc1.isEqualTo("10").contains(12));
        assertFalse(sc1.isEqualTo("10").contains(13));
    }
}
