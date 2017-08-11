package tech.tablesaw.plotting;

/**
 *
 */
public interface Series {

    float[] getX();

    float[] getY();

    float[] getColors();

    float[] getSizes();

    float[] getShapes();

    MarkerType markerType();
}
