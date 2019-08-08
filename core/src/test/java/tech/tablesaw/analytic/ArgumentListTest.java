package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import tech.tablesaw.analytic.AnalyticAggregateFunctions;
import tech.tablesaw.analytic.AnalyticNumberingFunctions;
import tech.tablesaw.analytic.ArgumentList;
import tech.tablesaw.analytic.ArgumentList.FunctionCall;

class ArgumentListTest {

  @Test
  public void singleColumnToSqlString() {
    ArgumentList argumentList = ArgumentList.builder()
      .stageFunction("col1", AnalyticAggregateFunctions.MAX)
      .unStageFunction("col1Count")
      .build();

    assertEquals("MAX(col1) OVER w AS col1Count", argumentList.toSqlString("w"));
  }

  @Test
  public void stageUnstageAggregate() {
    ArgumentList argumentList = ArgumentList.builder()
      .stageFunction("col1", AnalyticAggregateFunctions.MAX)
      .unStageFunction("col1Max")
      .stageFunction("col1", AnalyticAggregateFunctions.COUNT)
      .unStageFunction("col1Count")
      .build();

    assertEquals(0, argumentList.getNumberingFunctions().size());
    assertEquals(ImmutableList.of("col1Max", "col1Count"), argumentList.getNewColumnNames());
    assertEquals(ImmutableMap.of("col1Max",
      new FunctionCall<>("col1", "col1Max", AnalyticAggregateFunctions.MAX),
      "col1Count",
      new FunctionCall<>("col1", "col1Count", AnalyticAggregateFunctions.COUNT)
    ), argumentList.getAggregateFunctions());
  }

  @Test
  public void stageUnstageNumbering() {
    ArgumentList argumentList = ArgumentList.builder()
      .stageFunction("col1", AnalyticNumberingFunctions.RANK)
      .unStageFunction("col1Rank")
      .stageFunction("col1", AnalyticNumberingFunctions.DENSE_RANK)
      .unStageFunction("col1DenseRank")
      .build();

    assertEquals(0, argumentList.getAggregateFunctions().size());
    assertEquals(ImmutableList.of("col1Rank", "col1DenseRank"), argumentList.getNewColumnNames());
    assertEquals(ImmutableMap.of("col1Rank",
      new FunctionCall<>("col1", "col1Rank", AnalyticNumberingFunctions.RANK),
      "col1DenseRank",
      new FunctionCall<>("col1", "col1DenseRank", AnalyticNumberingFunctions.DENSE_RANK)
    ), argumentList.getNumberingFunctions());
  }

  @Test
  public void stageUnstageBothTypes() {
    ArgumentList argumentList = ArgumentList.builder()
      .stageFunction("col1", AnalyticNumberingFunctions.RANK)
      .unStageFunction("col1Rank")
      .stageFunction("col1", AnalyticAggregateFunctions.MAX)
      .unStageFunction("col1Max")
      .build();

    assertEquals(ImmutableList.of("col1Rank", "col1Max"), argumentList.getNewColumnNames());

    assertEquals(ImmutableMap.of("col1Max",
      new FunctionCall<>("col1", "col1Max", AnalyticAggregateFunctions.MAX)),
      argumentList.getAggregateFunctions());

    assertEquals(ImmutableMap.of("col1Rank",
      new FunctionCall<>("col1", "col1Rank", AnalyticNumberingFunctions.RANK)),
      argumentList.getNumberingFunctions());


    String expected = "RANK(col1) OVER w AS col1Rank,"
      + System.lineSeparator()
      + "MAX(col1) OVER w AS col1Max";

    assertEquals(expected, argumentList.toSqlString("w"));
  }

  @Test
  public void duplicateColsThrows() {
    Throwable thrown = assertThrows(IllegalArgumentException.class, () ->
      ArgumentList.builder()
      .stageFunction("col1", AnalyticAggregateFunctions.MAX)
      .unStageFunction("col1Max")
      .stageFunction("col1", AnalyticAggregateFunctions.COUNT)
      .unStageFunction("col1Max")
      .build()
      );

    assertTrue(thrown.getMessage().contains("duplicate column"));
  }

  @Test
  public void buildWithStagedThrows() {
    Throwable thrown = assertThrows(IllegalArgumentException.class, () ->
      ArgumentList.builder()
        .stageFunction("col1", AnalyticAggregateFunctions.MAX)
        .build()
    );

    assertTrue(thrown.getMessage().contains("Cannot build when a column is staged"));
  }

  @Test
  public void nothingStaged() {
    Throwable thrown = assertThrows(IllegalArgumentException.class, () ->
      ArgumentList.builder()
        .stageFunction("col1", AnalyticAggregateFunctions.MAX)
        .stageFunction("col1", AnalyticAggregateFunctions.MAX)
        .build()
    );

    assertTrue(thrown.getMessage().contains("Cannot stage a column while another is staged"));

  }

}