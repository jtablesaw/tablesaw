package tech.tablesaw.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReaderRegistry {

  private final Map<String, DataReader<?>> optionTypesRegistry = new HashMap<>();

  private final Map<String, DataReader<?>> extensionsRegistry = new HashMap<>();

  private final Map<String, DataReader<?>> mimeTypesRegistry = new HashMap<>();

  public void registerOptions(Class<? extends ReadOptions> optionsType, DataReader<?> reader) {
    optionTypesRegistry.put(optionsType.getCanonicalName(), reader);
  }

  public void registerExtension(String extension, DataReader<?> reader) {
    extensionsRegistry.put(extension, reader);
  }

  public void registerMimeType(String mimeType, DataReader<?> reader) {
    mimeTypesRegistry.put(mimeType, reader);
  }

  @SuppressWarnings("unchecked")
  public <T extends ReadOptions> DataReader<T> getReaderForOptions(T options) {
    String clazz = options.getClass().getCanonicalName();
    DataReader<T> reader = (DataReader<T>) optionTypesRegistry.get(clazz);
    if (reader == null) {
      throw new IllegalArgumentException("No reader registered for class " + clazz);
    }
    return reader;
  }

  public Optional<DataReader<?>> getReaderForExtension(String extension) {
    return Optional.ofNullable(extensionsRegistry.get(extension));
  }

  public Optional<DataReader<?>> getReaderForMimeType(String mimeType) {
    return Optional.ofNullable(mimeTypesRegistry.get(mimeType));
  }
}
