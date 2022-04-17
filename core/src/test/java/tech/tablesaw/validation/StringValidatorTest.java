package tech.tablesaw.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.base.Strings;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

class StringValidatorTest {

  private final Predicate<String> predicate = StringUtils::isAlpha;
  private final Predicate<String> predicate2 = StringUtils::isNumeric;

  private StringValidator v1;
  private StringValidator v2;
  String[] values = {"Funny", "sad", "42", "42", "funny", "Funny"};
  StringColumn sc = StringColumn.create("test", values);

  @BeforeEach
  void setUp() {
    v1 = new StringValidator("Value is alphabetic", predicate);
    v2 = new StringValidator("Value is numeric", predicate2);
  }

  @Test
  void name() {
    assertEquals("Value is alphabetic", v1.name());
  }

  @Test
  void validate() {
    Selection matches = v1.validate(sc);
    assertEquals(0, matches.get(0));
    assertEquals(1, matches.get(1));
    assertEquals(2, matches.size());
  }

  @Test
  void summaryValidation() {
    sc.addValidator(v1).addValidator(v2);
    sc.addValidator(
        new StringValidator("Value is not missing", string -> !Strings.isNullOrEmpty(string)));
    Table results = Utils.summaryResultsTable();
    sc.summaryValidation(results);
    System.out.println(results);
  }

  @Test
  void detailValidation() {
    Table t = Table.create("t", sc);
    sc.addValidator(v1).addValidator(v2);
    Table results = Utils.detailedResultsTable();
    sc.detailedValidation(results);
    System.out.println(results);

    Table merged = Utils.mergeResults(t, results);
    System.out.println(merged);

    StringColumn sc2 = merged.stringColumn("test");
    sc2.set(sc2.isEqualTo("funny"), "Funny");
    sc2.set(sc2.isEqualTo("sad"), "Funny");
    System.out.println(merged);
  }

  @Test
  void intermediateValidation() {
    sc.addValidator(v1).addValidator(v2);
    Table results = Utils.intermediateResultsTable();
    sc.intermediateValidation(results);
    System.out.println(results);
  }
}
