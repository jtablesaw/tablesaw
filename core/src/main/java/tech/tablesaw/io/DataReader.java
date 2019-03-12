package tech.tablesaw.io;

import java.io.IOException;

import tech.tablesaw.api.Table;

public interface DataReader<O extends ReadOptions> {

    Table read(Source source) throws IOException;

    Table read(O options) throws IOException;

}
