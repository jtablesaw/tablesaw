package tech.tablesaw.api.ml.clustering;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.util.DoubleArrays;

/**
 * A matrix containing the measures of dissimilarity between all pairs
 * of observations.
 */
public class Proximity {
    
    private final double[][] proximityMatrix;
    
    /**
     * Constructor. 
     * 
     * @param columns the columns of data containing the various measures (i.e., variables)
     * describing each observations
     */
    public Proximity(NumericColumn... columns) {
        double[][] input = DoubleArrays.to2dArray(columns);
        this.proximityMatrix = calcMatrix(input);
    }
    
    private double[][] calcMatrix(double[][] input) {
        
        int n = input.length;
        double[][] proximity = new double[n][];
        
        for (int i = 0; i < n; i++) {
            proximity[i] = new double[i+1];
            for (int j = 0; j < i; j++) {
                // Calculate Euclidean distance between two arrays (i.e., two rows of a Table)
                proximity[i][j] = smile.math.Math.distance(input[i], input[j]);
            }
        }
        
        return proximity;
    }
    
    public double[][] getMatrix() {
        return this.proximityMatrix;
    }

}
