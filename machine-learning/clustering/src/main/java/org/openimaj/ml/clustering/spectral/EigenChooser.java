package org.openimaj.ml.clustering.spectral;

import java.util.Iterator;

import org.openimaj.util.pair.DoubleObjectPair;

import ch.akuhn.matrix.SparseMatrix;
import ch.akuhn.matrix.Vector;
import ch.akuhn.matrix.eigenvalues.Eigenvalues;

/**
 * Method which makes a decision on how many eigen vectors to select
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 */
public abstract class EigenChooser{
	/**
	 * @param vals
	 * @param totalEigenVectors the total number of eigen vectors
	 * @return count the eigen vectors
	 */
	public abstract int nEigenVectors(Iterator<DoubleObjectPair<Vector>> vals, int totalEigenVectors);

	/**
	 * Make a coarse decision of the number of eigen vectors to extract in the first place
	 * with the knowledge of the eigen values that will likely be important
	 * @param laplacian the matrix to be decomposed
	 * @return the prepared eigen values
	 */
	public abstract Eigenvalues prepare(SparseMatrix laplacian) ;
}