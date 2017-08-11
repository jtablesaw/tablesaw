package tech.tablesaw.examples;

import static tech.tablesaw.api.ColumnType.*;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.ml.classification.ConfusionMatrix;
import tech.tablesaw.api.ml.classification.LogisticRegression;
import tech.tablesaw.io.csv.CsvReader;

/**
 *
 */
public class SfCrimeTest {

    public static void main(String[] args) throws Exception {

        Table crime = Table.createFromCsv("/Users/larrywhite/IdeaProjects/testdata/bigdata/train.csv");

        out(crime.shape());
        out(crime.structure().print());

        crime.removeColumns("DayOfWeek");

        IntColumn precinct = crime.categoryColumn("PdDistrict").toIntColumn();
        precinct.setName("Precinct");
        crime.addColumn(precinct);

        ShortColumn year = crime.dateTimeColumn("Dates").year();
        year.setName("Year");
        crime.addColumn(year);

        CategoryColumn category = crime.categoryColumn("Category");
        Table categorySummary = category.summary().sortDescendingOn("Count");
        out(categorySummary.print());

        ShortColumn minuteOfDay = crime.dateTimeColumn("Dates").minuteOfDay();
        minuteOfDay.setName("MinuteOfDay");
        crime.addColumn(minuteOfDay);

        ShortColumn dayOfYear = crime.dateTimeColumn("Dates").dayOfYear();
        dayOfYear.setName("DayOfYear");
        crime.addColumn(dayOfYear);

        ShortColumn dayOfWeekValue = crime.dateTimeColumn("Dates").dayOfWeekValue();
        dayOfWeekValue.setName("DayOfWeek");
        crime.addColumn(dayOfWeekValue);

        Table[] subTables = crime.sampleSplit(.1);
        Table train = subTables[0];
        Table test = subTables[1];


        out(CsvReader.printColumnTypes("/Users/larrywhite/IdeaProjects/testdata/bigdata/sampleSubmission.csv", true,
                ','));

        LogisticRegression model = LogisticRegression.learn(
                train.categoryColumn("Category"),
                0.1,
                1.0E-3,
                700,
                train.nCol("X"),
                train.nCol("Y"),
                train.nCol("MinuteOfDay"),
                train.nCol("DayOfYear"),
                train.nCol("DayOfWeek"),
                train.nCol("Year"),
                train.nCol("Precinct"));

        out("Model trained");

        ConfusionMatrix matrix = model.predictMatrix(test.categoryColumn("Category"),
                test.nCol("X"),
                test.nCol("Y"),
                test.nCol("MinuteOfDay"),
                test.nCol("DayOfYear"),
                test.nCol("DayOfWeek"),
                test.nCol("Year"),
                test.nCol("Precinct"));

        out(matrix.accuracy());
        out(matrix.toTable().print());

        // Table trueCrime = Table.createFromCsv("/Users/larrywhite/IdeaProjects/testdata/bigdata/test.csv");
        // out(CsvReader.printColumnTypes("/Users/larrywhite/IdeaProjects/testdata/bigdata/sampleSubmission.csv",
        // true, ','));

        ColumnType[] columnTypes = {
                INTEGER, // 0     Id
                FLOAT,  // 1     ARSON
                FLOAT,  // 2     ASSAULT
                FLOAT,  // 3     BAD CHECKS
                FLOAT,  // 4     BRIBERY
                FLOAT,  // 5     BURGLARY
                FLOAT,  // 6     DISORDERLY CONDUCT
                FLOAT,  // 7     DRIVING UNDER THE INFLUENCE
                FLOAT,  // 8     DRUG/NARCOTIC
                FLOAT,  // 9     DRUNKENNESS
                FLOAT,  // 10    EMBEZZLEMENT
                FLOAT,  // 11    EXTORTION
                FLOAT,  // 12    FAMILY OFFENSES
                FLOAT,  // 13    FORGERY/COUNTERFEITING
                FLOAT,  // 14    FRAUD
                FLOAT,  // 15    GAMBLING
                FLOAT,  // 16    KIDNAPPING
                FLOAT,  // 17    LARCENY/THEFT
                FLOAT,  // 18    LIQUOR LAWS
                FLOAT,  // 19    LOITERING
                FLOAT,  // 20    MISSING PERSON
                FLOAT,  // 21    NON-CRIMINAL
                FLOAT,  // 22    OTHER OFFENSES
                FLOAT,  // 23    PORNOGRAPHY/OBSCENE MAT
                FLOAT,  // 24    PROSTITUTION
                FLOAT,  // 25    RECOVERED VEHICLE
                FLOAT,  // 26    ROBBERY
                FLOAT,  // 27    RUNAWAY
                FLOAT,  // 28    SECONDARY CODES
                FLOAT,  // 29    SEX OFFENSES FORCIBLE
                FLOAT,  // 30    SEX OFFENSES NON FORCIBLE
                FLOAT,  // 31    STOLEN PROPERTY
                FLOAT,  // 32    SUICIDE
                FLOAT,  // 33    SUSPICIOUS OCC
                FLOAT,  // 34    TREA
                FLOAT,  // 35    TRESPASS
                FLOAT,  // 36    VANDALISM
                FLOAT,  // 37    VEHICLE THEFT
                FLOAT,  // 38    WARRANTS
                FLOAT,  // 39    WEAPON LAWS
        };

        Table results = Table.createFromCsv(columnTypes,
                "/Users/larrywhite/IdeaProjects/testdata/bigdata/sampleSubmission.csv");

        FloatColumn larceny = results.floatColumn("LARCENY/THEFT");
        FloatColumn warrants = results.floatColumn("WARRANTS");

        Table trueCrime = testData();
        for (int row : trueCrime) {
            double[] posteriori = new double[39];
            model.predictFromModel(row, posteriori,
                    test.nCol("X"),
                    test.nCol("Y"),
                    test.nCol("MinuteOfDay"),
                    test.nCol("DayOfYear"),
                    test.nCol("DayOfWeek"),
                    test.nCol("Year"),
                    test.nCol("Precinct")
            );

            larceny.set(row, 1.0f);
            warrants.set(row, 0f);
        }
        results.exportToCsv("newSubmission.csv");
    }

    private static Table testData() throws Exception {
        // Setup actual test data
        Table trueCrime = Table.createFromCsv("/Users/larrywhite/IdeaProjects/testdata/bigdata/test.csv");

        trueCrime.removeColumns("DayOfWeek");

        IntColumn precinctT = trueCrime.categoryColumn("PdDistrict").toIntColumn();
        precinctT.setName("Precinct");
        trueCrime.addColumn(precinctT);

        ShortColumn yearT = trueCrime.dateTimeColumn("Dates").year();
        yearT.setName("Year");
        trueCrime.addColumn(yearT);

        ShortColumn minuteOfDayT = trueCrime.dateTimeColumn("Dates").minuteOfDay();
        minuteOfDayT.setName("MinuteOfDay");
        trueCrime.addColumn(minuteOfDayT);

        ShortColumn dayOfYearT = trueCrime.dateTimeColumn("Dates").dayOfYear();
        dayOfYearT.setName("DayOfYear");
        trueCrime.addColumn(dayOfYearT);

        ShortColumn dayOfWeekValueT = trueCrime.dateTimeColumn("Dates").dayOfWeekValue();
        dayOfWeekValueT.setName("DayOfWeek");
        trueCrime.addColumn(dayOfWeekValueT);
        return trueCrime;
    }

    private static void out(Object str) {
        System.out.println(String.valueOf(str));
    }
}
