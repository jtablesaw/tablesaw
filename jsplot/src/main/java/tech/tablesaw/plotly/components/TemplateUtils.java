package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;

public class TemplateUtils {


    public static PebbleEngine getNewEngine() {
        PebbleEngine engine = null;
        try {
            Loader<?> loader = new ClasspathLoader();
            engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
        } catch (PebbleException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return engine;
    }


}
