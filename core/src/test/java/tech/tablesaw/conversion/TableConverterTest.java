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

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableConverterTest {

    @Test
    public void asDoubleMatrix() {
        double[] array1 = {0, 1, 2};
        double[] array2 = {0, 1, 2};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        Table table = Table.create("test", c1, c2);

        double[][] expected = {{0.0, 0.0}, {1.0, 1.0}, {2.0, 2.0}};
        double[][] results = table.as().doubleMatrix();
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void asDoubleMatrixColArgs() {
        double[] array1 = {0, 1, 1};
        double[] array2 = {0, 1, 2};
        double[] array3 = {0, 1, 3};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        DoubleColumn c3 = DoubleColumn.create("3", array3);
        Table table = Table.create("test", c1, c2, c3);

        double[][] expected = {{0.0, 0.0}, {1.0, 1.0}, {1.0, 3.0}};
        double[][] results = table.as().doubleMatrix("1", "3");
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void asIntMatrix() {
        double[] array1 = {0, 1, 2};
        double[] array2 = {0, 1, 2};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        Table table = Table.create("test", c1, c2);

        int[][] expected = {{0, 0}, {1, 1}, {2, 2}};
        int[][] results = table.as().intMatrix();
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void asIntMatrixColArgs() {
        double[] array1 = {0, 1, 1};
        double[] array2 = {0, 1, 2};
        double[] array3 = {0, 1, 3};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        DoubleColumn c3 = DoubleColumn.create("3", array3);
        Table table = Table.create("test", c1, c2, c3);

        int[][] expected = {{0, 0}, {1, 1}, {1, 3}};
        int[][] results = table.as().intMatrix("1", "3");
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void asFloatMatrix() {
        double[] array1 = {0, 1, 2};
        double[] array2 = {0, 1, 2};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        Table table = Table.create("test", c1, c2);

        float[][] expected = {{0.0f, 0.0f}, {1.0f, 1.0f}, {2.0f, 2.0f}};
        float[][] results = table.as().floatMatrix();
        assertTrue(Arrays.deepEquals(expected, results));
    }

    @Test
    public void asFloatMatrixColArgs() {
        double[] array1 = {0, 1, 1};
        double[] array2 = {0, 1, 2};
        double[] array3 = {0, 1, 3};

        DoubleColumn c1 = DoubleColumn.create("1", array1);
        DoubleColumn c2 = DoubleColumn.create("2", array2);
        DoubleColumn c3 = DoubleColumn.create("3", array3);
        Table table = Table.create("test", c1, c2, c3);

        float[][] expected = {{0.0f, 0.0f}, {1.0f, 1.0f}, {1.0f, 3.0f}};
        float[][] results = table.as().floatMatrix("1", "3");
        assertTrue(Arrays.deepEquals(expected, results));
    }
}
