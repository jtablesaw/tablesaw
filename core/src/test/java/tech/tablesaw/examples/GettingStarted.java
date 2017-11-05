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

package tech.tablesaw.examples;

import static tech.tablesaw.api.ColumnType.*;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

/**
 * Basic example code
 */
public class GettingStarted {

    private ColumnType[] types = {
            LOCAL_DATE,     // date of poll
            INTEGER,        // approval rating (pct)
            CATEGORY        // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
    }

    @Test
    public void printStructure() throws Exception {
        out(table.structure());

        out(table.first(10));

        out(table.summary());

        out(table.columnNames());

        Column approval = table.column("approval");
        out(approval.summary());

        Column who = table.column("who");
        out(who.summary());

        Column date = table.column("date");
        out(date.summary());
    }

    private synchronized void out(Object obj) {
        System.out.println(String.valueOf(obj));
    }

}
