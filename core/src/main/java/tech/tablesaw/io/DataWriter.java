package tech.tablesaw.io;

import tech.tablesaw.api.Table;

public interface DataWriter<O> {

    void write(Table table, Destination dest);

    void write(Table table, O options);

}
