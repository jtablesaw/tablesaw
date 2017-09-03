package tech.tablesaw.api.ml.features;

import smile.projection.PCA;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.util.DoubleArrays;

public class PrincipalComponents {

    private final PCA pca;

    private PrincipalComponents(double[][] data, boolean useCorrelationMatrix) {
        this.pca = new PCA(data, useCorrelationMatrix);
    }

    public static PrincipalComponents create(boolean useCorrelationMatrix, NumericColumn... columns) {
        double[][] data = DoubleArrays.to2dArray(columns);
        return new PrincipalComponents(data, useCorrelationMatrix);
    }

    public double[] getCenter() {
        return pca.getCenter();
    }

    public double[] getCumulativeVarianceProportion() {
        return pca.getCumulativeVarianceProportion();
    }

    public double[] getVarianceProportion() {
        return pca.getVarianceProportion();
    }

    public double[] getVariance() {
        return pca.getVariance();
    }

    public double[] project(double[] x) {
        return pca.project(x);
    }

    public double[][] project(double[][] x) {
        return pca.project(x);
    }

    public double[][] getLoadings() {
        return pca.getLoadings().array();
    }

    public double[][] getProjection() {
        return pca.getProjection().array();
    }

    public PCA setProjection(int p) {
        return pca.setProjection(p);
    }

    public PCA setProjection(double p) {
        return pca.setProjection(p);
    }
}
