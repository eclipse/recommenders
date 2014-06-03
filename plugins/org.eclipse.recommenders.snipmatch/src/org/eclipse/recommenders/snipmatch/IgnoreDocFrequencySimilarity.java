/**
 * Copyright (c) 2010, 2015 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.snipmatch;

import org.apache.lucene.search.DefaultSimilarity;

public class IgnoreDocFrequencySimilarity extends DefaultSimilarity {

    @Override
    public float idf(int docFreq, int numDocs) {
        return 1.0f;
    }

    @Override
    public float tf(float freq) {
        return 1.0f;
    }

}
