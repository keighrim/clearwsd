package edu.colorodo.clear.wsd.classifier;

/**
 * Learning instance represented as a sparse vector with an associated target index and unique identifier.
 *
 * @author jamesgung
 */
public interface StringInstance extends SparseVector {

    /**
     * Unique identifier for this instance.
     */
    int id();

    /**
     * Target index associated with this instance.
     */
    int target();

}
