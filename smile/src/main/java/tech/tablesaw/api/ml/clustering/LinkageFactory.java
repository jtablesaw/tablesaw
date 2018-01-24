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
 * TODO
 */
public class LinkageFactory {
    
    
    /**
     * TODO
     */
    public enum type {
        COMPLETE, SINGLE, UPGMA, UPGMC, WARD, WPGMA, WPGMC
    }
    
    /**
     * TODO
     * 
     * @param proximity TODO
     * @param linkageType TODO
     * @return TODO
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
