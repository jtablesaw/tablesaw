/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.api.plot;

import java.io.IOException;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;

/**
 *
 */
public class BubbleExample {

    public static void main(String[] args) throws IOException {
        Table baseball = Table.read().csv("../data/market_share.csv");
        Table sub = baseball.selectRows(0, 10);
        NumericColumn x = sub.nCol("Products");
        NumericColumn y = sub.nCol("Sales");
        NumericColumn data = sub.nCol("Market_Share");
        Bubble.show("Market Share", x, y, data);
    }

}
