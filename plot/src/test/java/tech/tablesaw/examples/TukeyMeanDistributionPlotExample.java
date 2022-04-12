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

import org.apache.commons.math3.distribution.NormalDistribution;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.TukeyMeanDifferencePlot;

/** Illustrates how to create a quantile plot for visualizing a distribution */
public class TukeyMeanDistributionPlotExample {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.read().csv("../data/baseball.csv");
    Table nl = baseball.where(baseball.stringColumn("league").isEqualTo("NL"));
    Table al = baseball.where(baseball.stringColumn("league").isEqualTo("AL"));
    Plot.show(
        TukeyMeanDifferencePlot.create(
            "Wins NL vs AL",
            "wins",
            nl.intColumn("W").asDoubleArray(),
            al.intColumn("W").asDoubleArray()));

    // example with difference sized arrays;
    double[] first = new NormalDistribution().sample(100);
    double[] second = new NormalDistribution().sample(200);
    Plot.show(
        TukeyMeanDifferencePlot.create(
            "Test of different sized arrays", "random data", first, second));
  }
}
