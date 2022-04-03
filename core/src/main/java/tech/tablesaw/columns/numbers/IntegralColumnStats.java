package tech.tablesaw.columns.numbers;

public class IntegralColumnStats {

  private int n;
  private int range;
  private int runs;
  private int unique;

  public IntegralColumnStats(Builder builder) {
    this.n = builder.n;
    this.range = builder.range;
    this.runs = builder.runs;
    this.unique = builder.unique;
  }

  public int getN() {
    return n;
  }

  public int getRange() {
    return range;
  }

  public int getRuns() {
    return runs;
  }

  public int getUnique() {
    return unique;
  }

  public static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private int n;
    private int range;
    private int runs;
    private int unique;
    private int missing;
    private int min;
    private int max;

    public Builder n(int count) {
      this.n = count;
      return this;
    }

    public Builder min(int min) {
      this.min = min;
      return this;
    }

    public Builder max(int max) {
      this.max = max;
      return this;
    }

    public Builder missing(int missingCount) {
      this.missing = missingCount;
      return this;
    }

    public Builder unique(int countUnique) {
      this.unique = countUnique;
      return this;
    }

    public Builder runs(int runCount) {
      this.runs = runCount;
      return this;
    }

    public Builder range(int range) {
      this.range = range;
      return this;
    }

    public IntegralColumnStats build() {
      return new IntegralColumnStats(this);
    }
  }
}
