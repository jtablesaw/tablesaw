package tech.tablesaw.api.ml.clustering;

import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;
import smile.clustering.linkage.SingleLinkage;
import smile.clustering.linkage.UPGMALinkage;
import smile.clustering.linkage.UPGMCLinkage;
import smile.clustering.linkage.WPGMALinkage;
import smile.clustering.linkage.WPGMCLinkage;
import smile.clustering.linkage.WardLinkage;

/**
 * Cluster dissimilarity measures. An agglomerative hierarchical clustering
 * builds the hierarchy from the individual elements by progressively merging
 * clusters. The linkage criteria determines the distance between clusters
 * (i.e. sets of observations) based on as a pairwise distance function between
 * observations. Some commonly used linkage criteria are:
 * <ul>
 * <li> Maximum or complete linkage clustering (COMPLETE) </li>
 * <li> Minimum or single-linkage clustering (SINGLE) </li>
 * <li> Mean or average linkage clustering (UPGMA) </li>
 * <li> Unweighted Pair Group Method using Centroids (UPCMA). Also known as centroid linkage. </li>
 * <li> Weighted Pair Group Method with Arithmetic mean (WPGMA) </li>
 * <li> Weighted Pair Group Method using Centroids (WPGMC). Also known as median linkage. </li>
 * <li> Ward's linkage (WARD) </li>
 * </ul>
 * 
 * @author Haifeng Li (docs) and Chris Baker (code)
 * 
 * @see <a href="https://github.com/haifengl/smile/blob/355198c504f1c45652542da6580a3041799cb0f8/core/src/main/java/smile/clustering/linkage/package-info.java">linkage/package-info.java</a>
 */
public class LinkageFactory {
    
    
    /**
     * Cluster dissimilarity measure
     */
    public enum type {
        COMPLETE, SINGLE, UPGMA, UPGMC, WARD, WPGMA, WPGMC
    }
    
    /**
     * Create a Linkage object.
     * 
     * @param proximity the proximity matrix to store the measures of
     * dissimilarity between each pair of observations/rows
     * @param linkageType the type of dissimilarity measure you would like 
     * to use when determining which clusters to merge
     * 
     * @return a measure of dissimilarity between clusters
     * 
     * @see <a href="https://github.com/haifengl/smile/blob/355198c504f1c45652542da6580a3041799cb0f8/core/src/main/java/smile/clustering/linkage/Linkage.java">Linkage.java</a>
     */
    public Linkage createLinkage(double[][] proximity, type linkageType) {
        
        Linkage link;
        switch (linkageType) {
            case WARD:
                link = new WardLinkage(proximity);
                break;
            case COMPLETE:
                link = new CompleteLinkage(proximity);
                break;
            case SINGLE:
                link = new SingleLinkage(proximity);
                break;
            case UPGMA:
                link = new UPGMALinkage(proximity);
                break;
            case UPGMC:
                link = new UPGMCLinkage(proximity);
                break;
            case WPGMA:
                link = new WPGMALinkage(proximity);
                break;
            case WPGMC:
                link = new WPGMCLinkage(proximity);
                break;
            default: 
                link = new SingleLinkage(proximity);
                break;
        }
        
        return link;
    }

}
