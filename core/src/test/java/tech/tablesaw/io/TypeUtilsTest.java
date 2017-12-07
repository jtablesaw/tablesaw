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

package tech.tablesaw.io;

import java.time.LocalDate;
import java.util.Locale;
import org.junit.Test;
import tech.tablesaw.api.DoubleColumn;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.tablesaw.api.ColumnType.DOUBLE;

public class TypeUtilsTest {

    /**
     * Test would throw ClassCastException if method does not work properly
     */
    @Test
    public void testNewColumn() {
        DoubleColumn column = (DoubleColumn) TypeUtils.newColumn("test", DOUBLE);
        assertThat(column, notNullValue());
    }
    
    @Test
    public void testDateFormaterWithLocaleEN() {
        String anotherDate = "12-May-2015";
        LocalDate result = LocalDate.parse(anotherDate, TypeUtils.DATE_FORMATTER.withLocale(Locale.ENGLISH));
        assertThat(result, notNullValue());
    }
}