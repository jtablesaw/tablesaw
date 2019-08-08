package tech.tablesaw.analytic;

enum AnalyticNumberingFunctions {

  ROW_NUMBER(NumberingFunctionsImplementations.rowNumber),
  RANK(NumberingFunctionsImplementations.rank),
  DENSE_RANK(NumberingFunctionsImplementations.denseRank);

  private final NumberingFunction implementation;

  AnalyticNumberingFunctions(NumberingFunction implementation) {
    this.implementation = implementation;
  }

  public NumberingFunction getImplementation() {
    return implementation;
  }
}
