package tech.tablesaw.api.ml.clustering;

import smile.clustering.linkage.Linkage;
import tech.tablesaw.api.NumericColumn;

/**
 * TODO
 */
public class HierarchicalClustering {

    private final smile.clustering.HierarchicalClustering hclust;

    /**
     * Constructor.
     * 
     * @param linkageType TODO
     * @param columns TODO
     */
    public HierarchicalClustering(LinkageFactory.type linkageType, NumericColumn... columns) {
        Proximity proximity = new Proximity(columns);
        Linkage linkage = new LinkageFactory().createLinkage(proximity.getMatrix(), linkageType);
        this.hclust = new smile.clustering.HierarchicalClustering(linkage);
    }
    
    public int[][] getTree() {
        return hclust.getTree();
    } 
    public double[] getHeight() {
        return hclust.getHeight();
    }
    
    /**
     * TODO
     * 
     * @param k TODO
     * @return TODO
     */
    public int[] partition(int k) {
        // given number of desired clusters, returns clusters
        return hclust.partition(k);
    }    
    /**
     * TODO
     * 
     * @param h TODO
     * @return TODO
     */
    public int[] partition(double h) {
        // given height to cut tree at, returns clusters
        return hclust.partition(h);
    } 

}
