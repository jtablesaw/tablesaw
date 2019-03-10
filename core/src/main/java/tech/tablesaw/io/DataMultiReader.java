package tech.tablesaw.io;

import java.io.IOException;
import java.util.List;

import tech.tablesaw.api.Table;

public interface DataMultiReader<O> {

    List<Table> readMultiple(Source source) throws IOException;

    List<Table> readMultiple(O options) throws IOException;

}
