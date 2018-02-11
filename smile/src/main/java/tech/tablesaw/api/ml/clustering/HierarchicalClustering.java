package tech.tablesaw.api.ml.clustering;

import smile.clustering.linkage.Linkage;
import tech.tablesaw.api.NumericColumn;

/**
 * Agglomerative Hierarchical Clustering. Hierarchical agglomerative clustering
 * seeks to build a hierarchy of clusters in a bottom up approach: each
 * observation starts in its own cluster, and pairs of clusters are merged as
 * one moves up the hierarchy. The results of hierarchical clustering are
 * usually presented in a dendrogram.
 * <p>
 * In general, the merges are determined in a greedy manner. In order to decide
 * which clusters should be combined, a measure of dissimilarity between sets
 * of observations is required. In most methods of hierarchical clustering,
 * this is achieved by using a linkage criteria which specifies the dissimilarity 
 * of sets as a function of the pairwise distances of observations in the sets. These
 * pairwise distances are specified in a dissimilarity matrix ({@link Proximity}) and the 
 * linkage criteria that is used to determine the set-level dissimilarities is specified 
 * in a <code>Linkage</code> object ({@link LinkageFactory}).
 * <p>
 * Hierarchical clustering has the distinct advantage that any valid measure
 * of distance can be used. In fact, the observations themselves are not
 * required: all that is used is a matrix of distances.
 * 
 * <h2>References</h2>
 * <ol>
 * <li>David Eppstein. Fast hierarchical clustering andv other applications of dynamic closest pairs. SODA 1998.</li>
 * </ol>
 * 
 * @see <a href="https://github.com/haifengl/smile/blob/master/core/src/main/java/smile/clustering/HierarchicalClustering.java">HierarchicalClustering.java</a>
 * 
 * @author Haifeng Li (docs) and Chris Baker (code)
 */
public class HierarchicalClustering {

    private final smile.clustering.HierarchicalClustering hclust;

    /**
     * Constructor.
     * 
     * @param linkageType the type of dissimilarity measure you would like 
     * to use when determining which clusters to merge
     * @param columns the columns of data containing the various measures (i.e., variables)
     * describing each observations
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
     * Cluster a set of observations into a given number of cluster.
     * 
     * @param k the number of groups/clusters that the observations should be
     * broken into
     * 
     * @return the cluster memberships of the observations
     */
    public int[] partition(int k) {
        // given number of desired clusters, returns clusters
        return hclust.partition(k);
    }    
    /**
     * Cluster a set of observations based on a given height to cut a dendrogram
     * tree at.
     * 
     * @param h the height at which the tree is cut
     * 
     * @return the cluster memberships of the observations
     */
    public int[] partition(double h) {
        // given height to cut tree at, returns clusters
        return hclust.partition(h);
    } 

}
