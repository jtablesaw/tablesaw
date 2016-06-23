package com.github.lwhite1.tablesaw.filters;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;
import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.tablesaw.api.QueryHelper.both;
import static com.github.lwhite1.tablesaw.api.QueryHelper.column;
import static java.lang.System.out;

/**
 * Tests manipulation of large (but not big) data sets
 */
public class TimeDependentFilteringTest {

  // pools to get random test data from
  private static List<String> concepts = new ArrayList<>(100_000);
  private static IntArrayList patientIds = new IntArrayList(1_000_000);
  private static int size = 60 * 365;
  private static IntArrayList dates = new IntArrayList(size);


  public static void main(String[] args) throws Exception {

    int numberOfRecordsInTable = 1_000;
    Stopwatch stopwatch = Stopwatch.createStarted();

    Table t = defineSchema();
    generateTestData(t, numberOfRecordsInTable, stopwatch);

    t.setName("Observations");

    String conceptA = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
    String conceptB = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
    String conceptZ = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
    String conceptD = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
    String conceptE = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
    String conceptF = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));

    stopwatch.reset().start();

    ColumnReference concept = column("concept");

    //Non-temporal clause
    Table nt = t.selectWhere(
        both(concept.isEqualTo(conceptA),
            (concept.isNotEqualTo(conceptB))));

    //Independent temporal clause
    Table tIndependentPatient1 = t.select("patient", "date").where(concept.isEqualTo(conceptZ));
    Table tIndependentPatient2 = t.select("patient").where(concept.isNotEqualTo(conceptD));

    // combine the results from above
    Table independent = tIndependentPatient1.selectWhere(
        column("patient").isIn(tIndependentPatient2.intColumn("patient")));

    //Dependent temporal query
    // Here we select against the results of the independent clause
    // We have one observation per
    for (int i = 0; i < tIndependentPatient1.rowCount(); i++)

    System.out.println("Done");
  }

  private static Table defineSchema() {
    Table t;
    t = new Table("Observations");
    CategoryColumn conceptId = CategoryColumn.create("concept");
    LocalDateColumn date = LocalDateColumn.create("date");
    FloatColumn value = FloatColumn.create("value");
    IntColumn patientId = IntColumn.create("patient");

    t.addColumn(conceptId);
    t.addColumn(date);
    t.addColumn(value);
    t.addColumn(patientId);
    return t;
  }

  private static void generateTestData(Table t, int numberOfRecordsInTable, Stopwatch stopwatch) throws IOException {
    stopwatch.reset().start();
    out.println("Generating test data");
    generateData(numberOfRecordsInTable, t);
    out.println("Time to generate "
        + numberOfRecordsInTable + " records: "
        + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
  }

  private static void generateData(int observationCount, Table table) throws IOException {
    // create pools of random values

    while (concepts.size() <= 100_000) {
      concepts.add(RandomStringUtils.randomAscii(30));
    }

    while (patientIds.size() <= 1_000_000) {
      patientIds.add(RandomUtils.nextInt(0, 2_000_000_000));
    }

    while (dates.size() <= size){
      dates.add(PackedLocalDate.pack(randomDate()));
    }

    LocalDateColumn dateColumn = table.localDateColumn("date");
    CategoryColumn conceptColumn = table.categoryColumn("concept");
    FloatColumn valueColumn = table.floatColumn("value");
    IntColumn patientColumn = table.intColumn("patient");

    // sample from the pools to write the data
    for (int i = 0; i < observationCount; i++) {
      dateColumn.add(dates.getInt(RandomUtils.nextInt(0, dates.size())));
      conceptColumn.add(concepts.get(RandomUtils.nextInt(0, concepts.size())));
      valueColumn.add(RandomUtils.nextFloat(0f, 100_000f));
      patientColumn.add(patientIds.getInt(RandomUtils.nextInt(0, patientIds.size())));

    }
  }

  private static LocalDate randomDate() {
    Random random = new Random();
    int minDay = (int) LocalDate.of(1920, 1, 1).toEpochDay();
    int maxDay = (int) LocalDate.of(2016, 1, 1).toEpochDay();
    long randomDay = minDay + random.nextInt(maxDay - minDay);
    return LocalDate.ofEpochDay(randomDay);
  }
}
