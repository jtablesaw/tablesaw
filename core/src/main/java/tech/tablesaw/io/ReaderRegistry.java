package tech.tablesaw.io;

import java.util.HashMap;
import java.util.Map;

public class ReaderRegistry {

    private final Map<String, DataReader<?>> optionTypesRegistry = new HashMap<>();
    private final Map<String, DataMultiReader<?>> optionTypesMultiRegistry = new HashMap<>();

    private final Map<String, DataReader<?>> extensionsRegistry = new HashMap<>();
    private final Map<String, DataMultiReader<?>> extensionsMultiRegistry = new HashMap<>();

    private final Map<String, DataReader<?>> mimeTypesRegistry = new HashMap<>();
    private final Map<String, DataMultiReader<?>> mimeTypesMultiRegistry = new HashMap<>();


    public void registerOptions(Class<? extends ReadOptions> optionsType, DataReader<?> reader) {
	optionTypesRegistry.put(optionsType.getCanonicalName(), reader);
    }
    public void registerOptions(Class<? extends ReadOptions> optionsType, DataMultiReader<?> reader) {
	optionTypesMultiRegistry.put(optionsType.getCanonicalName(), reader);
    }

    public void registerExtension(String extension, DataReader<?> reader) {
	extensionsRegistry.put(extension, reader);
    }
    public void registerExtension(String extension, DataMultiReader<?> reader) {
	extensionsMultiRegistry.put(extension, reader);
    }

    public void registerMimeType(String mimeType, DataReader<?> reader) {
	mimeTypesRegistry.put(mimeType, reader);
    }
    public void registerMimeType(String mimeType, DataMultiReader<?> reader) {
	mimeTypesMultiRegistry.put(mimeType, reader);
    }

    public DataReader<?> getReaderForOptions(ReadOptions options) {
	return optionTypesRegistry.get(options.getClass().getCanonicalName());
    }
    public DataMultiReader<?> getMultiReaderForOptionsType(ReadOptions options) {
	return optionTypesMultiRegistry.get(options.getClass().getCanonicalName());
    }

    public DataReader<?> getReaderForExtension(String extension) {
	return extensionsRegistry.get(extension);
    }
    public DataMultiReader<?> getMultiReaderForExtension(String extension) {
	return extensionsMultiRegistry.get(extension);
    }

    public DataReader<?> getReaderForMimeType(String mimeType) {
	return mimeTypesRegistry.get(mimeType);
    }
    public DataMultiReader<?> getMultiReaderForMimeType(String mimeType) {
	return mimeTypesMultiRegistry.get(mimeType);
    }

}
