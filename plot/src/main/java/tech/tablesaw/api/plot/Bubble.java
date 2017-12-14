/*
 * Copyright 2017 mario.schroeder.
 *
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

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotting.xchart.XchartBubble;

/**
 *
 * Interface to render a bubble plot.
 */
public class Bubble {

    public static void show(NumericColumn x, NumericColumn y, NumericColumn data) {

        show("", x, y, data);
    }

    public static void show(String chartTitle, NumericColumn x, NumericColumn y, NumericColumn data) {

        show(chartTitle, x.toDoubleArray(), y.toDoubleArray(), data.toDoubleArray());
    }
    
    public static void show(double[] x, double[] y, double[] data) {

        show("", x, y, data);
    }

    public static void show(String chartTitle, double[] x, double[] y, double[] data) {

        new XchartBubble().show(chartTitle, x, "", y, "", data);
    }
}
