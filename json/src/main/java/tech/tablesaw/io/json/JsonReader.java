package tech.tablesaw.io.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.*;

public class JsonReader implements DataReader<JsonReadOptions> {

  private static final JsonReader INSTANCE = new JsonReader();
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    register(Table.defaultReaderRegistry);
  }

  public static void register(ReaderRegistry registry) {
    registry.registerExtension("json", INSTANCE);
    registry.registerMimeType("application/json", INSTANCE);
    registry.registerOptions(JsonReadOptions.class, INSTANCE);
  }

  @Override
  public Table read(JsonReadOptions options) {
    JsonNode jsonObj = null;
    try {
      jsonObj = mapper.readTree(options.source().createReader(null));
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    if (options.path() != null) {
      jsonObj = jsonObj.at(options.path());
    }
    if (!jsonObj.isArray()) {
      throw new IllegalStateException(
          "Only reading a JSON array is currently supported. The array must hold an array or object for each row.");
    }
    if (jsonObj.size() == 0) {
      return Table.create(options.tableName());
    }

    JsonNode firstNode = jsonObj.get(0);
    if (firstNode.isArray()) {
      return convertArrayOfArrays(jsonObj, options);
    }
    return convertArrayOfObjects(jsonObj, options);
  }

  private Table convertArrayOfArrays(JsonNode jsonObj, ReadOptions options) {
    JsonNode firstNode = jsonObj.get(0);
    boolean firstRowAllStrings = true;
    List<String> columnNames = new ArrayList<>();
    for (JsonNode n : firstNode) {
      if (!n.isTextual()) {
        firstRowAllStrings = false;
      }
    }
    boolean hasHeader = firstRowAllStrings;
    for (int i = 0; i < firstNode.size(); i++) {
      columnNames.add(hasHeader ? firstNode.get(i).textValue() : "Column " + i);
    }
    List<String[]> dataRows = new ArrayList<>();
    for (int i = hasHeader ? 1 : 0; i < jsonObj.size(); i++) {
      JsonNode arr = jsonObj.get(i);
      String[] row = new String[arr.size()];
      for (int j = 0; j < arr.size(); j++) {
        row[j] = arr.get(j).asText();
      }
      dataRows.add(row);
    }
    return TableBuildingUtils.build(columnNames, dataRows, options);
  }

  private Table convertArrayOfObjects(JsonNode jsonObj, ReadOptions options) {
    // flatten each object inside the array
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < jsonObj.size(); i++) {
      JsonNode rowObj = jsonObj.get(i);
      String flattenedRow = null;
      try {
        flattenedRow = JsonFlattener.flatten(mapper.writeValueAsString(rowObj));
      } catch (JsonProcessingException e) {
        throw new RuntimeIOException(e);
      }
      if (i != 0) {
        result.append(",");
      }
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
    return read(JsonReadOptions.builder(source).build());
  }
}
