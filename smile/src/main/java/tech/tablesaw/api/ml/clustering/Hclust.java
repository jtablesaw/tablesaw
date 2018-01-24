package tech.tablesaw.api.ml.clustering;

import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.Linkage;
import tech.tablesaw.api.NumericColumn;

/**
 * TODO
 */
public class Hclust {
	
    private final HierarchicalClustering hierarchicalClustering;

    /**
     * Constructor.
     * 
     * @param linkageType TODO
     * @param columns TODO
     */
    public Hclust(LinkageFactory.type linkageType, NumericColumn... columns) {
        Proximity proximity = new Proximity(columns);
        Linkage linkage = new LinkageFactory().createLinkage(proximity.getMatrix(), linkageType);
        this.hierarchicalClustering = new HierarchicalClustering(linkage);
    }
    
    public int[][] getTree() {
        return hierarchicalClustering.getTree();
    } 
    public double[] getHeight() {
        return hierarchicalClustering.getHeight();
    }
    
    /**
     * TODO
     * 
     * @param k TODO
     * @return TODO
     */
    public int[] partition(int k) {
        // given number of desired clusters, returns clusters
        return hierarchicalClustering.partition(k);
    }    
    /**
     * TODO
     * 
     * @param h TODO
     * @return TODO
     */
    public int[] partition(double h) {
        // given height to cut tree at, returns clusters
        return hierarchicalClustering.partition(h);
    } 

}
