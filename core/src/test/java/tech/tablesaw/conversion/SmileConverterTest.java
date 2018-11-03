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

package tech.tablesaw.conversion;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import smile.classification.RandomForest;
import smile.regression.OLS;
import tech.tablesaw.api.Table;

public class SmileConverterTest {

    @Test
    public void regression() throws IOException {
	Table moneyball = Table.read().csv("../data/baseball.csv");
	moneyball.addColumns(moneyball.numberColumn("RS").subtract(moneyball.numberColumn("RA")).setName("RD"));
	OLS winsModel = new OLS(moneyball.select("W", "RD").smile().numericDataset("RD"));
	assertNotNull(winsModel.toString());
    }

    @Test
    public void classification() throws IOException {
	Table moneyball = Table.read().csv("../data/baseball.csv");
	RandomForest playoffsModel = new RandomForest(moneyball.smile().nominalDataset("Playoffs", "RS", "RA", "OBP"), 1);
	assertNotNull(playoffsModel.toString());
    }

}
