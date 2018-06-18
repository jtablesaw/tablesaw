package tech.tablesaw.plotly.components;

public class Grid {

    public enum RowOrder {
        ENUMERATED,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP;
    }

    /**
     * The number of rows in the grid. If you provide a 2D `subplots` array or a `yaxes` array,
     * its length is used as the default. But it's also possible to have a different length,
     * if you want to leave a row at the end for non-cartesian subplots.
     */
    private int rows;

    /**
     * Is the first row the top or the bottom? Note that columns are always enumerated from left to right.
     */
    private RowOrder rowOrder = RowOrder.TOP_TO_BOTTOM;

    /**
     * The number of columns in the grid. If you provide a 2D `subplots` array,
     * the length of its longest row is used as the default. If you give an `xaxes` array,
     * its length is used as the default. But it's also possible to have a different length,
     * if you want to leave a row at the end for non-cartesian subplots.
     */
    private int columns;

    /**
     *
     * Horizontal space between grid cells, expressed as a fraction of the total width available to one cell.
     * Defaults to 0.1 for coupled-axes grids and 0.2 for independent grids.
     */
    private double xGap; // number between or equal to 0 and 1

    /**
     * Vertical space between grid cells, expressed as a fraction of the total height available to one cell.
     * Defaults to 0.1 for coupled-axes grids and 0.3 for independent grids.
     */
    private double yGap; // number between or equal to 0 and 1
}
