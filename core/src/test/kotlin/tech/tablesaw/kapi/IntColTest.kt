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

package tech.tablesaw.kapi

import tech.tablesaw.api.IntColumn
import tech.tablesaw.api.Table
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 */
class IntColTest {

    @Test
    fun plus() {
        val column = IntColumn("test", 4)
        column.append(1)
        column.append(2)
        column.append(3)
        column.append(4)

        val col = IntCol(column)
        val result = col + 3
        val result2 = col + col
        val result3 = col / 2
        val result4 = col + 200
        val result5 = col.emptyCopy()

        assertEquals(4, result.get(0))
        assertEquals(2, result2.get(0))
        assertEquals("test + 3", result.name())
        assertEquals("test / 2", result3.name())
        assertEquals("test", result5.name())

        assertNotNull(result.id())

        assertEquals(203, result4[2])
        assertEquals(3, col[2])
        assertEquals(2, (result4 % col)[2])

        assertEquals(4, result.firstElement())
        assertEquals(2, result2.firstElement())

        /*
                  println(result.sum())
                  println(result2.sum())

                  println(result.javaClass)
                  println(result3.javaClass)

                  println(result3 * result3)

                  println(col[2])

                  println(3 in col)
                  println(10 in col)
                  println(10 !in col)

                  // math functions
                  println(col.sum())
                  println(col.product())
                  println(col.max())
                  println(col.min())
                  println(col.median())
                  println(col.mean())
                  println(col.geometricMean())
                  println(col.quadraticMean())
                  println(col.quartile1())
                  println(col.quartile3())
                  println(col.percentile(.60))
                  println(col.sumOfLogs())
                  println(col.sumOfSquares())
                  println(col.populationVariance())
                  println(col.standardDeviation())
                  println(col.skewness())
                  println(col.kurtosis())

          */
        val subset = col.isEven()
//        println(subset.print())
    }

    @Test
    fun plus1() {

        val column = IntColumn("test", 4)
        column.append(1)
        column.append(2)
        column.append(3)
        column.append(4)

        val t = Table.createFromCsv("../data/BushApproval.csv")
        val frame = Dataframe(t)

        /*
        println(frame.print())
        println(frame.printHtml())
        println(frame.name())
        println(frame)
        println(frame.shape())
        println(frame.summary())
        println(frame.get(2, 12))
        println(frame[2, 12])

        println(frame.columnNames())
        println("Row Count: ${frame.rowCount()}")
        println("Column Count ${frame.columnCount()}")

        println(frame.first(3).print())
        println(frame.last(3).print())
*/
        val fromCsv = Dataframe.createFromCsv("../data/BushApproval.csv")

/*
        println(fromCsv.summary())
        println(frame[0].print())
        println(frame["approval"].summary().print())
*/
    }
}