package tech.tablesaw.io.json;

import java.io.Writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

public class JsonWriteOptions extends WriteOptions {

  private final boolean asObjects;
  private final boolean header;
  private final ObjectMapper mapper;

  private JsonWriteOptions(Builder builder) {
    super(builder);
    this.asObjects = builder.asObjects;
    this.header = builder.header;
    this.mapper = builder.mapper;
  }

  public boolean asObjects() {
    return asObjects;
  }

  public boolean header() {
    return header;
  }

  public ObjectMapper mapper() { return mapper; }

  public static Builder builder(Writer writer) {
    return new Builder(new Destination(writer));
  }

  public static Builder builder(Destination destination) {
    return new Builder(destination);
  }

  public static class Builder extends WriteOptions.Builder {

    private boolean asObjects = true;
    private boolean header = false;
    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    protected Builder(Destination destination) {
      super(destination);
    }

    /** If true writes each row as an object. If false writes each row as an array. */
    public JsonWriteOptions.Builder asObjects(boolean asObjects) {
      this.asObjects = asObjects;
      return this;
    }

    /** Whether to write a header row. Only used if asObjects is false. */
    public JsonWriteOptions.Builder header(boolean header) {
      this.header = header;
      return this;
    }

    public JsonWriteOptions.Builder mapper(ObjectMapper mapper) {
      this.mapper = mapper;
      return this;
    }

    public JsonWriteOptions build() {
      return new JsonWriteOptions(this);
    }
  }
}
