package tech.tablesaw.io;

import tech.tablesaw.api.Table;

public interface DataWriter<O extends WriteOptions> {

  void write(Table table, Destination dest);

  void write(Table table, O options);
}
