package tech.tablesaw.io;

import java.util.HashMap;
import java.util.Map;

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
	return (DataReader<T>) optionTypesRegistry.get(options.getClass().getCanonicalName());
    }

    public DataReader<?> getReaderForExtension(String extension) {
	return extensionsRegistry.get(extension);
    }

    public DataReader<?> getReaderForMimeType(String mimeType) {
	return mimeTypesRegistry.get(mimeType);
    }

}
