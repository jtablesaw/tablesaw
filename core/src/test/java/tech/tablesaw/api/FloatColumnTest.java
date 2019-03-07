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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FloatColumnTest {

    @Test
    public void testAppendFloat() {
        final FloatColumn floatColumn = FloatColumn.create("floats");
        final Float floatValue = 2.5f;
        floatColumn.append(floatValue);
        assertEquals(floatValue, floatColumn.get(0));
    }

    @Test
    public void testAppendFloatObject() {
        final FloatColumn floatColumn = FloatColumn.create("floats");
        final Float floatObject = 2.5f;
        floatColumn.append(floatObject);
        assertEquals(floatObject, floatColumn.get(0));
    }
}
