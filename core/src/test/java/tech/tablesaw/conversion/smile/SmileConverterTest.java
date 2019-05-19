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

package tech.tablesaw.conversion.smile;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import smile.classification.RandomForest;
import smile.data.AttributeDataset;
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
    public void regressionWithStratifiedSampleTest() throws IOException {
        Table moneyball = Table.read().csv("../data/baseball.csv");
        Table[] splits = moneyball.stratifiedSampleSplit(moneyball.stringColumn("Team"), 0.6);
        Table stratifiedMoneyBall = splits[0];
        stratifiedMoneyBall.addColumns(stratifiedMoneyBall.numberColumn("RS").subtract(stratifiedMoneyBall.numberColumn("RA")).setName("RD"));
        OLS winsModel = new OLS(stratifiedMoneyBall.select("W", "RD").smile().numericDataset("RD"));
        assertNotNull(winsModel.toString());
    }

    @Test
    public void classification() throws IOException {
        Table moneyball = Table.read().csv("../data/baseball.csv");
        RandomForest playoffsModel = new RandomForest(moneyball.smile().nominalDataset("Playoffs", "RS", "RA", "OBP"), 1);
        assertNotNull(playoffsModel.toString());
    }

    @Test
    public void nominalDatasetToString() throws IOException {
        Table moneyball = Table.read().csv("../data/baseball.csv");
        AttributeDataset dataset = moneyball.smile().nominalDataset("Playoffs", "League", "RS", "RA", "OBP");
        assertNotNull(dataset.toString());
    }

    @Test
    public void columnNames() throws IOException {
        Table moneyball = Table.read().csv("../data/baseball.csv");
        String responseColumnName = "Playoffs";
        String[] variableColumnNames = new String[] { "League", "RS", "RA", "OBP" };
        AttributeDataset dataset = moneyball.smile().nominalDataset(responseColumnName, variableColumnNames);
        String[] resultNames = Arrays.stream(dataset.attributes()).map(attr -> attr.getName()).toArray(String[]::new);
        assertEquals(responseColumnName, dataset.responseAttribute().getName());
        assertArrayEquals(variableColumnNames, resultNames);
    }

}
