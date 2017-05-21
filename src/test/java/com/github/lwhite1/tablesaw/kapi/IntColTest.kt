package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.IntColumn
import com.github.lwhite1.tablesaw.api.Table
import org.junit.Test

/**
 */
class IntColTest {

    @Test
    fun plus() {
        val column = IntColumn.create("test", 4)
        column.append(1)
        column.append(2)
        column.append(3)
        column.append(4)

        val col = IntCol(column)
        val result = col + 3
        val result2 = col + col
        val result3 = col / 2
        val result4 = col + 200

        println(result)
        println(result2)

        println(result4[2])
        println(col[2])
        println("remainder: " + (result4 % col)[2])

        //  println(result.firstElement())
        //  println(result2.firstElement())

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

    }

    @Test
    fun plus1() {

        val column = IntColumn.create("test", 4)
        column.append(1)
        column.append(2)
        column.append(3)
        column.append(4)

        val t = Table.createFromCsv("data/BushApproval.csv")
        val frame = Dataframe(t)

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

        val fromCsv = Dataframe.createFromCsv("data/BushApproval.csv")
        println(fromCsv.summary())
    }
}