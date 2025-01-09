package tech.tablesaw.io.jsonl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.wnameless.json.flattener.JsonFlattener;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.RuntimeIOException;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.TableBuildingUtils;

public class JsonlReader implements DataReader<JsonlReadOptions> {

  private static final JsonlReader INSTANCE = new JsonlReader();
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    register(Table.defaultReaderRegistry);
  }

  public static void register(ReaderRegistry registry) {
    // no standard exists yet. taken from https://murex.rocks/types/jsonl.html#more-information
    registry.registerExtension("jsonl", INSTANCE);
    registry.registerMimeType("application/json-lines", INSTANCE);
    registry.registerMimeType("application/jsonl", INSTANCE);
    registry.registerMimeType("application/jsonlines", INSTANCE);
    registry.registerMimeType("application/ldjson", INSTANCE);
    registry.registerMimeType("application/ndjson", INSTANCE);
    registry.registerMimeType("application/x-json-lines", INSTANCE);
    registry.registerMimeType("application/x-jsonl", INSTANCE);
    registry.registerMimeType("application/x-jsonlines", INSTANCE);
    registry.registerMimeType("application/x-ldjson", INSTANCE);
    registry.registerMimeType("application/x-ndjson", INSTANCE);
    registry.registerMimeType("text/json-lines", INSTANCE);
    registry.registerMimeType("text/jsonl", INSTANCE);
    registry.registerMimeType("text/jsonlines", INSTANCE);
    registry.registerMimeType("text/ldjson", INSTANCE);
    registry.registerMimeType("text/ndjson", INSTANCE);
    registry.registerMimeType("text/x-json-lines", INSTANCE);
    registry.registerMimeType("text/x-jsonl", INSTANCE);
    registry.registerMimeType("text/x-jsonlines", INSTANCE);
    registry.registerMimeType("text/x-ldjson", INSTANCE);
    registry.registerMimeType("text/x-ndjson", INSTANCE);
    registry.registerOptions(JsonlReadOptions.class, INSTANCE);
  }

  @Override
  public Table read(JsonlReadOptions options) {
    ObjectReader stream = mapper.readerFor(JsonNode.class);
    try {
      Reader reader = options.source().createReader(null);
      JsonParser parser = stream.createParser(reader);
      Iterator<JsonNode> iter = stream.readValues(parser);
      return convertObjects(iter, options);
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  private Table convertObjects(Iterator<JsonNode> iter, ReadOptions options) {
    // flatten each object inside the array
    StringBuilder result = new StringBuilder("[");
    boolean first = true;
    for (; iter.hasNext(); ) {
      JsonNode rowObj = iter.next();
      String flattenedRow = null;
      try {
        flattenedRow = JsonFlattener.flatten(mapper.writeValueAsString(rowObj));
      } catch (JsonProcessingException e) {
        throw new RuntimeIOException(e);
      }
      if (!first) {
        result.append(",");
      }
      first = false;
      result.append(flattenedRow);
    }
    String flattenedJsonString = result.append("]").toString();
    JsonNode flattenedJsonObj = null;
    try {
      flattenedJsonObj = mapper.readTree(flattenedJsonString);
    } catch (JsonProcessingException e) {
      throw new RuntimeIOException(e);
    }

    Set<String> colNames = new LinkedHashSet<>();
    for (JsonNode row : flattenedJsonObj) {
      Iterator<String> fieldNames = row.fieldNames();
      while (fieldNames.hasNext()) {
        colNames.add(fieldNames.next());
      }
    }

    List<String> columnNames = new ArrayList<>(colNames);
    List<String[]> dataRows = new ArrayList<>();
    for (JsonNode node : flattenedJsonObj) {
      String[] arr = new String[columnNames.size()];
      for (int i = 0; i < columnNames.size(); i++) {
        if (node.has(columnNames.get(i))) {
          arr[i] = node.get(columnNames.get(i)).asText();
        } else {
          arr[i] = null;
        }
      }
      dataRows.add(arr);
    }

    return TableBuildingUtils.build(columnNames, dataRows, options);
  }

  @Override
  public Table read(Source source) {
    return read(JsonlReadOptions.builder(source).build());
  }
}
