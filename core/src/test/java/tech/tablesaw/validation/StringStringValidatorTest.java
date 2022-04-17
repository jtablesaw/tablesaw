package tech.tablesaw.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BiPredicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.strings.StringPredicates;
import tech.tablesaw.selection.Selection;

class StringStringValidatorTest {

  private StringStringValidator v1;
  private StringStringValidator v2;
  String[] values = {"Funny", "sad", "42", "sadly"};
  StringColumn sc = StringColumn.create("test", values);

  @BeforeEach
  void setUp() {
    BiPredicate<String, String> predicate = StringPredicates.startsWith;
    BiPredicate<String, String> predicate2 = String::matches;

    v1 = new StringStringValidator("Values start with", predicate, "Fun");
    v2 = new StringStringValidator("Values matches regex ", predicate2, ".+ad$");
  }

  @Test
  void validate() {
    Selection matches = v1.validate(sc);
    assertEquals(0, matches.get(0));
    assertEquals(1, matches.size());
  }

  @Test
  void columnInstall() {
    sc.addValidator(v1).addValidator(v2);

    sc.addValidator(StringStringValidator.endsWith.value("y"));

    Table results = Utils.summaryResultsTable();
    sc.summaryValidation(results);
    System.out.println(results);
  }
}
