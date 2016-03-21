package com.deathrayresearch.outlier.examples;

import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.BooleanColumn;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.PackedLocalDate;
import com.deathrayresearch.outlier.columns.ShortColumn;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.io.CsvWriter;
import com.deathrayresearch.outlier.store.StorageManager;
import com.google.common.base.Stopwatch;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.columns.ColumnType.*;

/**
 *
 */
public class LargeDataTest {

  public static void main(String[] args) throws Exception {

    /*
    Table t = createPeoples(400_000_000);
    System.out.println("Done");
    Stopwatch stopwatch = Stopwatch.createStarted();
    storeInDb(t);
    System.out.println("Time to store in columnStore " + stopwatch.elapsed(TimeUnit.SECONDS));
*/

    System.out.println("Loading data. Please wait...");
    Stopwatch stopwatch = Stopwatch.createStarted();
    Table t = StorageManager.readTable("bigdata/peopleShort2/4683684b-b030-4d75-bd1a-4b4ce7815553");
    System.out.println("Time to load from columnStore " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    System.out.println();

    stopwatch.reset().start();
    System.out.println(t.categoryColumn("first name").head(5).print());
    System.out.println("Time to print head for first name column " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
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
    System.out.println(t.head(5).print());
    System.out.println("Time to print head(5) " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
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
        entries[7] = String.valueOf(fairy.baseProducer().randomBetween(65, 280));
        entries[8] = String.valueOf(fairy.baseProducer().randomBetween(64, 78));
        entries[9] = String.valueOf(person.isFemale());
        writer.writeNext(entries);
      }
    }*/
  }

  private static Table createPeoples(int quantity) throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();

    Fairy fairy = Fairy.create();
    Table t = new Table("People");
    CategoryColumn fName = CategoryColumn.create("first name");
    CategoryColumn lName = CategoryColumn.create("last name");
    CategoryColumn company = CategoryColumn.create("company");
    CategoryColumn city = CategoryColumn.create("city");
    CategoryColumn postalCode = CategoryColumn.create("postal code");
    CategoryColumn state = CategoryColumn.create("state");
    LocalDateColumn birthDate = LocalDateColumn.create("birth date");
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
      state.add(fairy.baseProducer().randomElement(usStateArray));
      weight.add((short) fairy.baseProducer().randomBetween(65, 280));
      height.add((short) fairy.baseProducer().randomBetween(64, 78));
      female.add(person.isFemale());
    }
    System.out.println("Time to generate " + stopwatch.elapsed(TimeUnit.SECONDS));
    return t;
  }

  private static void storeInDb() throws Exception {
    ColumnType[] columnTypes = {CAT, CAT, CAT, LOCAL_DATE, CAT, CAT, CAT, SHORT_INT, SHORT_INT, BOOLEAN, BOOLEAN};
    Table t = CsvReader.read(columnTypes, "bigdata/people1.csv");
    StorageManager.saveTable("bigdata/people", t);
  }

  private static void storeInDb(Table t) throws Exception {
    StorageManager.saveTable("bigdata/peopleShort2", t);
  }

  private static String[] usStateArray = {"Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut",
      "Delaware","District Of Columbia","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas",
      "Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri",
      "Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina",
      "North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota",
      "Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};

  private static List<String> usStates = Arrays.asList(usStateArray);
}
