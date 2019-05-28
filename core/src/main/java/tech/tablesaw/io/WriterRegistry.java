package tech.tablesaw.io;

import java.util.HashMap;
import java.util.Map;

public class WriterRegistry {

    private final Map<String, DataWriter<?>> optionTypesRegistry = new HashMap<>();

    private final Map<String, DataWriter<?>> extensionsRegistry = new HashMap<>();

    public void registerOptions(Class<? extends WriteOptions> optionsType, DataWriter<?> writer) {
        optionTypesRegistry.put(optionsType.getCanonicalName(), writer);
    }

    public void registerExtension(String extension, DataWriter<?> writer) {
        extensionsRegistry.put(extension, writer);
    }

    @SuppressWarnings("unchecked")
    public <T extends WriteOptions> DataWriter<T> getWriterForOptions(T options) {
        return (DataWriter<T>) optionTypesRegistry.get(options.getClass().getCanonicalName());
    }

    public DataWriter<?> getWriterForExtension(String extension) {
        return extensionsRegistry.get(extension);
    }

}
