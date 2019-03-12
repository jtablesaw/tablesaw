package tech.tablesaw.io;

import java.io.IOException;

import tech.tablesaw.api.Table;

public interface DataWriter<O extends WriteOptions> {

    void write(Table table, Destination dest) throws IOException;

    void write(Table table, O options) throws IOException;

}
