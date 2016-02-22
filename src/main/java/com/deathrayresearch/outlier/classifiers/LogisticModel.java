package com.deathrayresearch.outlier.classifiers;

import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.io.CsvWriter;
import com.deathrayresearch.outlier.util.DictionaryMap;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.classifiers.bayesian.NaiveBayes;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.deathrayresearch.outlier.columns.ColumnType.*;
import static com.deathrayresearch.outlier.columns.ColumnType.FLOAT;

/**
 *
 */
public class LogisticModel {

  public static void main(String[] args) throws Exception {

    ColumnType[] heading = {
        LOCAL_DATE_TIME,   // date and time
        CAT,   // category
        TEXT,  // description
        CAT,   // day of week
        CAT,   // PD district
        CAT,   // resolution
        TEXT,  // address
        FLOAT, // lon
        FLOAT, // lat
    };

    Table table = CsvReader.read("data/train.csv", heading);
    table.removeColumn(8);
    table.removeColumn(7);
    table.removeColumn(6);
    table.removeColumn(2);
    table.removeColumn(0);
    CsvWriter.write("testfolder/train_filter.csv", table);
    System.out.println(table.columnNames());
    NaiveBayes model = new NaiveBayes();

    System.out.println(table.categoryColumn("Category").summary().sortDescendingOn("Count").print());

    CategoricalData target = table.categoryColumn("Category").asCategoricalData();
    CategoricalData dow = table.categoryColumn("DayOfWeek").asCategoricalData();
    CategoricalData district = table.categoryColumn("PdDistrict").asCategoricalData();
    CategoricalData cats[] = new CategoricalData[2];
    cats[0] = dow;
    cats[1] = district;

    DictionaryMap dictionaryMap = table.categoryColumn("Category").dictionaryMap();
    ClassificationDataSet dataSet = new ClassificationDataSet(0, cats, target);

    for (int r = 0; r < table.rowCount(); r++) {
      DataPoint dataPoint = dataPoint(table, cats, r);
      int code = table.categoryColumn("Category").getInt(r);
      dataSet.addDataPoint(dataPoint, code);
    }
    model.trainC(dataSet);

    ColumnType testHeading[] = {
        INTEGER,         // Id,
        LOCAL_DATE_TIME, // Dates,
        CAT,             // DayOfWeek,
        CAT,             // PdDistrict,
        TEXT,            // Address,
        FLOAT,           // X,
        FLOAT,           // Y
    };

    Table testTable = CsvReader.read("data/test.csv", testHeading);
    testTable.removeColumn(6);
    testTable.removeColumn(5);
    testTable.removeColumn(4);
    testTable.removeColumn(1);
    testTable.removeColumn(0);
    System.out.println(testTable.rowCount());

    write(testTable, dictionaryMap, cats, model);
  }

  static DataPoint dataPoint(Table t, CategoricalData[] cats, int row) {
    int[] categoricalValues = new int[cats.length];
    categoricalValues[0] = t.categoryColumn("DayOfWeek").getInt(row);
    categoricalValues[1] = t.categoryColumn("PdDistrict").getInt(row);
    Vec numericalValues = new DenseVector(0);
    return new DataPoint(numericalValues, categoricalValues, cats);
  }

  static void write(Table t, DictionaryMap dictionaryMap, CategoricalData[] cats, Classifier model) throws IOException {

    CSVWriter writer = new CSVWriter(new FileWriter("testfolder/OUTPUT.csv"));
    Set<String> factorNames = new TreeSet<>();
    factorNames.addAll(dictionaryMap.categories());

    String[] header = new String[factorNames.size() + 1];
    header[0] = "id";
    List<String> factorNameList = new ArrayList<>(factorNames);

    for (int j = 1; j < factorNames.size() + 1; j++) {
      header[j] = factorNameList.get(j - 1);
    }
    writer.writeNext(header);

    for (int i = 0; i < t.rowCount(); i++) {
      String[] line = new String[factorNames.size() + 1];
      line[0] = String.valueOf(i);
      CategoricalResults results = model.classify(dataPoint(t, cats, i));
      int mostLikely = results.mostLikely();
      for (int j = 1; j < factorNames.size() + 1; j++) {
        String cat = dictionaryMap.get((short) (j - 1)).toUpperCase();
        int k = factorNameList.indexOf(cat) + 1;
        if (j == mostLikely) {
          line[k] = String.valueOf(1);
        } else {
          line[k] = String.valueOf(0);
        }
      }
      writer.writeNext(line);
    }
    writer.flush();
  }
}
