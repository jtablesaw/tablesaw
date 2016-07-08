package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.TestDataUtil;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import com.github.lwhite1.tablesaw.io.csv.CsvWriter;
import com.github.lwhite1.tablesaw.store.StorageManager;
import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.google.common.base.Stopwatch;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * Tests manipulation of large (but not big) data sets
 */
public class LargeDataTest {

  private static String CSV_FILE = "bigdata/people1.csv";

  public static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();

    ColumnType[] columnTypes = {ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.LOCAL_DATE, ColumnType.SHORT_INT, ColumnType.SHORT_INT, ColumnType.BOOLEAN};
    Table t = CsvReader.read(columnTypes, CSV_FILE);
    System.out.println("Time to read from CSV File " + stopwatch.elapsed(TimeUnit.SECONDS));

    stopwatch = stopwatch.reset().start();
    storeInDb(t);
    System.out.println("Time to store in columnStore " + stopwatch.elapsed(TimeUnit.SECONDS));

    stopwatch.reset().start();
    System.out.println(t.categoryColumn("first name").first(5).print());
    System.out.println("Time to print first 5 from first name column " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
    System.out.println();

    stopwatch.reset().start();
    System.out.println(t.shortColumn("weight").summary().print());
    System.out.println("Time to summarize weight column " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    System.out.println();

    stopwatch.reset().start();
    System.out.println(t.shortColumn("height").summary().print());
    System.out.println("Time to summarize height column " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    System.out.println();

    stopwatch.reset().start();
    System.out.println(t.first(5).print());
    System.out.println("Time to print first(5) " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    System.out.println();

    stopwatch.reset().start();
    System.out.println(t.structure().print());
    System.out.println("Time to print structure " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    System.out.println();

    stopwatch.reset().start();
    CsvWriter.write("bigdata/shortpeople2.csv", t);
    System.out.println("Time to write csv file " + stopwatch.elapsed(TimeUnit.SECONDS));
    System.out.println();
  }

  private static void createPeople(Table t) throws Exception {

/*    try (CSVWriter writer = new CSVWriter(new FileWriter("people.csv"))) {
      String[] header = {"first name", "last name", "company",
          "city", "postal code", "state", "birthdate", "height", "weight", "female"};
      writer.writeNext(header);

      for (int r = 0; r < 300_000_000; r++) {
        if (r % 1_000_000 == 0) {
          System.out.println(r);
          writer.flush();
        }
        String[] entries = new String[header.length];
        entries[0] = person.firstName();
        entries[1] = person.lastName();
        entries[2] = person.getCompany().name();
        entries[3] = person.dateOfBirth().toLocalDate().toString();
        entries[4] = person.getAddress().getCity();
        entries[5] = person.getAddress().getPostalCode();
        entries[6] = fairy.baseProducer().randomElement(usStateArray);
        entries[7] = String.column(fairy.baseProducer().randomBetween(65, 280));
        entries[8] = String.column(fairy.baseProducer().randomBetween(64, 78));
        entries[9] = String.column(person.isFemale());
        writer.writeNext(entries);
      }
    }*/
  }

  private static Table createPeoples(int quantity) throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();

    Fairy fairy = Fairy.create();
    Table t = Table.create("People");
    CategoryColumn fName = CategoryColumn.create("first name");
    CategoryColumn lName = CategoryColumn.create("last name");
    CategoryColumn company = CategoryColumn.create("company");
    CategoryColumn city = CategoryColumn.create("city");
    CategoryColumn postalCode = CategoryColumn.create("postal code");
    CategoryColumn state = CategoryColumn.create("state");
    DateColumn birthDate = DateColumn.create("birth date");
    ShortColumn height = ShortColumn.create("height");
    ShortColumn weight = ShortColumn.create("weight");
    BooleanColumn female = BooleanColumn.create("female");

    t.addColumn(fName);
    t.addColumn(lName);
    t.addColumn(company);
    t.addColumn(city);
    t.addColumn(postalCode);
    t.addColumn(state);
    t.addColumn(birthDate);
    t.addColumn(height);
    t.addColumn(weight);
    t.addColumn(female);

    Person person;

    for (int r = 0; r < quantity; r++) {
      if (r % 1_000_000 == 0) {
        System.out.println(r);
      }
      person = fairy.person();
      fName.add(person.firstName());
      lName.add(person.lastName());
      company.add(person.getCompany().name());
      birthDate.add(PackedLocalDate.pack(LocalDate.parse(person.dateOfBirth().toLocalDate().toString())));
      city.add(person.getAddress().getCity());
      postalCode.add(person.getAddress().getPostalCode());
      state.add(TestDataUtil.randomUsState());
      weight.add((short) fairy.baseProducer().randomBetween(65, 280));
      height.add((short) fairy.baseProducer().randomBetween(64, 78));
      female.add(person.isFemale());
    }
    System.out.println("Time to generate " + stopwatch.elapsed(TimeUnit.SECONDS));
    return t;
  }

  private static void storeInDb() throws Exception {
    ColumnType[] columnTypes = {ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.LOCAL_DATE, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.SHORT_INT, ColumnType.SHORT_INT, ColumnType.BOOLEAN, ColumnType.BOOLEAN};
    Table t = CsvReader.read(columnTypes, CSV_FILE);
    StorageManager.saveTable("bigdata/people", t);
  }

  private static void storeInDb(Table t) throws Exception {
    StorageManager.saveTable("bigdata/peopleShort2", t);
  }
}
