package com.github.lwhite1.tablesaw.store;

public final class CSVParserConfig {
  private final String csvFile;
  private final boolean hasHeader;
  private final char fieldDelimiter;
  private final char fieldSeparator;
  private final int columnBatch;

  private CSVParserConfig(String csvFile, boolean hasHeader, char fieldDelimiter, char fieldSeparator, int columnBatch) {
    this.csvFile = csvFile;
    this.hasHeader = hasHeader;
    this.fieldDelimiter = fieldDelimiter;
    this.fieldSeparator = fieldSeparator;
    this.columnBatch = columnBatch;
  }

  public String csvFile() { return csvFile; }

  public boolean hasHeader() { return hasHeader; }

  public char columnSeparator() { return fieldSeparator; }

  public int columnBatch() { return columnBatch; }

  public char columnDelimiter() { return fieldDelimiter; }

  public static final class Builder {
    private final String csvFile;
    private boolean hasHeader = true;
    private char fieldDelimiter = '"';
    private int columnBatchSize = 20;
    private char fieldSeparator = ',';

    private Builder(String csvFile) {
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

    public Builder setFieldSeparator(char fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
    }

    public CSVParserConfig build() {
      return new CSVParserConfig(csvFile, hasHeader, fieldDelimiter, fieldSeparator, columnBatchSize);
    }
  }

  public static Builder newBuilder(String csvFilePath) {
    return new Builder(csvFilePath);
  }

  public static CSVParserConfig defaultParser(String csvFilePath) {
    return new Builder(csvFilePath).build();
  }
}
