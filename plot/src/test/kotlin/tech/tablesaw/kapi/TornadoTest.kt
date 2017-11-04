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

import tech.tablesaw.api.ColumnType
import tech.tablesaw.api.Table
import tech.tablesaw.api.plot.Scatter

import tech.tablesaw.api.ColumnType.*
import tech.tablesaw.api.QueryHelper.column
import tech.tablesaw.io.csv.CsvReadOptions
import kotlin.system.exitProcess

/**
 *
 */
@Throws(Exception::class)
fun main(args: Array<String>) {

    var tornadoes: Dataframe = Dataframe(Table.read().csv(
            CsvReadOptions.builder("../data/1950-2014_torn.csv").columnTypes(COLUMN_TYPES_OLD)))

    val tStructure = tornadoes.structure()
    println(tStructure)
    println()

    tornadoes.removeColumns("Number", "Year", "Month", "Day", "Zone", "State FIPS", "Loss", "Crop Loss", "End " + "Lat", "End Lon", "NS", "SN", "SG", "FIPS 1", "FIPS 2", "FIPS 3", "FIPS 4")

    tornadoes.exportToCsv("../data/tornadoes_1950-2014.csv")

    tornadoes = Dataframe(Table.read().csv("../data/tornadoes_1950-2014.csv"))

    println(tornadoes.structure())
    println(tornadoes.structure().target.selectWhere(column("Column Type").isEqualTo("INTEGER")))

    tornadoes.setName("tornadoes")

    println()
    println("Col names")
    println(tornadoes.columnNames())

    println()
    println("Remove the 'State No' column")
    tornadoes.removeColumns("State No")
    println(tornadoes.columnNames())

    println()
    println("print the table's shape:")
    println(tornadoes.shape())

    println()
    println("Use first(3) to view the first 3 rows:")
    println(tornadoes.first(3))

    tornadoes = Dataframe(tornadoes.target.selectWhere(column("Start Lat").isGreaterThan(20f)))
    Scatter.show("US Tornadoes 1950-2014", tornadoes.nCol("Start Lon"), tornadoes.nCol("Start Lat"))

    println()
    println("Extact month from the date and make it a separate column")
    val month = tornadoes.dateCol("Date").month()
    println(month.summary())

    println("Add the month column to the table")
    tornadoes.target.addColumn(2, month)
    println(tornadoes.columnNames())

    println()
    println("Filtering: Tornadoes where there were fatalities")
    var fatal = tornadoes.target.selectWhere(column("Fatalities").isGreaterThan(0))
    println(fatal.shape())

    println()
    println(fatal.first(5))

    println()
    println("Total fatalities: " + fatal.shortColumn("Fatalities").sum())

    println()
    println("Sorting on Fatalities in descending order")
    fatal = fatal.sortDescendingOn("Fatalities")
    println(fatal.first(5))

    println("")
    println("Calculating basic descriptive statistics on Fatalities")
    println(fatal.shortColumn("Fatalities").summary())


    //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
    val injuriesByScale = tornadoes.median("Injuries").by("Scale")
    val fob = tornadoes.minimum("Injuries").by("Scale", "State")
    println(fob)
    injuriesByScale.setName("Median injuries by Tornado Scale")
    println(injuriesByScale)

    //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
    val injuriesByScaleState = tornadoes.median("Injuries").by("Scale", "State")
    injuriesByScaleState.setName("Median injuries by Tornado Scale and State")
    println(injuriesByScaleState)

    println()
    println("Writing the revised table to a new csv file")
    tornadoes.exportToCsv("../data/rev_tornadoes_1950-2014.csv")

    println()
    println("Saving to Tablesaw format")
    val dbName = tornadoes.save("/tmp/tablesaw/testdata")

    // NOTE: dbName is equal to "/tmp/tablesaw/testdata/tornadoes.saw"

    println()
    println("Reading from Tablesaw format")
    Dataframe(Table.readTable(dbName))
    println("Finished")
    exitProcess(1)
}

// column types for the tornado table
private val COLUMN_TYPES_OLD = arrayOf<ColumnType>(INTEGER, // number by year
        INTEGER, // year
        INTEGER, // month
        INTEGER, // day
        LOCAL_DATE, // date
        LOCAL_TIME, // time
        CATEGORY, // tz
        CATEGORY, // st
        CATEGORY, // state fips
        INTEGER, // state torn number
        INTEGER, // scale
        INTEGER, // injuries
        INTEGER, // fatalities
        FLOAT, // loss
        FLOAT, // crop loss
        FLOAT, // St. Lat
        FLOAT, // St. Lon
        FLOAT, // End Lat
        FLOAT, // End Lon
        FLOAT, // length
        FLOAT, // width
        FLOAT, // NS
        FLOAT, // SN
        FLOAT, // SG
        CATEGORY, // Count FIPS 1-4
        CATEGORY, CATEGORY, CATEGORY)
