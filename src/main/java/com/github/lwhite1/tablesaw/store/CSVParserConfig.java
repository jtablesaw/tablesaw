package com.github.lwhite1.tablesaw.store;

public final class CSVParserConfig {
  private final String csvFile;
  private final boolean hasHeader;
  private final char fieldDelimiter;
  private final int columnBatch;

  private CSVParserConfig(String csvFile, Boolean hasHeader, char fieldDelimiter, int columnBatch) {
    this.csvFile = csvFile;
    this.hasHeader = hasHeader;
    this.fieldDelimiter = fieldDelimiter;
    this.columnBatch = columnBatch;
  }

  public String csvFile() { return csvFile; }

  public boolean hasHeader() { return hasHeader; }

  public char fieldDelimiter() { return fieldDelimiter; }

  public int columnBatch() { return columnBatch; }

  public static final class Builder {
    private final String csvFile;
    private boolean hasHeader = true;
    private char fieldDelimiter = ',';
    private int columnBatchSize = 20;

    public Builder(String csvFile) {
      this.csvFile = csvFile;
    }

    public Builder hasHeader(boolean b) {
      hasHeader = b;
      return this;
    }

    public Builder fieldDelimiter(char c) {
      fieldDelimiter = c;
      return this;
    }

    public Builder columnBatchSize(int n) {
      columnBatchSize = n;
      return this;
    }

    public CSVParserConfig build() {
      return new CSVParserConfig(csvFile, hasHeader, fieldDelimiter, columnBatchSize);
    }
  }
}
