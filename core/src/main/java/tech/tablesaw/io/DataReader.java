package tech.tablesaw.io;

import tech.tablesaw.api.Table;

public interface DataReader<O extends ReadOptions> {

  Table read(Source source);

  Table read(O options);
}
