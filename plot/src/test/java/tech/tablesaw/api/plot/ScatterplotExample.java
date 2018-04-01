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

package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;

/**
 *
 */
public class ScatterplotExample {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");
        NumberColumn x = baseball.nCol("BA");
        NumberColumn y = baseball.nCol("W");
        Scatter.show(x, y);

        Scatter.show("Regular season wins by year",
                baseball.numberColumn("W"),
                baseball.numberColumn("Year"),
                baseball.splitOn(baseball.categoricalColumn("Playoffs")));
    }
}