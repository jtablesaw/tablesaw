package tech.tablesaw.io.jsonl;

import java.io.Writer;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

public class JsonlWriteOptions extends WriteOptions {

  private JsonlWriteOptions(Builder builder) {
    super(builder);
  }

  public static Builder builder(Writer writer) {
    return new Builder(new Destination(writer));
  }

  public static Builder builder(Destination destination) {
    return new Builder(destination);
  }

  public static class Builder extends WriteOptions.Builder {

    protected Builder(Destination destination) {
      super(destination);
    }

    public JsonlWriteOptions build() {
      return new JsonlWriteOptions(this);
    }
  }
}
