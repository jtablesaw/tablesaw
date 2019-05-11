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
package tech.tablesaw.examples;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BubblePlot;
import tech.tablesaw.plotly.components.Figure;

import java.io.IOException;

/**
 *
 */
public class BubbleExample2 {

    public static void main(String[] args) throws IOException {

        Table wines = Table.read().csv("../data/test_wines.csv");

        Table champagne =
                wines.where(
                        wines.stringColumn("wine type").isEqualTo("Champagne & Sparkling")
                                .and(wines.stringColumn("region").isEqualTo("California")));

        Figure figure = BubblePlot.create("Average retail price for champagnes by year and rating",
                champagne,           // table name
                "highest pro score", // x variable column name
                "year",              // y variable column name
                "Mean Retail"        // bubble size
        );

        Plot.show(figure);
    }
}