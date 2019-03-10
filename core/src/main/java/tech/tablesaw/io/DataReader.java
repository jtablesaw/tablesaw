package tech.tablesaw.io;

import java.io.IOException;

import tech.tablesaw.api.Table;

public interface DataReader<O> {

    Table read(Source source) throws IOException;

    Table read(Source source, O options) throws IOException;

}
