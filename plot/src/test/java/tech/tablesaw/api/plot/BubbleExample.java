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

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.io.IOException;

/**
 *
 */
public class BubbleExample {

    public static void main(String[] args) throws IOException {
        Table marketShare = Table.read().csv("../data/market_share.csv");
        Table sub = marketShare.where(Selection.withRange(0, 4));
        NumberColumn x = sub.nCol("Products");
        NumberColumn y = sub.nCol("Sales");
        NumberColumn data = sub.nCol("Market_Share");
        Bubble.show("Market Share", x, y, data);
    }

}
