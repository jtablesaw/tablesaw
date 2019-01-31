package tech.tablesaw.io.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.wnameless.json.flattener.JsonFlattener;

public class JsonReader {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper csvMapper = new CsvMapper();

    public Csv jsonToCsv(String json) throws IOException {
        JsonNode jsonObj = mapper.readTree(json);
        if (!jsonObj.isArray()) {
            throw new IllegalStateException(
                    "Only reading a json array or arrays or objects is currently supported");
        }
        if (jsonObj.size() == 0) {
            return new Csv("", false);
        }
        // array of arrays
        if (jsonObj.get(0).isArray()) {
            CsvSchema schema = CsvSchema.emptySchema();
            boolean allStrings = true;
            for (JsonNode n : jsonObj.get(0)) {
        	if (!n.isTextual()) {
        	    allStrings = false;
        	}
            }
            return new Csv(csvMapper.writer(schema).writeValueAsString(jsonObj), allStrings);
        }
        // array of objects
        // flatten each object inside the array
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < jsonObj.size(); i++) {
            JsonNode rowObj = jsonObj.get(i);
            String flattenedRow = JsonFlattener.flatten(mapper.writeValueAsString(rowObj));
            if (i != 0) {
                result.append(",");
            }
            result.append(flattenedRow);
        }
        String flattenedJsonString = result.append("]").toString();
        JsonNode flattenedJsonObj = mapper.readTree(flattenedJsonString);
        Set<String> colNames = new HashSet<>();
        for (JsonNode row : flattenedJsonObj) {
            Iterator<String> fieldNames = row.fieldNames();
            while (fieldNames.hasNext()) {
        	colNames.add(fieldNames.next());
            }
        }
        CsvSchema.Builder schemaBuilder = new CsvSchema.Builder().setUseHeader(true);
        colNames.stream().forEach(c -> schemaBuilder.addColumn(c));
        return new Csv(csvMapper.writer(schemaBuilder.build()).writeValueAsString(flattenedJsonObj), true);
    }

    public static class Csv {
	private final String contents;
	private final boolean hasHeader;

	public Csv(String contents, boolean hasHeader) {
	    this.contents = contents;
	    this.hasHeader = hasHeader;
	}

	public String getContents() {
	    return contents;
	}

	public boolean hasHeader() {
	    return hasHeader;
	}
    }

}
