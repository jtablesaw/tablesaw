package com.deathrayresearch.outlier.examples;

import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.BooleanColumn;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.PackedLocalDate;
import com.deathrayresearch.outlier.io.CsvReader;
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

    Stopwatch stopwatch = Stopwatch.createStarted();
    createPeopleAndStoreAsColumns();
    System.out.println("Time to generate & write columnStore " + stopwatch.elapsed(TimeUnit.SECONDS));
  }

  private static void createPeople() throws Exception {
    Fairy fairy = Fairy.create();
    Table t = new Table("People");
    CategoryColumn fName = CategoryColumn.create("first name");
    CategoryColumn lName = CategoryColumn.create("last name");
    CategoryColumn company = CategoryColumn.create("company");
    CategoryColumn city = CategoryColumn.create("city");
    CategoryColumn postalCode = CategoryColumn.create("postal code");
    CategoryColumn state = CategoryColumn.create("state");
    LocalDateColumn dateColumn = LocalDateColumn.create("birth date");
    IntColumn height = IntColumn.create("height");
    IntColumn weight = IntColumn.create("weight");
    IntColumn female = IntColumn.create("female");
    IntColumn control = IntColumn.create("control");

    t.addColumn(fName);
    t.addColumn(lName);
    t.addColumn(company);
    t.addColumn(city);
    t.addColumn(postalCode);
    t.addColumn(state);
    t.addColumn(dateColumn);
    t.addColumn(height);
    t.addColumn(weight);
    t.addColumn(female);
    t.addColumn(control);

    CSVWriter writer = new CSVWriter(new FileWriter("people.csv"));
    String[] header = {"first name", "last name", "company",
        "city", "postal code", "state",
        "birthdate", "height", "weight", "female", "control"};
    writer.writeNext(header);
    Person person;

    for (int r = 0; r < 300_000_000; r++) {
      if (r % 1_000_000 == 0) {
        System.out.println(r);
        writer.flush();
      }
      String[] entries = new String[header.length];
      person = fairy.person();
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
      entries[10] = String.valueOf(fairy.baseProducer().trueOrFalse());
      //entries[6] = person.getAddress().street();
      //entries[6] = person.telephoneNumber();
      //entries[2] = person.email();

      writer.writeNext(entries);
    }
    writer.close();
  }

  private static void createPeopleAndStoreAsColumns() throws Exception {
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
    IntColumn height = IntColumn.create("height");
    IntColumn weight = IntColumn.create("weight");
    BooleanColumn female = BooleanColumn.create("female");
    BooleanColumn control = BooleanColumn.create("control");

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
    t.addColumn(control);

    Person person;

    for (int r = 0; r < 300_000_000; r++) {
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
      fairy.baseProducer().randomElement(usStateArray);
      weight.add(fairy.baseProducer().randomBetween(65, 280));
      height.add(fairy.baseProducer().randomBetween(64, 78));
      female.add(person.isFemale());
      control.add(fairy.baseProducer().trueOrFalse());
    }
    System.out.println("Time to generate " + stopwatch.elapsed(TimeUnit.SECONDS));
    stopwatch.reset();

    StorageManager.saveTable("bigdata/people", t);
    System.out.println("Time to write columnStore " + stopwatch.elapsed(TimeUnit.SECONDS));

  }

  private static void storeInDb() throws Exception {
    ColumnType[] columnTypes = {CAT, CAT, CAT, LOCAL_DATE, CAT, CAT, CAT, INTEGER, INTEGER, BOOLEAN, BOOLEAN};
    Table t = CsvReader.read(columnTypes, "bigdata/people.csv");
    StorageManager.saveTable("bigdata/people", t);
  }

  private static String[] usStateArray = {"Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware",
      "District Of Columbia","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky",
      "Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana",
      "Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota",
      "Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas",
      "Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};

  private static List<String> usStates = Arrays.asList(usStateArray);
}
