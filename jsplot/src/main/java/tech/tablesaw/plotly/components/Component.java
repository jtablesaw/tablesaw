package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;

abstract class Component {

    final PebbleEngine engine = TemplateUtils.getNewEngine();

    abstract String asJavascript();

    @Override
    public String toString() {
        return asJavascript();
    }
}
