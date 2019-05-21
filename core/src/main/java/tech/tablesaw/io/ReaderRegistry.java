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
	String clazz = options.getClass().getCanonicalName();
	DataReader<T> reader = (DataReader<T>) optionTypesRegistry.get(clazz);
	if (reader == null) {
	    throw new IllegalArgumentException("No reader registered for class " + clazz);
	}
	return reader;
    }

    public DataReader<?> getReaderForExtension(String extension) {
	DataReader<?> reader = extensionsRegistry.get(extension);
	if (reader == null) {
	    throw new IllegalArgumentException("No reader registered for extension " + extension);
	}
	return reader;
    }

    public DataReader<?> getReaderForMimeType(String mimeType) {
	DataReader<?> reader = mimeTypesRegistry.get(mimeType);
	if (reader == null) {
	    throw new IllegalArgumentException("No reader registered for mime-type " + mimeType);
	}
	return reader;
    }

}
