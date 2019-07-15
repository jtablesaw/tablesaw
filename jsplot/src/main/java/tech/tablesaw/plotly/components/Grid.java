package tech.tablesaw.plotly.components;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Grid extends Component {

    public enum RowOrder {
        ENUMERATED,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP;
        
        @Override
        public String toString() {
            return this.name().toLowerCase().replaceAll("_", " ");
        }
        
    }
    public enum Pattern{
        INDEPENDENT,
        COUPLED;
        
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * The number of rows in the grid. If you provide a 2D `subplots` array or a `yaxes` array,
     * its length is used as the default. But it's also possible to have a different length,
     * if you want to leave a row at the end for non-cartesian subplots.
     */
    private final int rows;

    /**
     * Is the first row the top or the bottom? Note that columns are always enumerated from left to right.
     */
    private final RowOrder rowOrder;

    /**
     * If no `subplots`, `xaxes`, or `yaxes` are given but we do have `rows` and `columns`,',
     * we can generate defaults using consecutive axis IDs, in two ways:',
     * '*coupled* gives one x axis per column and one y axis per row.',
     * '*independent* uses a new xy pair for each cell, left-to-right across each row',
     * 'then iterating rows according to `roworder`.
     */
    private final Pattern pattern;
    
    /**
     * The number of columns in the grid. If you provide a 2D `subplots` array,
     * the length of its longest row is used as the default. If you give an `xaxes` array,
     * its length is used as the default. But it's also possible to have a different length,
     * if you want to leave a row at the end for non-cartesian subplots.
     */
    private final int columns;

    /**
     *
     * Horizontal space between grid cells, expressed as a fraction of the total width available to one cell.
     * Defaults to 0.1 for coupled-axes grids and 0.2 for independent grids.
     */
    private final double xGap; // number between or equal to 0 and 1

    /**
     * Vertical space between grid cells, expressed as a fraction of the total height available to one cell.
     * Defaults to 0.1 for coupled-axes grids and 0.3 for independent grids.
     */
    private final double yGap; // number between or equal to 0 and 1

    public Grid(GridBuilder gridBuilder) {
        this.rows = gridBuilder.rows;
        this.columns = gridBuilder.columns;
        this.rowOrder = gridBuilder.rowOrder;
        this.xGap = gridBuilder.xGap;
        this.yGap = gridBuilder.yGap;
        this.pattern = gridBuilder.pattern;
    }


    @Override
    public String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("grid_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("xGap", xGap);
        context.put("yGap", yGap);
        context.put("rows", rows);
        context.put("columns", columns);
        context.put("rowOrder", rowOrder);
        context.put("pattern", pattern);
        return context;
    }

    public static GridBuilder builder() {
        return new GridBuilder();
    }

    public static class GridBuilder {

        private int rows = 80;

        private int columns = 80;

        private double xGap = 100;

        private double yGap = 80;

        private RowOrder rowOrder = RowOrder.TOP_TO_BOTTOM;
        private Pattern pattern = Pattern.COUPLED;

        private GridBuilder() {}

        /**
         * The number of rows in the grid. If you provide a 2D `subplots` array or a `yaxes` array,
         * its length is used as the default. But it's also possible to have a different length,
         * if you want to leave a row at the end for non-cartesian subplots.
         *
         * @param rows an integer greater than or equal to 1
         * @return this GridBuilder
         */
        public GridBuilder rows(int rows) {
            Preconditions.checkArgument(rows >= 1);
            this.rows = rows;
            return this;
        }

        /**
         * The number of columns in the grid. If you provide a 2D `subplots` array,
         * the length of its longest row is used as the default. If you give an `xaxes` array,
         * its length is used as the default. But it's also possible to have a different length,
         * if you want to leave a row at the end for non-cartesian subplots.
         *
         * @param columns an integer greater than or equal to 1
         * @return this GridBuilder
         */
        public GridBuilder columns(int columns) {
            Preconditions.checkArgument(rows >= 1);
            this.columns = columns;
            return this;
        }

        /**
         * Horizontal space between grid cells, expressed as a fraction of the total width available to one cell.
         * Defaults to 0.1 for coupled-axes grids and 0.2 for independent grids.
         *
         * @param xGap  a double &gt;= 0 &amp;&amp; &lt;= 1
         * @return  this GridBuilder
         */
        public GridBuilder xGap(double xGap) {
            Preconditions.checkArgument(xGap >= 0 && xGap <= 1);
            this.xGap = xGap;
            return this;
        }

        /**
         * Vertical space between grid cells, expressed as a fraction of the total height available to one cell.
         * Defaults to 0.1 for coupled-axes grids and 0.3 for independent grids.
         *
         * @param yGap  a double &gt;= 0 &amp;&amp; &lt;= 1
         * @return  this GridBuilder
         */
        public GridBuilder yGap(double yGap) {
            Preconditions.checkArgument(yGap >= 0 && yGap <= 1);
            this.yGap = yGap;
            return this;
        }

        public GridBuilder rowOrder(RowOrder rowOrder) {
            this.rowOrder = rowOrder;
            return this;
        }
        
        /**
         * If no `subplots`, `xaxes`, or `yaxes` are given but we do have `rows` and `columns`,',
         * we can generate defaults using consecutive axis IDs, in two ways:',
         * '*coupled* gives one x axis per column and one y axis per row.',
         * '*independent* uses a new xy pair for each cell, left-to-right across each row',
         * 'then iterating rows according to `roworder`.
         *
         * @param pattern defaults to COUPLED
         * @return  this GridBuilder
         */
        public GridBuilder pattern(Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        public Grid build() {
            return new Grid(this);
        }
    }

}
