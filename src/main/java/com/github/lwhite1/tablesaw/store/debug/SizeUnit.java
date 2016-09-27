package com.github.lwhite1.tablesaw.store.debug;

public enum SizeUnit {
  Bytes("bytes", 1),
  KB("kb", 1024),
  MB("mb", 1024 * 1024),
  GB("gb", 1024 * 1024 * 1024);

  public final String symbol;
  public final long factor;

  SizeUnit(String symbol, long factor) {
    this.symbol = symbol;
    this.factor = factor;
  }

  public long toBytes(long n) { return n * factor; }

}
